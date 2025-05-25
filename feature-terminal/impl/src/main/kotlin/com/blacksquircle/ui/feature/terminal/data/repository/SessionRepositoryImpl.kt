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

package com.blacksquircle.ui.feature.terminal.data.repository

import com.blacksquircle.ui.feature.terminal.data.manager.SessionManager
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.blacksquircle.ui.feature.terminal.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SessionRepositoryImpl(
    private val sessionManager: SessionManager,
) : SessionRepository {

    private val _sessionFlow = MutableStateFlow<List<SessionModel>>(emptyList())
    override val sessionFlow: Flow<List<SessionModel>> = _sessionFlow.asStateFlow()

    override fun createSession(): String {
        return sessionManager.createSession().also {
            updateSessionList()
        }
    }

    override fun closeSession(sessionId: String) {
        sessionManager.closeSession(sessionId)
        updateSessionList()
    }

    private fun updateSessionList() {
        _sessionFlow.value = sessionManager.sessions()
    }
}