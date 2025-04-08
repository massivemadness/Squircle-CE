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

package com.blacksquircle.ui.feature.servers.cache

import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Test

class ServerCredentialsTest {

    private val testUuid = "12345"
    private val testPassword = "secret"

    @After
    fun cleanup() {
        ServerCredentials.remove(testUuid)
    }

    @Test
    fun `When credentials are put Then they can be retrieved`() {
        // Given
        val uuid = testUuid
        val password = testPassword

        // When
        ServerCredentials.put(uuid, password)

        // Then
        val actual = ServerCredentials.get(uuid)
        assertEquals(password, actual)
    }

    @Test
    fun `When getting credentials for unknown uuid Then null is returned`() {
        // Given
        val unknownUuid = "non-existent-uuid"

        // When
        val actual = ServerCredentials.get(unknownUuid)

        // Then
        assertNull(actual)
    }

    @Test
    fun `When credentials are removed Then they are no longer retrievable`() {
        // Given
        val uuid = testUuid
        ServerCredentials.put(uuid, testPassword)

        // When
        ServerCredentials.remove(uuid)

        // Then
        val actual = ServerCredentials.get(uuid)
        assertNull(actual)
    }

    @Test
    fun `When credentials are overwritten Then latest value is returned`() {
        // Given
        val uuid = testUuid
        ServerCredentials.put(uuid, "old-password")

        // When
        ServerCredentials.put(uuid, "new-password")

        // Then
        val actual = ServerCredentials.get(uuid)
        assertEquals("new-password", actual)
    }
}