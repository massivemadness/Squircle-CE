/*
 * Copyright 2021 Squircle IDE contributors.
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
import com.blacksquircle.ui.R
import com.blacksquircle.ui.data.converter.DocumentConverter
import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.domain.repository.documents.DocumentRepository
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.utils.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val toastEvent = SingleLiveEvent<Int>()

    val fullScreenMode: Boolean
        get() = settingsManager.fullScreenMode
    val confirmExit: Boolean
        get() = settingsManager.confirmExit

    fun handleIntent(intent: Intent, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                var path = intent.data?.path.toString()
                Log.d(TAG, "Handle external file path = $path")

                if (path.startsWith("/external_files/")) {
                    path = path.replaceFirst(
                        oldValue = "/external_files/",
                        newValue = "/storage/emulated/0/"
                    )
                }

                val index = path.indexOf("/storage/emulated/0/")
                if (index > -1) {
                    val fileModel = FileModel(path.substring(index, path.length))
                    val documentModel = DocumentConverter.toModel(fileModel)
                    documentRepository.updateDocument(documentModel)
                    onSuccess()
                } else {
                    toastEvent.value = R.string.message_file_not_found
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }
}