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

package com.blacksquircle.ui.feature.terminal.data

import android.content.Context
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.blacksquircle.ui.feature.terminal.domain.repository.SessionRepository
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

internal class SessionRepositoryImpl(
    private val context: Context,
) : SessionRepository {

    private val sessionCache = hashMapOf<String, TerminalSession>()

    private val _sessions = MutableStateFlow<List<SessionModel>>(emptyList())
    override val sessions: Flow<List<SessionModel>> = _sessions.asStateFlow()

    override fun createSession(client: TerminalSessionClient): String {
        val sessionId = UUID.randomUUID().toString()
        val session = TerminalSession(
            /* shellPath = */ "/system/bin/sh",
            /* cwd = */ context.filesDir.absolutePath,
            /* args = */ emptyArray(),
            /* env = */ emptyArray(),
            /* transcriptRows = */ TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
            /* client = */ client,
        )
        sessionCache[sessionId] = session
        updateSessions()
        return sessionId
    }

    override fun closeSession(sessionId: String) {
        sessionCache[sessionId]?.finishIfRunning()
        sessionCache.remove(sessionId)
        updateSessions()
    }

    override fun closeAllSessions() {
        sessionCache.forEach { (_, session) ->
            session.finishIfRunning()
        }
        sessionCache.clear()
        updateSessions()
    }

    private fun updateSessions() {
        _sessions.value = sessionCache.map { (sessionId, session) ->
            SessionModel(
                sessionId = sessionId,
                session = session,
            )
        }
    }
}