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

package com.blacksquircle.ui.feature.settings.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class EditorHeaderViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _viewState = MutableStateFlow(updateViewState())
    val viewState: StateFlow<EditorHeaderViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack())
        }
    }

    fun onFontSizeChanged(fontSize: Int) {
        viewModelScope.launch {
            settingsManager.fontSize = fontSize
            _viewState.value = updateViewState()
        }
    }

    fun onFontTypeClicked() {
        viewModelScope.launch {
            val screen = Screen.Fonts
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onWordWrapChanged(wordWrap: Boolean) {
        viewModelScope.launch {
            settingsManager.wordWrap = wordWrap
            _viewState.value = updateViewState()
        }
    }

    fun onCodeCompletionChanged(codeCompletion: Boolean) {
        viewModelScope.launch {
            settingsManager.codeCompletion = codeCompletion
            _viewState.value = updateViewState()
        }
    }

    fun onPinchZoomChanged(pinchZoom: Boolean) {
        viewModelScope.launch {
            settingsManager.pinchZoom = pinchZoom
            _viewState.value = updateViewState()
        }
    }

    fun onLineNumbersChanged(lineNumbers: Boolean) {
        viewModelScope.launch {
            settingsManager.lineNumbers = lineNumbers
            _viewState.value = updateViewState()
        }
    }

    fun onHighlightCurrentLineChanged(highlightCurrentLine: Boolean) {
        viewModelScope.launch {
            settingsManager.highlightCurrentLine = highlightCurrentLine
            _viewState.value = updateViewState()
        }
    }

    fun onHighlightMatchingDelimitersChanged(highlightMatchingDelimiters: Boolean) {
        viewModelScope.launch {
            settingsManager.highlightMatchingDelimiters = highlightMatchingDelimiters
            _viewState.value = updateViewState()
        }
    }

    fun onReadOnlyChanged(readOnly: Boolean) {
        viewModelScope.launch {
            settingsManager.readOnly = readOnly
            _viewState.value = updateViewState()
        }
    }

    fun onAutoSaveFilesChanged(autoSaveFiles: Boolean) {
        viewModelScope.launch {
            settingsManager.autoSaveFiles = autoSaveFiles
            _viewState.value = updateViewState()
        }
    }

    fun onExtendedKeyboardChanged(extendedKeyboard: Boolean) {
        viewModelScope.launch {
            settingsManager.extendedKeyboard = extendedKeyboard
            _viewState.value = updateViewState()
        }
    }

    fun onKeyboardPresetChanged(keyboardPreset: String) {
        viewModelScope.launch {
            settingsManager.keyboardPreset = keyboardPreset
            _viewState.value = updateViewState()
        }
    }

    fun onSoftKeyboardChanged(softKeyboard: Boolean) {
        viewModelScope.launch {
            settingsManager.softKeyboard = softKeyboard
            _viewState.value = updateViewState()
        }
    }

    private fun updateViewState(): EditorHeaderViewState {
        return EditorHeaderViewState(
            fontSize = settingsManager.fontSize,
            codeCompletion = settingsManager.codeCompletion,
            wordWrap = settingsManager.wordWrap,
            pinchZoom = settingsManager.pinchZoom,
            lineNumbers = settingsManager.lineNumbers,
            highlightCurrentLine = settingsManager.highlightCurrentLine,
            highlightMatchingDelimiters = settingsManager.highlightMatchingDelimiters,
            readOnly = settingsManager.readOnly,
            autoSaveFiles = settingsManager.autoSaveFiles,
            extendedKeyboard = settingsManager.extendedKeyboard,
            keyboardPreset = settingsManager.keyboardPreset,
            softKeyboard = settingsManager.softKeyboard,
        )
    }
}