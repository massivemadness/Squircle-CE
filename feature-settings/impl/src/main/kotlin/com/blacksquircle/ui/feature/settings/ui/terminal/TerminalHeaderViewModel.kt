/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.settings.ui.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.terminal.api.interactor.TerminalInteractor
import com.blacksquircle.ui.feature.terminal.api.model.RuntimeType
import com.blacksquircle.ui.navigation.api.Navigator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class TerminalHeaderViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val terminalInteractor: TerminalInteractor,
    private val navigator: Navigator,
) : ViewModel() {

    private val _viewState = MutableStateFlow(updateViewState())
    val viewState: StateFlow<TerminalHeaderViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        navigator.goBack()
    }

    fun onResume() {
        _viewState.update {
            it.copy(
                termuxInstalled = terminalInteractor.isTermuxInstalled(),
                termuxCompatible = terminalInteractor.isTermuxCompatible(),
                termuxPermission = terminalInteractor.isTermuxPermissionGranted(),
            )
        }
    }

    fun onPause() {
        // no-op
    }

    fun onTerminalRuntimeChanged(runtime: String) {
        settingsManager.terminalRuntime = runtime
        _viewState.value = updateViewState()
    }

    fun onTermuxCopyPropsClicked() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(termuxPropsCopied = true)
            }

            delay(2000)

            _viewState.update {
                it.copy(termuxPropsCopied = false)
            }
        }
    }

    fun onCursorBlinkingChanged(cursorBlinking: Boolean) {
        settingsManager.cursorBlinking = cursorBlinking
        _viewState.value = updateViewState()
    }

    fun onKeepScreenOnChanged(keepScreenOn: Boolean) {
        settingsManager.keepScreenOn = keepScreenOn
        _viewState.value = updateViewState()
    }

    private fun updateViewState(): TerminalHeaderViewState {
        return TerminalHeaderViewState(
            currentRuntime = RuntimeType.of(settingsManager.terminalRuntime),
            termuxInstalled = terminalInteractor.isTermuxInstalled(),
            termuxCompatible = terminalInteractor.isTermuxCompatible(),
            termuxPermission = terminalInteractor.isTermuxPermissionGranted(),
            termuxPropsCopied = false,
            cursorBlinking = settingsManager.cursorBlinking,
            keepScreenOn = settingsManager.keepScreenOn,
        )
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<TerminalHeaderViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}