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

import com.blacksquircle.ui.core.database.dao.server.ServerDao
import com.blacksquircle.ui.feature.servers.createServerConfig
import com.blacksquircle.ui.feature.servers.createServerEntity
import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import com.blacksquircle.ui.feature.servers.data.interactor.ServerInteractorImpl
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
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

    private val dispatcherProvider = TestDispatcherProvider()
    private val serverDao = mockk<ServerDao>(relaxed = true)
    private val serverInteractor = ServerInteractorImpl(
        dispatcherProvider = dispatcherProvider,
        serverDao = serverDao,
    )

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
    fun `When loading servers Then load from database`() = runTest {
        // When
        serverInteractor.flowAll()

        // Then
        coVerify(exactly = 1) { serverDao.flowAll() }
    }

    @Test
    fun `When loading server Then load password from memory`() = runTest {
        // Given
        val serverEntity = createServerEntity(
            uuid = serverId,
            authMethod = AuthMethod.PASSWORD,
            password = null, // requires authentication
        )
        coEvery { serverDao.load(serverId) } returns serverEntity
        ServerCredentials.put(serverId, serverPassword)

        // When
        val server = serverInteractor.loadServer(serverId)

        // Then
        val expected = createServerConfig(
            uuid = serverId,
            authMethod = AuthMethod.PASSWORD,
            password = serverPassword,
        )
        assertEquals(expected, server)
        coVerify(exactly = 1) { serverDao.load(serverId) }
    }

    @Test
    fun `When loading server Then load passphrase from memory`() = runTest {
        // Given
        val serverEntity = createServerEntity(
            uuid = serverId,
            authMethod = AuthMethod.KEY,
            passphrase = null, // requires authentication
        )
        coEvery { serverDao.load(serverId) } returns serverEntity
        ServerCredentials.put(serverId, serverPassword)

        // When
        val server = serverInteractor.loadServer(serverId)

        // Then
        val serverConfig = createServerConfig(
            uuid = serverId,
            authMethod = AuthMethod.KEY,
            passphrase = serverPassword,
        )
        assertEquals(serverConfig, server)
        coVerify(exactly = 1) { serverDao.load(serverId) }
    }
}