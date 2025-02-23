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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.api.model.EditorApiEvent
import com.blacksquircle.ui.feature.editor.data.mapper.DocumentMapper
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorViewState
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.fragment.model.ErrorAction
import com.blacksquircle.ui.feature.editor.ui.fragment.model.ErrorState
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.feature.themes.api.interactor.ThemesInteractor
import com.blacksquircle.ui.filesystem.base.model.FileModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.blacksquircle.ui.ds.R as UiR

internal class EditorViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val documentRepository: DocumentRepository,
    private val editorInteractor: EditorInteractor,
    private val themesInteractor: ThemesInteractor,
    private val fontsInteractor: FontsInteractor,
    private val shortcutsInteractor: ShortcutsInteractor,
) : ViewModel() {

    private val _viewState = MutableStateFlow(EditorViewState())
    val viewState: StateFlow<EditorViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var documents = emptyList<DocumentState>()
    private var selectedPosition = -1
    private var currentJob: Job? = null

    init {
        loadDocuments()

        editorInteractor.eventBus
            .onEach { event ->
                when (event) {
                    is EditorApiEvent.OpenFile -> {
                        onFileOpened(event.fileModel)
                    }
                    is EditorApiEvent.OpenFileUri -> {
                        // TODO
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onDrawerClicked() {
        viewModelScope.launch {
            val screen = Screen.Explorer
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            val screen = Screen.Settings
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onDocumentClicked(documentState: DocumentState) {
        val selectedDocument = documents[selectedPosition].document
        if (selectedDocument.fileUri == documentState.document.fileUri) {
            return
        }
        loadDocument(documentState.document)
    }

    fun onCloseDocumentClicked(documentState: DocumentState) {
        closeDocument(documentState.document)
    }

    private fun onFileOpened(fileModel: FileModel) {
        val document = DocumentMapper.toModel(fileModel, documents.size)
        loadDocument(document)
    }

    @Suppress("KotlinConstantConditions")
    private fun closeDocument(document: DocumentModel) {
        viewModelScope.launch {
            try {
                val documentPosition = document.position
                val currentPosition = when {
                    documentPosition == selectedPosition -> when {
                        documentPosition - 1 > -1 -> documentPosition - 1
                        documentPosition + 1 < documents.size -> documentPosition
                        else -> -1
                    }
                    documentPosition < selectedPosition -> selectedPosition - 1
                    documentPosition > selectedPosition -> selectedPosition
                    else -> -1
                }

                documents = documents.filterNot { it.document.position == documentPosition }
                selectedPosition = currentPosition

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                    )
                }

                // ???
                loadDocument(documents[selectedPosition].document)

                // settingsManager.selectedUuid = documents
                //     .getOrNull(currentPosition)?.document?.uuid.orEmpty()

                // documentRepository.closeDocument(document)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
            }
        }
    }

    private fun loadDocument(document: DocumentModel) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            /** Check if [document] is already added to tabs */
            val existingIndex = documents.indexOf { it.document.fileUri == document.fileUri }
            if (existingIndex != -1) {
                documents = documents.mapSelected { state ->
                    state.copy(content = null)
                }
                /** Select existing document */
                selectedPosition = existingIndex
            } else {
                documents = documents.mapSelected { state ->
                    state.copy(content = null)
                }
                /** Add new document */
                val documentState = DocumentState(
                    document = document,
                    content = null,
                )
                documents = documents + documentState
                selectedPosition = documents.size - 1
            }

            val documentState = documents[selectedPosition]
            settingsManager.selectedUuid = document.uuid

            try {
                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                        isLoading = true,
                    )
                }

                delay(1000L)
                val content = documentRepository.loadDocument(documentState.document)
                documents = documents.mapSelected {
                    documentState.copy(
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
                    documentState.copy(
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
                val documentList = documentRepository.loadDocuments()

                documents = documentList.map { document ->
                    DocumentState(
                        document = document,
                        content = null,
                    )
                }
                selectedPosition = documentList.indexOf { it.uuid == settingsManager.selectedUuid }

                _viewState.update {
                    it.copy(
                        documents = documents,
                        selectedDocument = selectedPosition,
                    )
                }

                if (documents.isNotEmpty()) {
                    loadDocument(documents[selectedPosition].document)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
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

    private fun List<DocumentState>.mapSelected(
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

    /*private val _toolbarViewState = MutableStateFlow<ToolbarViewState>(ToolbarViewState.ActionBar())
    val toolbarViewState: StateFlow<ToolbarViewState> = _toolbarViewState.asStateFlow()

    private val _editorViewState = MutableStateFlow<EditorViewState>(EditorViewState.Loading)
    val editorViewState: StateFlow<EditorViewState> = _editorViewState.asStateFlow()

    private val _keyboardViewState = MutableStateFlow(KeyboardManager.Mode.NONE)
    val keyboardViewState: StateFlow<KeyboardManager.Mode> = _keyboardViewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val _settings = MutableStateFlow<List<SettingsEvent<*>>>(emptyList())
    val settings: StateFlow<List<SettingsEvent<*>>> = _settings.asStateFlow()

    // FIXME private
    val confirmExit: Boolean
        get() = settingsManager.confirmExit

    private val documents = mutableListOf<DocumentModel>()
    private var selectedPosition = -1
    private var toolbarMode = ToolbarManager.Mode.DEFAULT
    private var keyboardMode = KeyboardManager.Mode.KEYBOARD
    private var findParams = FindParams()
    private var currentJob: Job? = null

    private fun newFile(event: EditorIntent.NewFile) {
        viewModelScope.launch {
            try {
                openFileUri(EditorIntent.OpenFileUri(event.fileUri))
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            }
        }
    }

    private fun openFile(event: EditorIntent.OpenFile) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                val document = DocumentMapper.toModel(event.fileModel)
                val position = documents.indexOrNull { it.fileUri == document.fileUri } ?: run {
                    documents.appendList(document)
                    updateDocuments()
                    documents.lastIndex
                }
                if (position != selectedPosition) {
                    selectTab(EditorIntent.SelectTab(position))
                }
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            }
        }
    }

    private fun openFileUri(event: EditorIntent.OpenFileUri) {
        viewModelScope.launch {
            try {
                val document = documentRepository.openFile(event.fileUri)
                val position = documents.indexOrNull { it.fileUri == document.fileUri } ?: run {
                    documents.appendList(document)
                    updateDocuments()
                    documents.lastIndex
                }
                if (position != selectedPosition) {
                    selectTab(EditorIntent.SelectTab(position))
                }
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            }
        }
    }

    private fun selectTab(event: EditorIntent.SelectTab) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                _editorViewState.value = EditorViewState.Loading
                _keyboardViewState.value = KeyboardManager.Mode.NONE

                val document = documents[event.position]
                settingsManager.selectedUuid = document.uuid
                refreshActionBar(event.position)

                _editorViewState.value = EditorViewState.Content(
                    content = documentRepository.loadFile(document),
                )
                _keyboardViewState.value = if (settingsManager.extendedKeyboard) {
                    keyboardMode
                } else {
                    KeyboardManager.Mode.NONE
                }
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            }
        }
    }

    private fun moveTab(event: EditorIntent.MoveTab) {
        viewModelScope.launch {
            try {
                val document = documents[event.from]
                documents.removeAt(event.from)
                documents.add(event.to, document)
                updateDocuments()

                refreshActionBar(
                    position = when (selectedPosition) {
                        in event.to until event.from -> selectedPosition + 1
                        in (event.from + 1)..event.to -> selectedPosition - 1
                        event.from -> event.to
                        else -> selectedPosition
                    },
                )
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    @Suppress("KotlinConstantConditions")
    private fun closeTab(event: EditorIntent.CloseTab) {
        viewModelScope.launch {
            try {
                if (event.position > -1) {
                    val document = documents[event.position]
                    if (document.modified) {
                        if (!event.allowModified) {
                            val screen = EditorScreen.CloseModifiedDialogScreen(event.position, document.name)
                            _viewEvent.send(ViewEvent.Navigation(screen))
                            return@launch
                        } else {
                            _viewEvent.send(ViewEvent.PopBackStack()) // close dialog
                        }
                    }
                    val reloadFile = event.position == selectedPosition
                    val position = when {
                        event.position == selectedPosition -> when {
                            event.position - 1 > -1 -> event.position - 1
                            event.position + 1 < documents.size -> event.position
                            else -> -1
                        }
                        event.position < selectedPosition -> selectedPosition - 1
                        event.position > selectedPosition -> selectedPosition
                        else -> -1
                    }

                    documentRepository.deleteDocument(document)
                    documents.removeAt(event.position)
                    settingsManager.selectedUuid = documents.getOrNull(position)?.uuid.orEmpty()
                    updateDocuments()
                    refreshActionBar(position)

                    if (reloadFile) {
                        if (documents.isNotEmpty()) {
                            selectTab(EditorIntent.SelectTab(position))
                        } else {
                            emptyState()
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun closeOthers(event: EditorIntent.CloseOthers) {
        viewModelScope.launch {
            try {
                for (index in documents.size - 1 downTo 0) {
                    if (index != event.position) {
                        val document = documents[index]
                        documentRepository.deleteDocument(document)
                        documents.removeAt(index)
                    }
                }
                updateDocuments()
                if (event.position != selectedPosition) {
                    selectTab(EditorIntent.SelectTab(0))
                } else {
                    refreshActionBar(0)
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun closeAll() {
        viewModelScope.launch {
            try {
                deleteDocuments()
                refreshActionBar(-1)
                emptyState()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

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

    private fun saveFile(event: EditorIntent.SaveFile) {
        viewModelScope.launch {
            try {
                if (selectedPosition > -1) {
                    val modified = documents[selectedPosition].modified
                    val localStorage = event.local || settingsManager.autoSaveFiles
                    val changeModified = localStorage && modified
                    val document = documents[selectedPosition].copy(
                        modified = if (changeModified) false else modified,
                        scrollX = event.scrollX,
                        scrollY = event.scrollY,
                        selectionStart = event.selectionStart,
                        selectionEnd = event.selectionEnd,
                    )
                    documents[selectedPosition] = document
                    if (changeModified) {
                        refreshActionBar()
                    }
                    val currentState = editorViewState.value
                    if (currentState is EditorViewState.Content) {
                        val content = DocumentContent(
                            documentModel = document,
                            undoStack = event.undoStack.clone(),
                            redoStack = event.redoStack.clone(),
                            text = event.text.toString(),
                        )
                        val params = DocumentParams(localStorage, true)
                        if (!event.local && !event.unselected) {
                            currentState.content = content
                        }
                        documentRepository.saveFile(content, params)
                        documentRepository.updateDocument(content.documentModel)
                        if (event.local) {
                            _viewEvent.send(
                                ViewEvent.Toast(stringProvider.getString(R.string.message_saved)),
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun saveFileAs(event: EditorIntent.SaveFileAs) {
        viewModelScope.launch {
            try {
                if (selectedPosition > -1) {
                    val document = documents[selectedPosition]
                    documentRepository.saveFileAs(document, event.fileUri)
                    _viewEvent.send(
                        ViewEvent.Toast(stringProvider.getString(R.string.message_saved)),
                    )
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
    }

    private suspend fun updateDocuments() {
        for (index in documents.size - 1 downTo 0) {
            val document = documents[index].copy(position = index)
            documents[index] = document
            documentRepository.updateDocument(document)
        }
    }

    private suspend fun deleteDocuments() {
        for (index in documents.size - 1 downTo 0) {
            val document = documents[index]
            documentRepository.deleteDocument(document)
        }
        documents.clear()
    }

    private fun refreshActionBar(position: Int = selectedPosition) {
        _toolbarViewState.value = ToolbarViewState.ActionBar(
            documents = documents.toList(),
            position = position.also {
                selectedPosition = it
            },
            mode = toolbarMode,
            findParams = findParams,
        )
    }

    private fun emptyState() {
        if (documents.isEmpty()) {
            _editorViewState.value = EditorViewState.Error(
                image = UiR.drawable.ic_file_find,
                title = stringProvider.getString(R.string.message_no_open_files),
                subtitle = "",
                action = EditorErrorAction.Undefined,
            )
            _keyboardViewState.value = KeyboardManager.Mode.NONE
        }
    }

    private fun errorState(e: Throwable) {
        when (e) {
            is CancellationException -> {
                _editorViewState.value = EditorViewState.Loading
                _keyboardViewState.value = KeyboardManager.Mode.NONE
            }
            else -> {
                _editorViewState.value = EditorViewState.Error(
                    image = UiR.drawable.ic_file_error,
                    title = stringProvider.getString(UiR.string.common_error_occurred),
                    subtitle = e.message.orEmpty(),
                    action = EditorErrorAction.CloseDocument,
                )
                _keyboardViewState.value = KeyboardManager.Mode.NONE
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val settings = mutableListOf<SettingsEvent<*>>()

                val themeModel = themesInteractor.current()
                settings.add(SettingsEvent.ColorScheme(themeModel))

                val fontSize = settingsManager.fontSize.toFloat()
                settings.add(SettingsEvent.FontSize(fontSize))

                val fontModel = fontsInteractor.current()
                settings.add(SettingsEvent.FontType(fontModel))

                val wordWrap = settingsManager.wordWrap
                settings.add(SettingsEvent.WordWrap(wordWrap))

                val codeCompletion = settingsManager.codeCompletion
                settings.add(SettingsEvent.CodeCompletion(codeCompletion))

     */
    /*val errorHighlighting = settingsManager.errorHighlighting
                settings.add(SettingsEvent.ErrorHighlight(errorHighlighting))*/
    /*

                val pinchZoom = settingsManager.pinchZoom
                settings.add(SettingsEvent.PinchZoom(pinchZoom))

                val lineNumbers = Pair(
                    settingsManager.lineNumbers,
                    settingsManager.highlightCurrentLine,
                )
                settings.add(SettingsEvent.LineNumbers(lineNumbers))

                val highlightMatchingDelimiters = settingsManager.highlightMatchingDelimiters
                settings.add(SettingsEvent.Delimiters(highlightMatchingDelimiters))

                val readOnly = settingsManager.readOnly
                settings.add(SettingsEvent.ReadOnly(readOnly))

                val keyboardPreset = "\t" + settingsManager.keyboardPreset
                val keyList = keyboardPreset.map { char ->
                    val display = if (char == '\t') {
                        stringProvider.getString(UiR.string.common_tab)
                    } else {
                        char.toString()
                    }
                    KeyModel(display, char)
                }
                settings.add(SettingsEvent.KeyboardPreset(keyList))

                val softKeyboard = settingsManager.softKeyboard
                settings.add(SettingsEvent.SoftKeys(softKeyboard))

                val autoIndentation = Triple(
                    settingsManager.autoIndentation,
                    settingsManager.autoCloseBrackets,
                    settingsManager.autoCloseQuotes,
                )
                settings.add(SettingsEvent.AutoIndentation(autoIndentation))

                val useSpacesInsteadOfTabs = settingsManager.useSpacesInsteadOfTabs
                settings.add(SettingsEvent.UseSpacesNotTabs(useSpacesInsteadOfTabs))

                val tabWidth = settingsManager.tabWidth
                settings.add(SettingsEvent.TabWidth(tabWidth))

                val keybindings = shortcutsInteractor.loadShortcuts()
                settings.add(SettingsEvent.Keybindings(keybindings))

                _settings.value = settings
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }*/
}