/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.editor.ui.editor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.core.extensions.indexOrNull
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.api.model.EditorApiEvent
import com.blacksquircle.ui.feature.editor.api.navigation.CloseFileDialog
import com.blacksquircle.ui.feature.editor.api.navigation.ConfirmExitDialog
import com.blacksquircle.ui.feature.editor.api.navigation.ForceSyntaxDialog
import com.blacksquircle.ui.feature.editor.api.navigation.GoToLineDialog
import com.blacksquircle.ui.feature.editor.api.navigation.InsertColorDialog
import com.blacksquircle.ui.feature.editor.data.mapper.DocumentMapper
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorCommand
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorSettings
import com.blacksquircle.ui.feature.editor.ui.editor.model.ErrorAction
import com.blacksquircle.ui.feature.editor.ui.editor.model.ErrorState
import com.blacksquircle.ui.feature.editor.ui.editor.model.SearchState
import com.blacksquircle.ui.feature.editor.ui.editor.view.selectionEnd
import com.blacksquircle.ui.feature.editor.ui.editor.view.selectionStart
import com.blacksquircle.ui.feature.explorer.api.navigation.StorageDeniedDialog
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.git.api.exception.InvalidCredentialsException
import com.blacksquircle.ui.feature.git.api.exception.RepositoryNotFoundException
import com.blacksquircle.ui.feature.git.api.exception.UnsupportedFilesystemException
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.git.api.navigation.GitDialog
import com.blacksquircle.ui.feature.settings.api.navigation.HeaderListScreen
import com.blacksquircle.ui.feature.shortcuts.api.extensions.forAction
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.filesystem.base.model.FileModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import com.blacksquircle.ui.ds.R as UiR

internal class EditorViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val documentRepository: DocumentRepository,
    private val editorInteractor: EditorInteractor,
    private val fontsInteractor: FontsInteractor,
    private val gitInteractor: GitInteractor,
    private val shortcutsInteractor: ShortcutsInteractor,
    private val languageInteractor: LanguageInteractor,
) : ViewModel() {

    private val _viewState = MutableStateFlow(EditorViewState())
    val viewState: StateFlow<EditorViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var documents = emptyList<DocumentState>()
    private var selectedPosition = -1
    private var settings = EditorSettings()
    private var currentJob: Job? = null

    init {
        loadDocuments()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            if (settingsManager.confirmExit) {
                val screen = ConfirmExitDialog
                _viewEvent.send(ViewEvent.Navigation(screen))
            } else {
                _viewEvent.send(ViewEvent.PopBackStack)
            }
        }
    }

    fun onNewFileClicked() {
        viewModelScope.launch {
            _viewEvent.send(EditorViewEvent.CreateFileContract)
        }
    }

    fun onOpenFileClicked() {
        viewModelScope.launch {
            _viewEvent.send(EditorViewEvent.OpenFileContract)
        }
    }

    fun onSaveFileClicked() {
        viewModelScope.launch {
            try {
                if (selectedPosition !in documents.indices) {
                    return@launch
                }

                val document = documents[selectedPosition].document
                val content = documents[selectedPosition].content ?: return@launch

                val updatedDocument = document.copy(
                    modified = false,
                    scrollX = content.scrollX,
                    scrollY = content.scrollY,
                    selectionStart = content.selectionStart,
                    selectionEnd = content.selectionEnd,
                )

                documents = documents.mapSelected { state ->
                    state.copy(document = updatedDocument)
                }
                _viewState.update {
                    it.copy(documents = documents)
                }

                documentRepository.saveDocument(updatedDocument, content)

                val message = stringProvider.getString(R.string.message_saved)
                _viewEvent.send(ViewEvent.Toast(message))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onSaveFileAsClicked() {
        viewModelScope.launch {
            if (selectedPosition !in documents.indices) {
                return@launch
            }
            val document = documents[selectedPosition].document
            _viewEvent.send(EditorViewEvent.SaveAsFileContract(document.name))
        }
    }

    fun onSaveFileSelected(fileUri: Uri) {
        viewModelScope.launch {
            try {
                if (selectedPosition !in documents.indices) {
                    return@launch
                }

                val document = documents[selectedPosition].document
                val content = documents[selectedPosition].content ?: return@launch

                documentRepository.saveExternal(document, content, fileUri)

                val message = stringProvider.getString(R.string.message_saved)
                _viewEvent.send(ViewEvent.Toast(message))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onRefreshFileClicked() {
        viewModelScope.launch {
            if (selectedPosition !in documents.indices) {
                return@launch
            }
            val document = documents[selectedPosition].document
            if (document.modified) {
                documents = documents.mapSelected { state ->
                    state.copy(document = document.copy(modified = false))
                }
                _viewState.update {
                    it.copy(documents = documents)
                }
            }
            documentRepository.refreshDocument(document)
            loadDocument(document, fromUser = false)
        }
    }

    fun onCloseFileClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }
        val document = documents[selectedPosition].document
        onCloseClicked(document, fromUser = true)
    }

    fun onContentChanged() {
        viewModelScope.launch {
            try {
                if (selectedPosition !in documents.indices) {
                    return@launch
                }

                val document = documents[selectedPosition].document
                val content = documents[selectedPosition].content ?: return@launch

                if (document.modified) {
                    _viewState.update {
                        it.copy(
                            canUndo = content.canUndo(),
                            canRedo = content.canRedo(),
                        )
                    }
                } else {
                    val modified = true
                    val updatedDocument = document.copy(
                        modified = modified,
                    )

                    documents = documents.mapSelected { state ->
                        state.copy(document = updatedDocument)
                    }
                    _viewState.update {
                        it.copy(
                            documents = documents,
                            canUndo = content.canUndo(),
                            canRedo = content.canRedo(),
                        )
                    }

                    documentRepository.changeModified(updatedDocument, modified)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onShortcutPressed(ctrl: Boolean, shift: Boolean, alt: Boolean, keyCode: Int) {
        when (settings.keybindings.forAction(ctrl, shift, alt, keyCode)) {
            Shortcut.NEW -> onNewFileClicked()
            Shortcut.OPEN -> onOpenFileClicked()
            Shortcut.SAVE -> onSaveFileClicked()
            Shortcut.SAVE_AS -> onSaveFileAsClicked()
            Shortcut.CLOSE -> onCloseFileClicked()
            Shortcut.CUT -> onCutClicked()
            Shortcut.COPY -> onCopyClicked()
            Shortcut.PASTE -> onPasteClicked()
            Shortcut.SELECT_ALL -> onSelectAllClicked()
            Shortcut.SELECT_LINE -> onSelectLineClicked()
            Shortcut.DELETE_LINE -> onDeleteLineClicked()
            Shortcut.DUPLICATE_LINE -> onDuplicateLineClicked()
            Shortcut.TOGGLE_CASE -> onToggleCaseClicked()
            Shortcut.PREV_WORD -> onPreviousWordClicked()
            Shortcut.NEXT_WORD -> onNextWordClicked()
            Shortcut.START_OF_LINE -> onStartOfLineClicked()
            Shortcut.END_OF_LINE -> onEndOfLineClicked()
            Shortcut.UNDO -> onUndoClicked()
            Shortcut.REDO -> onRedoClicked()
            Shortcut.FIND -> onToggleFindClicked()
            Shortcut.REPLACE -> onToggleReplaceClicked()
            Shortcut.GOTO_LINE -> onGoToLineClicked()
            Shortcut.FORCE_SYNTAX -> onForceSyntaxClicked()
            Shortcut.INSERT_COLOR -> onInsertColorClicked()
            else -> Unit
        }
    }

    fun onCutClicked() {
        viewModelScope.launch {
            if (settings.readOnly) return@launch
            val event = EditorCommand.Cut
            _viewEvent.send(EditorViewEvent.Command(event))
        }
    }

    fun onCopyClicked() {
        viewModelScope.launch {
            val command = EditorCommand.Copy
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onPasteClicked() {
        viewModelScope.launch {
            if (settings.readOnly) return@launch
            val command = EditorCommand.Paste
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onSelectAllClicked() {
        viewModelScope.launch {
            val command = EditorCommand.SelectAll
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onSelectLineClicked() {
        viewModelScope.launch {
            val command = EditorCommand.SelectLine
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onDeleteLineClicked() {
        viewModelScope.launch {
            if (settings.readOnly) return@launch
            val command = EditorCommand.DeleteLine
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onDuplicateLineClicked() {
        viewModelScope.launch {
            if (settings.readOnly) return@launch
            val command = EditorCommand.DuplicateLine
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    private fun onToggleCaseClicked() {
        viewModelScope.launch {
            if (settings.readOnly) return@launch
            val command = EditorCommand.ToggleCase
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    private fun onPreviousWordClicked() {
        viewModelScope.launch {
            val command = EditorCommand.PreviousWord
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    private fun onNextWordClicked() {
        viewModelScope.launch {
            val command = EditorCommand.NextWord
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    private fun onStartOfLineClicked() {
        viewModelScope.launch {
            val command = EditorCommand.StartOfLine
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    private fun onEndOfLineClicked() {
        viewModelScope.launch {
            val command = EditorCommand.EndOfLine
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onUndoClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }

        if (settings.readOnly) return
        val content = documents[selectedPosition].content ?: return
        content.undo()

        _viewState.update {
            it.copy(
                canUndo = content.canUndo(),
                canRedo = content.canRedo(),
            )
        }
    }

    fun onRedoClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }

        if (settings.readOnly) return
        val content = documents[selectedPosition].content ?: return
        content.redo()

        _viewState.update {
            it.copy(
                canUndo = content.canUndo(),
                canRedo = content.canRedo(),
            )
        }
    }

    fun onForceSyntaxClicked() {
        viewModelScope.launch {
            if (selectedPosition !in documents.indices) {
                return@launch
            }
            val document = documents[selectedPosition].document
            val screen = ForceSyntaxDialog(document.language)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onLanguageChanged(language: String) {
        viewModelScope.launch {
            try {
                if (selectedPosition !in documents.indices) {
                    return@launch
                }

                val document = documents[selectedPosition].document.copy(
                    language = language,
                )

                documents = documents.mapSelected { state ->
                    state.copy(document = document)
                }
                _viewState.update {
                    it.copy(documents = documents)
                }

                documentRepository.changeLanguage(document, language)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onInsertColorClicked() {
        viewModelScope.launch {
            if (selectedPosition !in documents.indices) {
                return@launch
            }
            val screen = InsertColorDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onColorSelected(color: Int) {
        viewModelScope.launch {
            val command = EditorCommand.Insert(color.toHexString())
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onToggleFindClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }

        documents = documents.mapSelected { state ->
            val searchState = if (state.searchState == null) SearchState() else null
            state.copy(searchState = searchState)
        }

        _viewState.update {
            it.copy(documents = documents)
        }

        viewModelScope.launch {
            val command = EditorCommand.StopSearch
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onToggleReplaceClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }

        documents = documents.mapSelected { state ->
            val searchState = when {
                state.searchState == null -> SearchState(replaceShown = true)
                state.searchState.replaceShown -> state.searchState.copy(replaceShown = false)
                !state.searchState.replaceShown -> state.searchState.copy(replaceShown = true)
                else -> state.searchState
            }
            state.copy(searchState = searchState)
        }

        _viewState.update {
            it.copy(documents = documents)
        }
    }

    fun onFindTextChanged(text: String) {
        if (selectedPosition !in documents.indices) {
            return
        }

        val currentSearch = documents[selectedPosition].searchState ?: return
        val updatedSearch = currentSearch.copy(findText = text)

        documents = documents.mapSelected { state ->
            state.copy(searchState = updatedSearch)
        }

        _viewState.update {
            it.copy(documents = documents)
        }

        viewModelScope.launch {
            val command = EditorCommand.Find(updatedSearch)
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onReplaceTextChanged(text: String) {
        if (selectedPosition !in documents.indices) {
            return
        }

        documents = documents.mapSelected { state ->
            val searchState = state.searchState?.copy(replaceText = text)
            state.copy(searchState = searchState)
        }

        _viewState.update {
            it.copy(documents = documents)
        }
    }

    fun onRegexClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }

        val currentSearch = documents[selectedPosition].searchState ?: return
        val updatedSearch = currentSearch.copy(regex = !currentSearch.regex)

        documents = documents.mapSelected { state ->
            state.copy(searchState = updatedSearch)
        }

        _viewState.update {
            it.copy(documents = documents)
        }

        viewModelScope.launch {
            val command = EditorCommand.Find(updatedSearch)
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onMatchCaseClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }

        val currentSearch = documents[selectedPosition].searchState ?: return
        val updatedSearch = currentSearch.copy(matchCase = !currentSearch.matchCase)

        documents = documents.mapSelected { state ->
            state.copy(searchState = updatedSearch)
        }

        _viewState.update {
            it.copy(documents = documents)
        }

        viewModelScope.launch {
            val command = EditorCommand.Find(updatedSearch)
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onWordsOnlyClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }

        val currentSearch = documents[selectedPosition].searchState ?: return
        val updatedSearch = currentSearch.copy(wordsOnly = !currentSearch.wordsOnly)

        documents = documents.mapSelected { state ->
            state.copy(searchState = updatedSearch)
        }

        _viewState.update {
            it.copy(documents = documents)
        }

        viewModelScope.launch {
            val command = EditorCommand.Find(updatedSearch)
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onPreviousMatchClicked() {
        viewModelScope.launch {
            val command = EditorCommand.PreviousMatch
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onNextMatchClicked() {
        viewModelScope.launch {
            val command = EditorCommand.NextMatch
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onReplaceMatchClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }
        val searchState = documents[selectedPosition].searchState ?: return

        viewModelScope.launch {
            val command = EditorCommand.Replace(searchState.replaceText)
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onReplaceAllClicked() {
        if (selectedPosition !in documents.indices) {
            return
        }
        val searchState = documents[selectedPosition].searchState ?: return

        viewModelScope.launch {
            val command = EditorCommand.ReplaceAll(searchState.replaceText)
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    private fun onGoToLineClicked() {
        viewModelScope.launch {
            if (selectedPosition !in documents.indices) {
                return@launch
            }
            val screen = GoToLineDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onLineSelected(lineNumber: Int) {
        viewModelScope.launch {
            val command = EditorCommand.GoToLine(lineNumber)
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            val screen = HeaderListScreen
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onGitClicked() {
        viewModelScope.launch {
            if (selectedPosition !in documents.indices) {
                return@launch
            }
            try {
                val document = documents[selectedPosition].document
                val fileModel = DocumentMapper.toModel(document)
                val repository = gitInteractor.getRepoPath(fileModel)
                val screen = GitDialog(repository)
                _viewEvent.send(ViewEvent.Navigation(screen))
            } catch (e: InvalidCredentialsException) {
                val message = stringProvider.getString(R.string.message_git_invalid_credentials)
                _viewEvent.send(ViewEvent.Toast(message))
            } catch (e: RepositoryNotFoundException) {
                val message = stringProvider.getString(R.string.message_git_repository_not_found)
                _viewEvent.send(ViewEvent.Toast(message))
            } catch (e: UnsupportedFilesystemException) {
                val message = stringProvider.getString(R.string.message_git_unsupported_filesystem)
                _viewEvent.send(ViewEvent.Toast(message))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onDocumentClicked(document: DocumentModel) {
        loadDocument(document, fromUser = true)
    }

    fun onDocumentMoved(from: Int, to: Int) {
        viewModelScope.launch {
            try {
                if (from !in documents.indices || to !in documents.indices || from == to) {
                    return@launch
                }

                val documentList = documents.toMutableList()
                val documentFrom = documentList[from].document
                val documentTo = documentList[to].document
                documentList.add(to, documentList.removeAt(from))

                documents = documentList.mapIndexed { index, state ->
                    if (state.document.position != index) {
                        state.copy(document = state.document.copy(position = index))
                    } else {
                        state
                    }
                }

                when (selectedPosition) {
                    /** Reorder from right to left */
                    in to until from -> selectedPosition += 1
                    /** Reorder from left to right */
                    in (from + 1)..to -> selectedPosition -= 1
                    /** Reorder selected item */
                    from -> selectedPosition = to
                }

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                    )
                }

                documentRepository.reorderDocuments(documentFrom, documentTo)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    @Suppress("KotlinConstantConditions")
    fun onCloseClicked(document: DocumentModel, fromUser: Boolean = true) {
        viewModelScope.launch {
            try {
                if (document.modified && fromUser) {
                    val screen = CloseFileDialog(
                        fileUuid = document.uuid,
                        fileName = document.name,
                    )
                    _viewEvent.send(ViewEvent.Navigation(screen))
                    return@launch
                }
                /** Calculate new position */
                val removedPosition = document.position
                val currentPosition = when {
                    removedPosition == selectedPosition -> when {
                        removedPosition - 1 > -1 -> removedPosition - 1
                        removedPosition + 1 < documents.size -> removedPosition
                        else -> -1
                    }

                    removedPosition < selectedPosition -> selectedPosition - 1
                    removedPosition > selectedPosition -> selectedPosition
                    else -> -1
                }
                val reloadFile = removedPosition == selectedPosition

                documents = documents.mapNotNull { state ->
                    when {
                        /** Remove document */
                        state.document.position == removedPosition -> {
                            null
                        }
                        /** Shift to left */
                        state.document.position > removedPosition -> {
                            state.copy(
                                document = state.document.copy(
                                    position = state.document.position - 1
                                )
                            )
                        }
                        /** Nothing is changed */
                        else -> {
                            state
                        }
                    }
                }
                selectedPosition = currentPosition

                val hasMoreFiles = reloadFile && documents.isNotEmpty()

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        canUndo = if (reloadFile) false else it.canUndo,
                        canRedo = if (reloadFile) false else it.canRedo,
                        isLoading = hasMoreFiles,
                    )
                }

                settingsManager.selectedUuid = documents
                    .getOrNull(currentPosition)
                    ?.document
                    ?.uuid
                    .orEmpty()

                documentRepository.closeDocument(document)

                if (reloadFile) {
                    /** If selected file is still loading, cancel request */
                    currentJob?.cancel()

                    /** Load new file content */
                    if (hasMoreFiles) {
                        val selectedDocument = documents[selectedPosition].document
                        loadDocument(selectedDocument, fromUser = false)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onCloseOthersClicked(document: DocumentModel) {
        viewModelScope.launch {
            try {
                documents = documents.mapNotNull { state ->
                    if (state.document.uuid == document.uuid) {
                        state.copy(document = document.copy(position = 0))
                    } else {
                        null
                    }
                }
                selectedPosition = 0

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                    )
                }

                documentRepository.closeOtherDocuments(document)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onCloseAllClicked() {
        viewModelScope.launch {
            try {
                currentJob?.cancel()

                documents = emptyList()
                selectedPosition = -1

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        canUndo = false,
                        canRedo = false,
                        isLoading = false,
                    )
                }

                documentRepository.closeAllDocuments()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onCloseModifiedClicked(fileUuid: String) {
        val document = documents.find { it.document.uuid == fileUuid }
        if (document != null) {
            onCloseClicked(document.document, fromUser = false)
        }
    }

    fun onErrorActionClicked(errorAction: ErrorAction) {
        viewModelScope.launch {
            when (errorAction) {
                ErrorAction.REQUEST_PERMISSIONS -> {
                    val screen = StorageDeniedDialog
                    _viewEvent.send(ViewEvent.Navigation(screen))
                }

                ErrorAction.CLOSE_DOCUMENT -> onCloseFileClicked()
                ErrorAction.UNDEFINED -> Unit
            }
        }
    }

    fun onExtraKeyClicked(key: Char) {
        viewModelScope.launch {
            val command = if (key == '\t') {
                EditorCommand.IndentOrTab
            } else {
                EditorCommand.Insert(key.toString())
            }
            _viewEvent.send(EditorViewEvent.Command(command))
        }
    }

    fun onExtraOptionsClicked() {
        _viewState.update {
            it.copy(showExtraKeys = !it.showExtraKeys)
        }
    }

    fun onResumed() {
        viewModelScope.launch {
            try {
                val currentSettings = settings
                val latestSettings = loadSettings()
                if (currentSettings != latestSettings) {
                    settings = latestSettings

                    _viewState.update {
                        it.copy(settings = settings)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onPaused() {
        viewModelScope.launch {
            try {
                if (selectedPosition !in documents.indices) {
                    return@launch
                }

                val document = documents[selectedPosition].document
                val content = documents[selectedPosition].content ?: return@launch

                val autoSaveFiles = settingsManager.autoSaveFiles
                val updatedDocument = document.copy(
                    modified = if (autoSaveFiles) false else document.modified,
                    scrollX = content.scrollX,
                    scrollY = content.scrollY,
                    selectionStart = content.selectionStart,
                    selectionEnd = content.selectionEnd,
                )

                documents = documents.mapSelected { state ->
                    state.copy(document = updatedDocument)
                }
                _viewState.update {
                    it.copy(documents = documents)
                }

                if (autoSaveFiles) {
                    documentRepository.saveDocument(updatedDocument, content)
                } else {
                    documentRepository.cacheDocument(updatedDocument, content)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onFileOpened(fileUri: Uri) {
        viewModelScope.launch {
            val document = documentRepository.openExternal(fileUri, documents.size)
            loadDocument(document, fromUser = true)
        }
    }

    private fun onFileOpened(fileModel: FileModel) {
        val document = DocumentMapper.toModel(fileModel, documents.size)
        loadDocument(document, fromUser = true)
    }

    private fun loadDocument(document: DocumentModel, fromUser: Boolean) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                /** Check if [document] is already added to tabs */
                val existingIndex = documents.indexOf { it.document.fileUri == document.fileUri }
                if (existingIndex == selectedPosition && selectedPosition != -1 && fromUser) {
                    return@launch
                }

                documents = documents.mapSelected { state ->
                    /** If it's user-initiated action, save cache and clear tab's content */
                    if (fromUser) {
                        val autoSaveFiles = settingsManager.autoSaveFiles
                        val updatedDocument = state.document.copy(
                            modified = if (autoSaveFiles) false else state.document.modified,
                            scrollX = state.content?.scrollX
                                ?: state.document.scrollX,
                            scrollY = state.content?.scrollY
                                ?: state.document.scrollY,
                            selectionStart = state.content?.selectionStart
                                ?: state.document.selectionStart,
                            selectionEnd = state.content?.selectionEnd
                                ?: state.document.selectionEnd,
                        )
                        if (state.content != null) {
                            if (autoSaveFiles) {
                                documentRepository.saveDocument(updatedDocument, state.content)
                            } else {
                                documentRepository.cacheDocument(updatedDocument, state.content)
                            }
                        }
                        state.copy(
                            document = updatedDocument,
                            content = null,
                        )
                    } else {
                        state
                    }
                }

                if (existingIndex != -1) {
                    /** Select existing document */
                    selectedPosition = existingIndex
                } else {
                    /** Create new document */
                    documents = documents + DocumentState(document)
                    selectedPosition = documents.size - 1
                }

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        canUndo = false,
                        canRedo = false,
                        isLoading = true,
                    )
                }

                /** Can't use [document] here, it might have different UUID with same file uri */
                val documentState = documents[selectedPosition]
                val content = documentRepository.loadDocument(documentState.document)
                ensureActive()

                documents = documents.mapSelected {
                    it.copy(
                        content = content,
                        errorState = null,
                    )
                }

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        canUndo = content.canUndo(),
                        canRedo = content.canRedo(),
                        isLoading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Timber.e(e, e.message)

                /** Clear content and show error */
                documents = documents.mapSelected {
                    it.copy(
                        content = null,
                        errorState = errorState(e),
                    )
                }

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            try {
                settings = loadSettings()

                val documentList = documentRepository.loadDocuments()
                languageInteractor.loadGrammars()

                documents = documentList.map { document -> DocumentState(document) }
                selectedPosition = if (documentList.isNotEmpty()) {
                    documentList.indexOrNull { it.uuid == settingsManager.selectedUuid } ?: 0
                } else {
                    -1
                }

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        settings = settings,
                        isLoading = documents.isNotEmpty(),
                    )
                }

                if (selectedPosition in documents.indices) {
                    val document = documents[selectedPosition].document
                    loadDocument(document, fromUser = false)
                }

                editorInteractor.eventBus.collect { event ->
                    when (event) {
                        is EditorApiEvent.OpenFile -> onFileOpened(event.fileModel)
                        is EditorApiEvent.OpenFileUri -> onFileOpened(event.fileUri)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun errorState(e: Throwable): ErrorState = when (e) {
        is PermissionException -> ErrorState(
            icon = UiR.drawable.ic_file_error,
            title = stringProvider.getString(UiR.string.message_access_denied),
            subtitle = stringProvider.getString(UiR.string.message_access_required),
            action = ErrorAction.REQUEST_PERMISSIONS,
        )

        else -> ErrorState(
            icon = UiR.drawable.ic_file_error,
            title = stringProvider.getString(UiR.string.common_error_occurred),
            subtitle = e.message.orEmpty(),
            action = ErrorAction.CLOSE_DOCUMENT,
        )
    }

    private inline fun List<DocumentState>.mapSelected(
        predicate: (DocumentState) -> DocumentState
    ): List<DocumentState> = mapIndexed { index, state ->
        if (index == selectedPosition) {
            predicate(state)
        } else {
            state
        }
    }

    private suspend fun loadSettings(): EditorSettings = EditorSettings(
        fontSize = settingsManager.fontSize.toFloat(),
        fontType = fontsInteractor.loadFont(settingsManager.fontType),
        wordWrap = settingsManager.wordWrap,
        codeCompletion = settingsManager.codeCompletion,
        pinchZoom = settingsManager.pinchZoom,
        lineNumbers = settingsManager.lineNumbers,
        highlightCurrentLine = settingsManager.highlightCurrentLine,
        highlightMatchingDelimiters = settingsManager.highlightMatchingDelimiters,
        highlightCodeBlocks = settingsManager.highlightCodeBlocks,
        showInvisibleChars = settingsManager.showInvisibleChars,
        readOnly = settingsManager.readOnly,
        extendedKeyboard = settingsManager.extendedKeyboard,
        keyboardPreset = settingsManager.keyboardPreset.toMutableList().distinct(),
        softKeyboard = settingsManager.softKeyboard,
        autoIndentation = settingsManager.autoIndentation,
        autoClosePairs = settingsManager.autoClosePairs,
        useSpacesInsteadOfTabs = settingsManager.useSpacesInsteadOfTabs,
        tabWidth = settingsManager.tabWidth,
        keybindings = shortcutsInteractor.loadShortcuts(),
    )

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<EditorViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}