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

package com.blacksquircle.ui.application.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.editor.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import com.blacksquircle.ui.feature.editor.R as EditorR

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val documentRepository: DocumentRepository,
) : ViewModel() {

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val _intentEvent = Channel<Intent>(Channel.BUFFERED)
    val intentEvent: Flow<Intent> = _intentEvent.receiveAsFlow()

    val fullScreenMode: Boolean
        get() = settingsManager.fullScreenMode
    val confirmExit: Boolean
        get() = settingsManager.confirmExit

    fun handleIntent(intent: Intent) {
        viewModelScope.launch {
            _intentEvent.send(intent)
        }
    }

    fun handleDocument(file: File, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val fileModel = FileModel(file.absolutePath, LocalFilesystem.LOCAL_UUID)
                val documentModel = DocumentConverter.toModel(fileModel)
                documentRepository.updateDocument(documentModel)
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(EditorR.string.message_error_occurred),
                    ),
                )
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}