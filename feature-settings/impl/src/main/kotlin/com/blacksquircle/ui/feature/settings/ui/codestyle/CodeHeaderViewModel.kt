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

package com.blacksquircle.ui.feature.settings.ui.codestyle

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
import javax.inject.Inject
import javax.inject.Provider

internal class CodeHeaderViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _viewState = MutableStateFlow(updateViewState())
    val viewState: StateFlow<CodeHeaderViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onAutoIndentChanged(autoIndent: Boolean) {
        viewModelScope.launch {
            settingsManager.autoIndentation = autoIndent
            _viewState.value = updateViewState()
        }
    }

    fun onAutoClosePairsChanged(autoQuotes: Boolean) {
        viewModelScope.launch {
            settingsManager.autoClosePairs = autoQuotes
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

    private fun updateViewState(): CodeHeaderViewState {
        return CodeHeaderViewState(
            autoIndentation = settingsManager.autoIndentation,
            autoClosePairs = settingsManager.autoClosePairs,
            useSpacesInsteadOfTabs = settingsManager.useSpacesInsteadOfTabs,
            tabWidth = settingsManager.tabWidth,
        )
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<CodeHeaderViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}