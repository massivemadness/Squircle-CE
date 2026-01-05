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

package com.blacksquircle.ui.feature.terminal

import com.blacksquircle.ui.feature.terminal.data.manager.SessionManagerImpl
import com.blacksquircle.ui.feature.terminal.domain.manager.SessionManager
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class SessionManagerImplTest {

    private val sessionManager: SessionManager = SessionManagerImpl()

    @Test
    fun `When user has multiple sessions Then return sessions sorted by ordinal`() {
        // Given
        sessionManager.createSession(FakeRuntime, null)
        sessionManager.createSession(FakeRuntime, null)
        sessionManager.createSession(FakeRuntime, null)

        // When
        val sessionList = sessionManager.sessions()

        // Then
        assertEquals(3, sessionList.size)

        assertEquals(0, sessionList[0].ordinal)
        assertEquals(1, sessionList[1].ordinal)
        assertEquals(2, sessionList[2].ordinal)
    }

    @Test
    fun `When closing session Then remove session from list`() {
        // Given
        val sessionId = sessionManager.createSession(FakeRuntime, null)

        // When
        sessionManager.closeSession(sessionId)

        // Then
        assertTrue(sessionManager.sessions().isEmpty())
    }
}