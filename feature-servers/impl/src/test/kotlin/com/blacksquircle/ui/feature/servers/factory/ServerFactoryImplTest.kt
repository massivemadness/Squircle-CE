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

package com.blacksquircle.ui.feature.servers.factory

import com.blacksquircle.ui.feature.servers.createServerConfig
import com.blacksquircle.ui.feature.servers.data.factory.ServerFactoryImpl
import com.blacksquircle.ui.filesystem.base.model.ServerType
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class ServerFactoryImplTest {

    private val cacheDir = mockk<File>()
    private val keysDir = mockk<File>()

    private val serverFilesystemFactory = ServerFactoryImpl(
        cacheDir = cacheDir,
        keysDir = keysDir
    )

    @Test
    fun `When creating a server with FTP scheme Then return FTPFilesystem`() = runTest {
        // Given
        val serverConfig = createServerConfig(scheme = ServerType.FTP)

        // When
        val filesystem = serverFilesystemFactory.create(serverConfig)

        // Then
        assertTrue(filesystem is FTPFilesystem)
    }

    @Test
    fun `When creating a server with FTPS scheme Then return FTPSFilesystem`() = runTest {
        // Given
        val serverConfig = createServerConfig(scheme = ServerType.FTPS)

        // When
        val filesystem = serverFilesystemFactory.create(serverConfig)

        // Then
        assertTrue(filesystem is FTPSFilesystem)
    }

    @Test
    fun `When creating a server with FTPES scheme Then return FTPSFilesystem`() = runTest {
        // Given
        val serverConfig = createServerConfig(scheme = ServerType.FTPES)

        // When
        val filesystem = serverFilesystemFactory.create(serverConfig)

        // Then
        assertTrue(filesystem is FTPSFilesystem)
    }

    @Test
    fun `When creating a server with SFTP scheme Then return SFTPFilesystem`() = runTest {
        // Given
        val serverConfig = createServerConfig(scheme = ServerType.SFTP)

        // When
        val filesystem = serverFilesystemFactory.create(serverConfig)

        // Then
        assertTrue(filesystem is SFTPFilesystem)
    }
}