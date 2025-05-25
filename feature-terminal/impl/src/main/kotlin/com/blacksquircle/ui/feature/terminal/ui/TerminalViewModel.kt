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
import com.blacksquircle.ui.feature.terminal.data.manager.SessionManager
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
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
    private val settingsManager: SettingsManager,
    private val sessionManager: SessionManager,
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
        updateViewState()
    }

    fun onCreateSessionClicked() {
        selectedSession = sessionManager.createSession()
        sessions = sessionManager.sessions()
        updateViewState()
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
            updateViewState()
        }
    }

    private fun updateViewState() {
        _viewState.update {
            it.copy(
                sessions = sessions,
                selectedSession = selectedSession,
            )
        }
    }

    private fun loadSessions() {
        sessions = sessionManager.sessions()

        if (sessions.isEmpty()) {
            sessionManager.createSession()
            sessions = sessionManager.sessions()
        }

        selectedSession = sessions.first().id

        _viewState.update {
            it.copy(
                sessions = sessions,
                selectedSession = selectedSession,
                cursorBlinking = settingsManager.cursorBlinking,
                keepScreenOn = settingsManager.keepScreenOn,
            )
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