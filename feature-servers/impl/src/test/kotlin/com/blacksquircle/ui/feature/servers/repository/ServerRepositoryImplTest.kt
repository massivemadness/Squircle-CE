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

package com.blacksquircle.ui.feature.servers.repository

import android.content.Context
import com.blacksquircle.ui.core.database.dao.server.ServerDao
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.servers.api.factory.ServerFactory
import com.blacksquircle.ui.feature.servers.createServerConfig
import com.blacksquircle.ui.feature.servers.createServerEntity
import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import com.blacksquircle.ui.feature.servers.data.repository.ServerRepositoryImpl
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ServerRepositoryImplTest {

    private val serverFactory = mockk<ServerFactory>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val dispatcherProvider = TestDispatcherProvider()
    private val serverDao = mockk<ServerDao>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private val serverRepository = ServerRepositoryImpl(
        serverFactory = serverFactory,
        settingsManager = settingsManager,
        dispatcherProvider = dispatcherProvider,
        serverDao = serverDao,
        context = context
    )

    private val serverId = "12345"

    @Before
    fun setup() {
        ServerCredentials.remove(serverId)
    }

    @After
    fun cleanup() {
        ServerCredentials.remove(serverId)
    }

    @Test
    fun `When checking availability Then return latency`() = runTest {
        // Given
        val serverConfig = createServerConfig()
        val filesystem = mockk<Filesystem>()
        every { filesystem.ping() } answers { Thread.sleep(200) }
        every { serverFactory.create(serverConfig) } returns filesystem

        // When
        val latency = serverRepository.checkAvailability(serverConfig)

        // Then
        verify(exactly = 1) { serverFactory.create(serverConfig) }
        verify(exactly = 1) { filesystem.ping() }
        assertTrue(latency >= 200)
    }

    @Test
    fun `When loading servers Then load from database`() = runTest {
        // Given
        val serverEntity = createServerEntity(uuid = serverId)
        coEvery { serverDao.loadAll() } returns listOf(serverEntity)

        // When
        val servers = serverRepository.loadServers()

        // Then
        val serverConfig = createServerConfig(uuid = serverId)
        assertEquals(serverConfig, servers[0])
        coVerify(exactly = 1) { serverDao.loadAll() }
    }

    @Test
    fun `When loading server Then load from database`() = runTest {
        // Given
        val serverEntity = createServerEntity(
            uuid = serverId,
            authMethod = AuthMethod.PASSWORD,
            password = null,
        )
        coEvery { serverDao.load(serverId) } returns serverEntity

        // When
        val server = serverRepository.loadServer(serverId)

        // Then
        assertEquals(null, server.password)
        coVerify(exactly = 1) { serverDao.load(serverId) }
    }

    @Test
    fun `When updating the server Then update server and clear credentials`() = runTest {
        // Given
        val serverConfig = createServerConfig(uuid = serverId)
        val serverEntity = createServerEntity(uuid = serverId)
        every { settingsManager.workspace } returns "different"
        mockkObject(ServerCredentials)

        // When
        serverRepository.upsertServer(serverConfig)

        // Then
        coVerify(exactly = 1) { serverDao.insert(serverEntity) }
        verify(exactly = 1) { ServerCredentials.remove(serverConfig.uuid) }
        unmockkObject(ServerCredentials)
    }

    @Test
    fun `When updating selected server Then clear selected workspace`() = runTest {
        // Given
        val serverConfig = createServerConfig(uuid = serverId)
        every { settingsManager.workspace } returns serverId

        // When
        serverRepository.upsertServer(serverConfig)

        // Then
        verify(exactly = 1) { settingsManager.remove(SettingsManager.KEY_WORKSPACE) }
    }

    @Test
    fun `When deleting the server Then delete server and clear credentials`() = runTest {
        // Given
        val serverConfig = createServerConfig(uuid = serverId)
        every { settingsManager.workspace } returns "different"
        mockkObject(ServerCredentials)

        // When
        serverRepository.deleteServer(serverConfig)

        // Then
        coVerify(exactly = 1) { serverDao.delete(serverConfig.uuid) }
        verify(exactly = 1) { ServerCredentials.remove(serverConfig.uuid) }
        unmockkObject(ServerCredentials)
    }

    @Test
    fun `When deleting selected server Then clear selected workspace`() = runTest {
        // Given
        val serverConfig = createServerConfig(uuid = serverId)
        every { settingsManager.workspace } returns serverId

        // When
        serverRepository.deleteServer(serverConfig)

        // Then
        verify(exactly = 1) { settingsManager.remove(SettingsManager.KEY_WORKSPACE) }
    }
}