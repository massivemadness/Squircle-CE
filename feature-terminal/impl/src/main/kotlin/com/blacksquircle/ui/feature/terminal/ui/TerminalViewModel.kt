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

package com.blacksquircle.ui.feature.terminal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.terminal.api.model.ShellArgs
import com.blacksquircle.ui.feature.terminal.api.runtime.TerminalRuntime
import com.blacksquircle.ui.feature.terminal.domain.manager.RuntimeManager
import com.blacksquircle.ui.feature.terminal.domain.manager.SessionManager
import com.blacksquircle.ui.feature.terminal.domain.model.RuntimeState
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TerminalViewModel @AssistedInject constructor(
    private val settingsManager: SettingsManager,
    private val sessionManager: SessionManager,
    private val runtimeManager: RuntimeManager,
    @Assisted private val pendingCommand: ShellArgs?,
) : ViewModel() {

    private val _viewState = MutableStateFlow(TerminalViewState())
    val viewState: StateFlow<TerminalViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var sessions = emptyList<SessionModel>()
    private var selectedSession: String? = null

    init {
        loadSessions()
    }

    fun onSessionClicked(sessionModel: SessionModel) {
        selectedSession = sessionModel.id
        _viewState.update {
            it.copy(selectedSession = selectedSession)
        }
    }

    fun onCreateSessionClicked() {
        viewModelScope.launch {
            createRuntime { runtime ->
                selectedSession = sessionManager.createSession(runtime)
                sessions = sessionManager.sessions()

                _viewState.update {
                    it.copy(
                        sessions = sessions,
                        selectedSession = selectedSession,
                    )
                }

                viewModelScope.launch {
                    _viewEvent.send(TerminalViewEvent.ScrollToEnd)
                }
            }
        }
    }

    @Suppress("KotlinConstantConditions")
    fun onCloseSessionClicked(sessionModel: SessionModel) {
        val selectedPosition = sessions.indexOf { it.id == selectedSession }
        val removedPosition = sessions.indexOf { it.id == sessionModel.id }
        val currentPosition = when {
            removedPosition == selectedPosition -> when {
                removedPosition - 1 > -1 -> removedPosition - 1
                removedPosition + 1 < sessions.size -> removedPosition
                else -> -1
            }
            removedPosition < selectedPosition -> selectedPosition - 1
            removedPosition > selectedPosition -> selectedPosition
            else -> -1
        }

        sessions = sessions.filter { it.id != sessionModel.id }
        selectedSession = sessions.getOrNull(currentPosition)?.id

        sessionManager.closeSession(sessionModel.id)

        if (sessions.isEmpty()) {
            viewModelScope.launch {
                _viewEvent.send(ViewEvent.PopBackStack)
            }
        } else {
            _viewState.update {
                it.copy(
                    sessions = sessions,
                    selectedSession = selectedSession,
                )
            }
        }
    }

    private suspend fun createRuntime(onReady: (TerminalRuntime) -> Unit) {
        runtimeManager.createRuntime().collect { state ->
            when (state) {
                is RuntimeState.Installing -> {
                    _viewState.update {
                        it.copy(
                            isInstalling = true,
                            installProgress = state.progress,
                            installError = null,
                        )
                    }
                }
                is RuntimeState.Ready -> {
                    _viewState.update {
                        it.copy(
                            isInstalling = false,
                            installProgress = 1f,
                            installError = null,
                        )
                    }
                    onReady(state.runtime)
                }
                is RuntimeState.Failed -> {
                    _viewState.update {
                        it.copy(
                            isInstalling = true,
                            installProgress = 0f,
                            installError = state.error,
                        )
                    }
                }
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            sessions = sessionManager.sessions()
            selectedSession = sessions.lastOrNull()?.id

            if (sessions.isEmpty() || pendingCommand != null) {
                createRuntime { runtime ->
                    sessionManager.createSession(runtime, pendingCommand)

                    sessions = sessionManager.sessions()
                    selectedSession = sessions.lastOrNull()?.id

                    _viewState.update {
                        it.copy(
                            sessions = sessions,
                            selectedSession = selectedSession,
                            cursorBlinking = settingsManager.cursorBlinking,
                            keepScreenOn = settingsManager.keepScreenOn,
                        )
                    }
                }
            } else {
                _viewState.update {
                    it.copy(
                        sessions = sessions,
                        selectedSession = selectedSession,
                        cursorBlinking = settingsManager.cursorBlinking,
                        keepScreenOn = settingsManager.keepScreenOn,
                    )
                }
            }
        }
    }

    class ParameterizedFactory(private val pendingCommand: ShellArgs?) : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelFactory: Factory

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelFactory.create(pendingCommand) as T
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted pendingCommand: ShellArgs?): TerminalViewModel
    }
}