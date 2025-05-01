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

package com.blacksquircle.ui.feature.settings.ui.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Provider

internal class FilesHeaderViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _viewState = MutableStateFlow(updateViewState())
    val viewState: StateFlow<FilesHeaderViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onStorageAccessClicked() {
        viewModelScope.launch {
            _viewEvent.send(FilesHeaderViewEvent.OpenStorageSettings)
        }
    }

    fun onEncodingAutoDetectChanged(encodingAutoDetect: Boolean) {
        viewModelScope.launch {
            settingsManager.encodingAutoDetect = encodingAutoDetect
            _viewState.value = updateViewState()
        }
    }

    fun onEncodingForOpeningChanged(encodingForOpening: String) {
        viewModelScope.launch {
            settingsManager.encodingForOpening = encodingForOpening
            _viewState.value = updateViewState()
        }
    }

    fun onEncodingForSavingChanged(encodingForSaving: String) {
        viewModelScope.launch {
            settingsManager.encodingForSaving = encodingForSaving
            _viewState.value = updateViewState()
        }
    }

    fun onLineBreakForSavingChanged(lineBreakForSaving: String) {
        viewModelScope.launch {
            settingsManager.lineBreakForSaving = lineBreakForSaving
            _viewState.value = updateViewState()
        }
    }

    fun onShowHiddenChanged(showHidden: Boolean) {
        viewModelScope.launch {
            settingsManager.showHidden = showHidden
            _viewState.value = updateViewState()
        }
    }

    fun onCompactPackagesChanged(compactPackages: Boolean) {
        viewModelScope.launch {
            settingsManager.compactPackages = compactPackages
            _viewState.value = updateViewState()
        }
    }

    fun onFoldersOnTopChanged(foldersOnTop: Boolean) {
        viewModelScope.launch {
            settingsManager.foldersOnTop = foldersOnTop
            _viewState.value = updateViewState()
        }
    }

    fun onSortModeChanged(sortMode: String) {
        viewModelScope.launch {
            settingsManager.sortMode = sortMode
            _viewState.value = updateViewState()
        }
    }

    private fun updateViewState(): FilesHeaderViewState {
        return FilesHeaderViewState(
            encodingAutoDetect = settingsManager.encodingAutoDetect,
            encodingForOpening = settingsManager.encodingForOpening,
            encodingForSaving = settingsManager.encodingForSaving,
            encodingList = Charset.availableCharsets()
                .map(Map.Entry<String, Charset>::key),
            lineBreakForSaving = settingsManager.lineBreakForSaving,
            showHidden = settingsManager.showHidden,
            foldersOnTop = settingsManager.foldersOnTop,
            compactPackages = settingsManager.compactPackages,
            sortMode = settingsManager.sortMode,
        )
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<FilesHeaderViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}