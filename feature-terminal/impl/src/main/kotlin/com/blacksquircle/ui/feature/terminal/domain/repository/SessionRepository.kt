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

package com.blacksquircle.ui.feature.terminal.domain.repository

import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.termux.terminal.TerminalSessionClient
import kotlinx.coroutines.flow.Flow

internal interface SessionRepository {

    val sessions: Flow<List<SessionModel>>

    fun createSession(client: TerminalSessionClient): String
    fun closeSession(sessionId: String)
    fun closeAllSessions()
}