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

package com.blacksquircle.ui.feature.terminal.data.manager

import android.content.Context
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.blacksquircle.ui.feature.terminal.ui.model.TerminalCommand
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalSessionClientImpl
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class SessionManager(private val context: Context) {

    private val sessions = ConcurrentHashMap<String, SessionModel>()

    fun sessions(): List<SessionModel> {
        return sessions.values.toList()
    }

    fun createSession(): String {
        val sessionId = UUID.randomUUID().toString()
        val commands = MutableSharedFlow<TerminalCommand>(extraBufferCapacity = 64)
        sessions[sessionId] = SessionModel(
            sessionId = sessionId,
            session = TerminalSession(
                /* shellPath = */ SHELL_PATH,
                /* cwd = */ context.filesDir.absolutePath,
                /* args = */ emptyArray(),
                /* env = */ emptyArray(),
                /* transcriptRows = */ TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
                /* client = */ TerminalSessionClientImpl(commands),
            ),
            commands = commands.asSharedFlow()
        )
        return sessionId
    }

    fun closeSession(sessionId: String) {
        sessions[sessionId]?.session?.finishIfRunning()
        sessions.remove(sessionId)
    }

    fun closeAllSessions() {
        sessions.forEach { (_, session) ->
            session.session.finishIfRunning()
        }
        sessions.clear()
    }

    companion object {
        private const val SHELL_PATH = "/system/bin/sh"
    }
}