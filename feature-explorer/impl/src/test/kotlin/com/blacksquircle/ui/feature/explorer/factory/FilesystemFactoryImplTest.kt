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

package com.blacksquircle.ui.feature.explorer.factory

import android.content.Context
import com.blacksquircle.ui.feature.explorer.data.factory.FilesystemFactoryImpl
import com.blacksquircle.ui.feature.servers.api.factory.ServerFactory
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.filesystem.saf.SAFFilesystem
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Test

class FilesystemFactoryImplTest {

    private val serverFactory = mockk<ServerFactory>(relaxed = true)
    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private val filesystemFactory = FilesystemFactoryImpl(
        serverFactory = serverFactory,
        serverInteractor = serverInteractor,
        context = context
    )

    @Test
    fun `When uuid is LOCAL_UUID Then create LocalFilesystem`() = runTest {
        // Given
        val uuid = LocalFilesystem.LOCAL_UUID

        // When
        val filesystem = filesystemFactory.create(uuid)

        // Then
        assertTrue(filesystem is LocalFilesystem)
    }

    @Test
    @Ignore("TODO: Method join in android.text.TextUtils not mocked")
    fun `When uuid is ROOT_UUID Then create RootFilesystem`() = runTest {
        // Given
        val uuid = RootFilesystem.ROOT_UUID

        // When
        val filesystem = filesystemFactory.create(uuid)

        // Then
        assertTrue(filesystem is RootFilesystem)
    }

    @Test
    fun `When uuid is SAF_UUID Then create SAFFilesystem`() = runTest {
        // Given
        val uuid = SAFFilesystem.SAF_UUID

        // When
        val filesystem = filesystemFactory.create(uuid)

        // Then
        assertTrue(filesystem is SAFFilesystem)
    }

    @Test
    fun `When uuid is other Then create server filesystem`() = runTest {
        // Given
        val uuid = "server_uuid"

        // When
        val filesystem = filesystemFactory.create(uuid)

        // Then
        assertNotNull(filesystem)
        coVerify(exactly = 1) { serverInteractor.loadServer(uuid) }
        coVerify(exactly = 1) { serverFactory.create(any()) }
    }
}