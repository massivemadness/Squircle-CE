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

package com.blacksquircle.ui.feature.explorer.workspace

import com.blacksquircle.ui.feature.explorer.data.workspace.ServerWorkspaceSource
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ServerWorkspaceSourceTest {

    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)

    private lateinit var serverWorkspaceSource: ServerWorkspaceSource

    @Test
    fun `When loading server workspaces Then return workspaces`() = runTest {
        // Given
        val serverId = "12345"
        val server = ServerConfig(
            uuid = serverId,
            scheme = ServerType.FTP,
            name = "Test Server",
            address = "192.168.1.1",
            port = 21,
            initialDir = "/",
            authMethod = AuthMethod.PASSWORD,
            username = "username",
            password = "secret",
            keyId = null,
            passphrase = null,
        )
        coEvery { serverInteractor.flowAll() } returns flowOf(listOf(server))

        serverWorkspaceSource = ServerWorkspaceSource(serverInteractor)

        // When
        val workspaces = serverWorkspaceSource.workspaceFlow.first()

        // Then
        assertEquals(1, workspaces.size)
        assertEquals(workspaces[0].uuid, serverId)
    }
}