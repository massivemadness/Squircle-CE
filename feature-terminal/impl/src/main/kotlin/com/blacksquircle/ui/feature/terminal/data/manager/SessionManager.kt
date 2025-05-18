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

import com.blacksquircle.ui.feature.terminal.data.factory.RuntimeFactory
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.blacksquircle.ui.feature.terminal.ui.model.TerminalCommand
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalSessionClientImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class SessionManager(private val runtimeFactory: RuntimeFactory) {

    private val sessions = ConcurrentHashMap<String, SessionModel>()

    fun sessions(): List<SessionModel> {
        return sessions.values.toList()
    }

    fun createSession(): String {
        val runtime = runtimeFactory.create()
        val sessionId = UUID.randomUUID().toString()
        val commands = MutableSharedFlow<TerminalCommand>(extraBufferCapacity = 64)
        val client = TerminalSessionClientImpl(
            onUpdate = { commands.tryEmit(TerminalCommand.Update) },
            onCopy = { text -> commands.tryEmit(TerminalCommand.Copy(text)) },
            onPaste = { commands.tryEmit(TerminalCommand.Paste) }
        )
        sessions[sessionId] = SessionModel(
            sessionId = sessionId,
            session = runtime.create(client),
            commands = commands.asSharedFlow(),
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
}