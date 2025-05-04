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

package com.blacksquircle.ui.feature.servers.interactor

import com.blacksquircle.ui.feature.servers.createServerConfig
import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import com.blacksquircle.ui.feature.servers.data.interactor.ServerInteractorImpl
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ServerInteractorImplTest {

    private val serverRepository = mockk<ServerRepository>(relaxed = true)
    private val serverInteractor = ServerInteractorImpl(serverRepository)

    private val serverId = "12345"
    private val serverPassword = "secret"

    @Before
    fun setup() {
        ServerCredentials.remove(serverId)
    }

    @After
    fun cleanup() {
        ServerCredentials.remove(serverId)
    }

    @Test
    fun `When authenticate on server Then save credentials in memory`() = runTest {
        // Given
        mockkObject(ServerCredentials)

        // When
        serverInteractor.authenticate(serverId, serverPassword)

        // Then
        verify(exactly = 1) { ServerCredentials.put(serverId, serverPassword) }
        unmockkObject(ServerCredentials)
    }

    @Test
    fun `When loading servers Then load from repository`() = runTest {
        // When
        serverInteractor.flowAll()

        // Then
        coVerify(exactly = 1) { serverRepository.loadServers() }
    }

    @Test
    fun `When server with no password Then load credentials from memory`() = runTest {
        // Given
        val serverConfig = createServerConfig(
            uuid = serverId,
            authMethod = AuthMethod.PASSWORD,
            password = null, // requires authentication
        )
        coEvery { serverRepository.loadServer(serverId) } returns serverConfig
        ServerCredentials.put(serverId, serverPassword)

        // When
        val server = serverInteractor.loadServer(serverId)

        // Then
        assertEquals(serverPassword, server.password)
        coVerify(exactly = 1) { serverRepository.loadServer(serverId) }
    }

    @Test
    fun `When server has no passphrase Then load credentials from memory`() = runTest {
        // Given
        val serverConfig = createServerConfig(
            uuid = serverId,
            authMethod = AuthMethod.KEY,
            passphrase = null, // requires authentication
        )
        coEvery { serverRepository.loadServer(serverId) } returns serverConfig
        ServerCredentials.put(serverId, serverPassword)

        // When
        val server = serverInteractor.loadServer(serverId)

        // Then
        assertEquals(serverPassword, server.passphrase)
        coVerify(exactly = 1) { serverRepository.loadServer(serverId) }
    }
}