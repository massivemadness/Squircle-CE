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

package com.blacksquircle.ui.feature.settings.ui.codestyle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CodeHeaderViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _viewState = MutableStateFlow(updateViewState())
    val viewState: StateFlow<CodeHeaderState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack())
        }
    }

    fun onAutoIndentChanged(autoIndent: Boolean) {
        viewModelScope.launch {
            settingsManager.autoIndentation = autoIndent
            _viewState.value = updateViewState()
        }
    }

    fun onAutoBracketsChanged(autoBrackets: Boolean) {
        viewModelScope.launch {
            settingsManager.autoCloseBrackets = autoBrackets
            _viewState.value = updateViewState()
        }
    }

    fun onAutoQuotesChanged(autoQuotes: Boolean) {
        viewModelScope.launch {
            settingsManager.autoCloseQuotes = autoQuotes
            _viewState.value = updateViewState()
        }
    }

    fun onUseSpacesChanged(useSpaces: Boolean) {
        viewModelScope.launch {
            settingsManager.useSpacesInsteadOfTabs = useSpaces
            _viewState.value = updateViewState()
        }
    }

    fun onTabWidthChanged(tabWidth: Int) {
        viewModelScope.launch {
            settingsManager.tabWidth = tabWidth
            _viewState.value = updateViewState()
        }
    }

    private fun updateViewState(): CodeHeaderState {
        return CodeHeaderState(
            autoIndentation = settingsManager.autoIndentation,
            autoCloseBrackets = settingsManager.autoCloseBrackets,
            autoCloseQuotes = settingsManager.autoCloseQuotes,
            useSpacesInsteadOfTabs = settingsManager.useSpacesInsteadOfTabs,
            tabWidth = settingsManager.tabWidth,
        )
    }
}