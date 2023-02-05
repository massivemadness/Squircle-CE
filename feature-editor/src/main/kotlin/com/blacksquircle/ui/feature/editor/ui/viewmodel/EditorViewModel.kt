/*
 * Copyright 2023 Squircle CE contributors.
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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.ui.extensions.*
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.editor.data.utils.SettingsEvent
import com.blacksquircle.ui.feature.editor.domain.model.DocumentContent
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.model.DocumentParams
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.viewstate.DocumentViewState
import com.blacksquircle.ui.feature.editor.ui.viewstate.EditorViewState
import com.blacksquircle.ui.feature.themes.data.utils.InternalTheme
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val documentRepository: DocumentRepository,
    private val themesRepository: ThemesRepository
) : ViewModel() {

    private val _editorViewState = MutableStateFlow<EditorViewState>(EditorViewState.Stub)
    val editorViewState: StateFlow<EditorViewState> = _editorViewState.asStateFlow()

    private val _documentViewState = MutableStateFlow<DocumentViewState>(DocumentViewState.Loading)
    val documentViewState: StateFlow<DocumentViewState> = _documentViewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val _settings = MutableStateFlow<List<SettingsEvent<*>>>(emptyList())
    val settings: StateFlow<List<SettingsEvent<*>>> = _settings.asStateFlow()

    private val documents = mutableListOf<DocumentModel>()
    private var selectedPosition = -1
    private var currentJob: Job? = null

    init {
        loadFiles()
    }

    fun obtainEvent(event: EditorIntent) {
        when (event) {
            is EditorIntent.LoadFiles -> loadFiles()
            is EditorIntent.LoadSettings -> loadSettings()

            is EditorIntent.OpenFile -> openFile(event)
            is EditorIntent.SelectTab -> selectTab(event)
            is EditorIntent.MoveTab -> moveTab(event)

            is EditorIntent.CloseTab -> closeTab(event)
            is EditorIntent.CloseOthers -> closeOthers(event)
            is EditorIntent.CloseAll -> closeAll(event)

            is EditorIntent.SaveFile -> saveFile(event)
        }
    }

    private fun loadFiles() {
        viewModelScope.launch {
            try {
                val documentList = documentRepository.fetchDocuments()
                val selectedUuid = settingsManager.selectedUuid
                selectedPosition = when {
                    documentList.isEmpty() -> -1
                    documentList.none { it.uuid == selectedUuid } -> 0
                    else -> documentList.indexOf { it.uuid == selectedUuid }
                }
                _editorViewState.value = EditorViewState.ActionBar(
                    documents = documents.replaceList(documentList),
                    position = selectedPosition
                )
                if (documentList.isEmpty()) {
                    _documentViewState.value = DocumentViewState.Error(
                        image = R.drawable.ic_file_find,
                        title = stringProvider.getString(R.string.message_no_open_files),
                        subtitle = "",
                    )
                } else {
                    selectTab(EditorIntent.SelectTab(selectedPosition))
                }
            } catch (e: Throwable) {
                Log.e(TAG, e.message, e)
                errorState(e)
            }
        }
    }

    private fun openFile(event: EditorIntent.OpenFile) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                val document = DocumentConverter.toModel(event.fileModel)
                val position = documents.indexOrNull {
                    it.fileUri == document.fileUri
                } ?: run {
                    documents.appendList(document).also { documents ->
                        updateDocuments(documents)
                    }
                    documents.lastIndex
                }
                if (position != selectedPosition) {
                    selectTab(EditorIntent.SelectTab(position))
                }
            } catch (e: Throwable) {
                Log.e(TAG, e.message, e)
                errorState(e)
            }
        }
    }

    private fun selectTab(event: EditorIntent.SelectTab) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                _editorViewState.value = EditorViewState.ActionBar(
                    documents = documents,
                    position = event.position,
                )
                _documentViewState.value = DocumentViewState.Loading

                val document = documents[event.position]
                settingsManager.selectedUuid = document.uuid
                selectedPosition = event.position

                _documentViewState.value = DocumentViewState.Content(
                    content = documentRepository.loadFile(document),
                    showKeyboard = settingsManager.extendedKeyboard
                )
            } catch (e: Throwable) {
                Log.e(TAG, e.message, e)
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
                updateDocuments(documents)

                when (selectedPosition) {
                    in event.to until event.from -> selectedPosition++
                    in (event.from + 1)..event.to -> selectedPosition--
                    event.from -> selectedPosition = event.to
                }

                _editorViewState.value = EditorViewState.ActionBar(
                    documents = documents,
                    position = selectedPosition,
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    @Suppress("KotlinConstantConditions")
    private fun closeTab(event: EditorIntent.CloseTab) {
        viewModelScope.launch {
            try {
                val document = documents[event.position]
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
                selectedPosition = position
                settingsManager.selectedUuid = documents.getOrNull(position)?.uuid.orEmpty()

                _editorViewState.value = EditorViewState.ActionBar(
                    documents = documents,
                    position = selectedPosition
                )

                if (reloadFile) {
                    if (documents.isEmpty()) {
                        _documentViewState.value = DocumentViewState.Error(
                            image = R.drawable.ic_file_find,
                            title = stringProvider.getString(R.string.message_no_open_files),
                            subtitle = "",
                        )
                    } else {
                        selectTab(EditorIntent.SelectTab(position))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
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
                if (event.position != selectedPosition) {
                    selectTab(EditorIntent.SelectTab(0))
                } else {
                    selectedPosition = 0
                    _editorViewState.value = EditorViewState.ActionBar(
                        documents = documents,
                        position = selectedPosition
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun closeAll(event: EditorIntent.CloseAll) {
        viewModelScope.launch {
            try {
                deleteDocuments(documents)
                selectedPosition = -1
                _editorViewState.value = EditorViewState.ActionBar(
                    documents = documents,
                    position = selectedPosition
                )
                _documentViewState.value = DocumentViewState.Error(
                    image = R.drawable.ic_file_find,
                    title = stringProvider.getString(R.string.message_no_open_files),
                    subtitle = "",
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun saveFile(event: EditorIntent.SaveFile) {
        viewModelScope.launch {
            try {
                if (selectedPosition > -1) {
                    val content = DocumentContent(
                        documentModel = documents[selectedPosition].apply {
                            scrollX = event.scrollX
                            scrollY = event.scrollY
                            selectionStart = event.selectionStart
                            selectionEnd = event.selectionEnd
                        },
                        language = event.language,
                        undoStack = event.undoStack,
                        redoStack = event.redoStack,
                        text = event.text,
                    )
                    val currentState = documentViewState.value
                    if (currentState is DocumentViewState.Content) {
                        if (!event.local) {
                            _documentViewState.value = currentState.copy(content = content)
                        }
                        documentRepository.saveFile(content, DocumentParams(event.local, true))
                        documentRepository.updateDocument(content.documentModel)
                        if (event.local) {
                            _viewEvent.send(ViewEvent.Toast(
                                stringProvider.getString(R.string.message_saved)
                            ))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private suspend fun updateDocuments(documents: List<DocumentModel>) {
        documents.forEachIndexed { index, document ->
            documentRepository.updateDocument(document.copy(position = index))
            document.position = index
        }
    }

    private suspend fun deleteDocuments(documents: List<DocumentModel>) {
        documents.forEach { document ->
            documentRepository.deleteDocument(document)
        }
        this.documents.clear()
    }

    private fun errorState(e: Throwable) {
        when (e) {
            is CancellationException -> {
                _documentViewState.value = DocumentViewState.Loading
            }
            else -> {
                _documentViewState.value = DocumentViewState.Error(
                    image = R.drawable.ic_file_error,
                    title = stringProvider.getString(R.string.message_error_occurred),
                    subtitle = e.message.orEmpty(),
                )
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val settings = mutableListOf<SettingsEvent<*>>()

                val value = settingsManager.colorScheme
                val theme = InternalTheme.find(value) ?: themesRepository.fetchTheme(value)
                settings.add(SettingsEvent.ThemePref(theme))

                val fontSize = settingsManager.fontSize.toFloat()
                settings.add(SettingsEvent.FontSize(fontSize))

                val fontType = settingsManager.fontType
                settings.add(SettingsEvent.FontType(fontType))

                val wordWrap = settingsManager.wordWrap
                settings.add(SettingsEvent.WordWrap(wordWrap))

                val codeCompletion = settingsManager.codeCompletion
                settings.add(SettingsEvent.CodeCompletion(codeCompletion))

                val errorHighlighting = settingsManager.errorHighlighting
                settings.add(SettingsEvent.ErrorHighlight(errorHighlighting))

                val pinchZoom = settingsManager.pinchZoom
                settings.add(SettingsEvent.PinchZoom(pinchZoom))

                val lineNumbers = Pair(
                    settingsManager.lineNumbers,
                    settingsManager.highlightCurrentLine
                )
                settings.add(SettingsEvent.LineNumbers(lineNumbers))

                val highlightMatchingDelimiters = settingsManager.highlightMatchingDelimiters
                settings.add(SettingsEvent.Delimiters(highlightMatchingDelimiters))

                val keyboardPreset = settingsManager.keyboardPreset
                    .toCharArray().map(Char::toString)
                settings.add(SettingsEvent.KeyboardPreset(keyboardPreset))

                val softKeyboard = settingsManager.softKeyboard
                settings.add(SettingsEvent.SoftKeys(softKeyboard))

                val autoIndentation = Triple(
                    settingsManager.autoIndentation,
                    settingsManager.autoCloseBrackets,
                    settingsManager.autoCloseQuotes
                )
                settings.add(SettingsEvent.AutoIndentation(autoIndentation))

                val useSpacesInsteadOfTabs = settingsManager.useSpacesInsteadOfTabs
                settings.add(SettingsEvent.UseSpacesNotTabs(useSpacesInsteadOfTabs))

                val tabWidth = settingsManager.tabWidth
                settings.add(SettingsEvent.TabWidth(tabWidth))

                _settings.value = settings
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    companion object {
        private const val TAG = "EditorViewModel"
    }
}