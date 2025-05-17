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
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.terminal.domain.repository.SessionRepository
import com.blacksquircle.ui.feature.terminal.ui.compose.TerminalCommand
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalSessionClientImpl
import com.termux.terminal.TerminalSessionClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class TerminalViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(TerminalViewState())
    val viewState: StateFlow<TerminalViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val sessionClient: TerminalSessionClient
        get() = TerminalSessionClientImpl(
            redraw = {
                viewModelScope.launch {
                    val command = TerminalCommand.Redraw
                    val event = TerminalViewEvent.Command(command)
                    _viewEvent.send(event)
                }
            }
        )

    init {
        loadSessions()
    }

    fun onCreateSessionClicked() {
        sessionRepository.createSession(sessionClient)
    }

    fun onCloseSessionClicked(sessionId: String) {
        sessionRepository.closeSession(sessionId)
    }

    fun onCloseAllSessionsClicked() {
        sessionRepository.closeAllSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            sessionRepository.sessions.collect { sessions ->
                if (sessions.isEmpty()) {
                    sessionRepository.createSession(sessionClient)
                    return@collect
                }
                _viewState.update {
                    it.copy(
                        sessions = sessions,
                        selectedSession = sessions[0].sessionId,
                    )
                }
            }
        }
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<TerminalViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}