/*
 * Copyright 2022 Squircle CE contributors.
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
import com.blacksquircle.ui.core.ui.extensions.appendList
import com.blacksquircle.ui.core.ui.extensions.indexOf
import com.blacksquircle.ui.core.ui.extensions.replaceList
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.viewstate.DocumentViewState
import com.blacksquircle.ui.feature.editor.ui.viewstate.EditorViewState
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val documents = mutableListOf<DocumentModel>()
    private var currentJob: Job? = null

    init {
        loadFiles()
    }

    fun obtainEvent(event: EditorIntent) {
        when (event) {
            is EditorIntent.LoadFiles -> loadFiles()
            is EditorIntent.SelectTab -> selectTab(event)
            is EditorIntent.OpenFile -> openFile(event)
        }
    }

    private fun loadFiles() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                val documentList = documentRepository.fetchDocuments()
                val selectedUuid = settingsManager.selectedUuid
                val selectedPosition = when {
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

    private fun selectTab(event: EditorIntent.SelectTab) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                _documentViewState.value = DocumentViewState.Loading
                val selectedDocument = documents[event.position].also {
                    settingsManager.selectedUuid = it.uuid
                }
                val content = documentRepository.loadFile(selectedDocument)
                _documentViewState.value = DocumentViewState.Content(
                    content = content,
                    measurement = null,
                )
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
                _editorViewState.value = EditorViewState.ActionBar(
                    documents = documents.appendList(document),
                    position = documents.lastIndex,
                )
            } catch (e: Throwable) {
                Log.e(TAG, e.message, e)
                errorState(e)
            }
        }
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

    companion object {
        private const val TAG = "EditorViewModel"
    }
}