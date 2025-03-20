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

package com.blacksquircle.ui.feature.editor.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.api.factory.LanguageInteractor
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.api.model.EditorApiEvent
import com.blacksquircle.ui.feature.editor.data.mapper.DocumentMapper
import com.blacksquircle.ui.feature.editor.data.model.EditorSettings
import com.blacksquircle.ui.feature.editor.data.model.KeyModel
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorViewEvent
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorViewState
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.fragment.model.ErrorAction
import com.blacksquircle.ui.feature.editor.ui.fragment.model.ErrorState
import com.blacksquircle.ui.feature.editor.ui.fragment.view.selectionEnd
import com.blacksquircle.ui.feature.editor.ui.fragment.view.selectionStart
import com.blacksquircle.ui.feature.editor.ui.navigation.EditorScreen
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.feature.themes.api.interactor.ThemesInteractor
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
    private val themesInteractor: ThemesInteractor,
    private val fontsInteractor: FontsInteractor,
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

    fun onDrawerClicked() {
        viewModelScope.launch {
            val screen = Screen.Explorer
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            if (settingsManager.confirmExit) {
                val screen = EditorScreen.ConfirmExit
                _viewEvent.send(ViewEvent.Navigation(screen))
            } else {
                _viewEvent.send(ViewEvent.PopBackStack())
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
                if (documents.isEmpty()) {
                    return@launch
                }

                val document = documents[selectedPosition].document
                val content = documents[selectedPosition].content

                if (content != null) {
                    documentRepository.saveDocument(document, content)

                    val message = stringProvider.getString(R.string.message_saved)
                    _viewEvent.send(ViewEvent.Toast(message))
                }
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
            if (documents.isEmpty()) {
                return@launch
            }
            val selectedDocument = documents[selectedPosition].document
            _viewEvent.send(EditorViewEvent.SaveAsFileContract(selectedDocument.name))
        }
    }

    fun onSaveFileSelected(fileUri: Uri) {
        viewModelScope.launch {
            try {
                if (documents.isEmpty()) {
                    return@launch
                }

                val document = documents[selectedPosition].document
                val content = documents[selectedPosition].content

                if (content != null) {
                    documentRepository.saveExternal(document, content, fileUri)

                    val message = stringProvider.getString(R.string.message_saved)
                    _viewEvent.send(ViewEvent.Toast(message))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onCloseFileClicked() {
        if (documents.isEmpty()) {
            return
        }
        val selectedDocument = documents[selectedPosition].document
        onCloseClicked(selectedDocument)
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            val screen = Screen.Settings
            _viewEvent.send(ViewEvent.Navigation(screen))
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
    fun onCloseClicked(document: DocumentModel) {
        viewModelScope.launch {
            try {
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

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                    )
                }

                settingsManager.selectedUuid = documents
                    .getOrNull(currentPosition)
                    ?.document?.uuid.orEmpty()

                documentRepository.closeDocument(document)

                if (reloadFile) {
                    /** If selected file is still loading, cancel request */
                    currentJob?.cancel()

                    /** Load new file content */
                    if (documents.isNotEmpty()) {
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

    fun onErrorActionClicked(errorAction: ErrorAction) {
        viewModelScope.launch {
            when (errorAction) {
                ErrorAction.CLOSE_DOCUMENT -> onCloseFileClicked()
                ErrorAction.UNDEFINED -> Unit
            }
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
                if (documents.isEmpty()) {
                    return@launch
                }

                val document = documents[selectedPosition].document
                val content = documents[selectedPosition].content

                if (content != null) {
                    documentRepository.cacheDocument(document, content)
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
                        val selectedDocument = state.document.copy(
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
                            if (settingsManager.autoSaveFiles) {
                                documentRepository.saveDocument(selectedDocument, state.content)
                            } else {
                                documentRepository.cacheDocument(selectedDocument, state.content)
                            }
                        }
                        state.copy(
                            document = selectedDocument,
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
                        isLoading = true,
                    )
                }

                /** Can't use [document] here, it might have different UUID with same file uri */
                val documentState = documents[selectedPosition]
                val content = documentRepository.loadDocument(documentState.document)
                languageInteractor.loadGrammar(documentState.document.language)
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
                        isLoading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
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

                themesInteractor.loadTheme(settings.theme)
                // TODO fontsInteractor.loadFont(settings.fontType) ???

                val documentList = documentRepository.loadDocuments().onEach { document ->
                    languageInteractor.loadGrammar(document.language)
                }

                documents = documentList.map { document -> DocumentState(document) }
                selectedPosition = documentList.indexOf { it.uuid == settingsManager.selectedUuid }

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        settings = settings,
                        isLoading = documents.isNotEmpty(),
                    )
                }

                if (documents.isNotEmpty()) {
                    val selectedDocument = documents[selectedPosition].document
                    loadDocument(selectedDocument, fromUser = false)
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

    private fun errorState(e: Exception): ErrorState {
        return ErrorState(
            icon = UiR.drawable.ic_file_error,
            title = stringProvider.getString(UiR.string.common_error_occurred),
            subtitle = e.message.orEmpty(),
            action = ErrorAction.CLOSE_DOCUMENT,
        )
    }

    private inline fun List<DocumentState>.mapSelected(
        predicate: (DocumentState) -> DocumentState
    ): List<DocumentState> {
        return mapIndexed { index, state ->
            if (index == selectedPosition) {
                predicate(state)
            } else {
                state
            }
        }
    }

    private suspend fun loadSettings(): EditorSettings {
        return EditorSettings(
            theme = settingsManager.editorTheme,
            fontSize = settingsManager.fontSize.toFloat(),
            fontType = fontsInteractor.loadFont(settingsManager.fontType),
            wordWrap = settingsManager.wordWrap,
            codeCompletion = settingsManager.codeCompletion,
            pinchZoom = settingsManager.pinchZoom,
            lineNumbers = settingsManager.lineNumbers,
            highlightCurrentLine = settingsManager.highlightCurrentLine,
            highlightMatchingDelimiters = settingsManager.highlightMatchingDelimiters,
            readOnly = settingsManager.readOnly,
            keyboardPreset = ("\t" + settingsManager.keyboardPreset).map { char ->
                val display = if (char == '\t') {
                    stringProvider.getString(UiR.string.common_tab)
                } else {
                    char.toString()
                }
                KeyModel(display, char)
            },
            softKeyboard = settingsManager.softKeyboard,
            autoIndentation = settingsManager.autoIndentation,
            autoCloseBrackets = settingsManager.autoCloseBrackets,
            autoCloseQuotes = settingsManager.autoCloseQuotes,
            useSpacesInsteadOfTabs = settingsManager.useSpacesInsteadOfTabs,
            tabWidth = settingsManager.tabWidth,
            keybindings = shortcutsInteractor.loadShortcuts(),
        )
    }

    /*private val _toolbarViewState = MutableStateFlow<ToolbarViewState>(ToolbarViewState.ActionBar())
    val toolbarViewState: StateFlow<ToolbarViewState> = _toolbarViewState.asStateFlow()

    private val _editorViewState = MutableStateFlow<EditorViewState>(EditorViewState.Loading)
    val editorViewState: StateFlow<EditorViewState> = _editorViewState.asStateFlow()

    private val _keyboardViewState = MutableStateFlow(KeyboardManager.Mode.NONE)
    val keyboardViewState: StateFlow<KeyboardManager.Mode> = _keyboardViewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var toolbarMode = ToolbarManager.Mode.DEFAULT
    private var keyboardMode = KeyboardManager.Mode.KEYBOARD
    private var findParams = FindParams()

    private fun gotoLine() {
        viewModelScope.launch {
            try {
                if (selectedPosition > -1) {
                    _viewEvent.send(ViewEvent.Navigation(EditorScreen.GotoLine))
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun gotoLineNumber(event: EditorIntent.GotoLineNumber) {
        viewModelScope.launch {
            try {
                _viewEvent.send(ViewEvent.PopBackStack()) // close dialog
                if (selectedPosition > -1) {
                    _viewEvent.send(EditorViewEvent.GotoLine(event.lineNumber))
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun colorPicker() {
        viewModelScope.launch {
            try {
                if (selectedPosition > -1) {
                    _viewEvent.send(ViewEvent.Navigation(EditorScreen.InsertColor))
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun insertColor(event: EditorIntent.InsertColor) {
        viewModelScope.launch {
            try {
                _viewEvent.send(ViewEvent.PopBackStack()) // close dialog
                if (selectedPosition > -1) {
                    val color = event.color.toHexString()
                    _viewEvent.send(EditorViewEvent.InsertColor(color))
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun forceSyntax() {
        viewModelScope.launch {
            try {
                if (selectedPosition > -1) {
                    val document = documents[selectedPosition]
                    val screen = EditorScreen.ForceSyntaxDialogScreen(document.language.languageName)
                    _viewEvent.send(ViewEvent.Navigation(screen))
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun forceSyntaxHighlighting(event: EditorIntent.SelectLanguage) {
        viewModelScope.launch {
            try {
                _viewEvent.send(ViewEvent.PopBackStack()) // close dialog
                if (selectedPosition > -1) {
                    val document = documents[selectedPosition]
                    documents[selectedPosition] = document.copy(
                        language = LanguageFactory.fromName(event.language)
                    )
                    documentRepository.updateDocument(document)
                    refreshActionBar()
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun modifyContent() {
        if (selectedPosition > -1) {
            val document = documents[selectedPosition]
            if (!document.modified) {
                documents[selectedPosition] = document.copy(modified = true)
                refreshActionBar()
            }
        }
    }

    private fun swapKeyboard() {
        _keyboardViewState.update {
            when (keyboardMode) {
                KeyboardManager.Mode.KEYBOARD -> KeyboardManager.Mode.TOOLS
                KeyboardManager.Mode.TOOLS -> KeyboardManager.Mode.KEYBOARD
                KeyboardManager.Mode.NONE -> KeyboardManager.Mode.NONE
            }.also { mode ->
                keyboardMode = mode
            }
        }
    }

    private fun panelDefault() {
        toolbarMode = ToolbarManager.Mode.DEFAULT
        refreshActionBar()
    }

    private fun panelFind() {
        toolbarMode = ToolbarManager.Mode.FIND
        refreshActionBar()
    }

    private fun panelFindReplace() {
        toolbarMode = ToolbarManager.Mode.FIND_REPLACE
        refreshActionBar()
    }

    private fun findQuery(event: EditorIntent.FindQuery) {
        viewModelScope.launch {
            try {
                findParams = findParams.copy(query = event.query)
                refreshActionBar()

                val results = documentRepository.find(event.text, findParams)
                _viewEvent.send(EditorViewEvent.FindResults(results))
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun findRegex(event: EditorIntent.FindRegex) {
        viewModelScope.launch {
            try {
                findParams = findParams.copy(regex = !findParams.regex)
                refreshActionBar()

                val results = documentRepository.find(event.text, findParams)
                _viewEvent.send(EditorViewEvent.FindResults(results))
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun findMatchCase(event: EditorIntent.FindMatchCase) {
        viewModelScope.launch {
            try {
                findParams = findParams.copy(matchCase = !findParams.matchCase)
                refreshActionBar()

                val results = documentRepository.find(event.text, findParams)
                _viewEvent.send(EditorViewEvent.FindResults(results))
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun findWordsOnly(event: EditorIntent.FindWordsOnly) {
        viewModelScope.launch {
            try {
                findParams = findParams.copy(wordsOnly = !findParams.wordsOnly)
                refreshActionBar()

                val results = documentRepository.find(event.text, findParams)
                _viewEvent.send(EditorViewEvent.FindResults(results))
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }*/

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<EditorViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}