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

package com.blacksquircle.ui.feature.explorer.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.blacksquircle.ui.core.database.dao.path.PathDao
import com.blacksquircle.ui.core.database.entity.path.PathEntity
import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.isStorageAccessGranted
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.createFile
import com.blacksquircle.ui.feature.explorer.createFolder
import com.blacksquircle.ui.feature.explorer.data.manager.TaskAction
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.repository.ExplorerRepositoryImpl
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class ExplorerRepositoryImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val gitInteractor = mockk<GitInteractor>(relaxed = true)
    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)
    private val filesystemFactory = mockk<FilesystemFactory>(relaxed = true)
    private val pathDao = mockk<PathDao>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private val filesystem = mockk<Filesystem>(relaxed = true)

    private val explorerRepository = ExplorerRepositoryImpl(
        dispatcherProvider = dispatcherProvider,
        settingsManager = settingsManager,
        taskManager = taskManager,
        gitInteractor = gitInteractor,
        serverInteractor = serverInteractor,
        filesystemFactory = filesystemFactory,
        pathDao = pathDao,
        context = context
    )

    @Test
    fun `When loading filesystems without servers Then return default filesystems`() = runTest {
        // Given
        mockkStatic(Environment::class)
        every { Environment.getExternalStorageDirectory() } returns mockk<File>().apply {
            every { absolutePath } returns ""
        }
        coEvery { serverInteractor.loadServers() } returns emptyList()

        // When
        val filesystems = explorerRepository.loadFilesystems()

        // Then
        assertTrue(filesystems.size == 2)
        assertEquals(filesystems[0].uuid, LocalFilesystem.LOCAL_UUID)
        assertEquals(filesystems[1].uuid, RootFilesystem.ROOT_UUID)
    }

    @Test
    fun `When loading filesystems with servers Then return all filesystems`() = runTest {
        // Given
        mockkStatic(Environment::class)
        every { Environment.getExternalStorageDirectory() } returns mockk<File>().apply {
            every { absolutePath } returns ""
        }
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
        coEvery { serverInteractor.loadServers() } returns listOf(server)

        // When
        val filesystems = explorerRepository.loadFilesystems()

        // Then
        assertTrue(filesystems.size == 3)
        assertEquals(filesystems[0].uuid, LocalFilesystem.LOCAL_UUID)
        assertEquals(filesystems[1].uuid, RootFilesystem.ROOT_UUID)
        assertEquals(filesystems[2].uuid, serverId)
    }

    @Test
    fun `When path is not found in database Then return default location`() = runTest {
        // Given
        val defaultLocation = "file:///storage/emulated/0/"
        val filesystemModel = FilesystemModel(
            uuid = LocalFilesystem.LOCAL_UUID,
            title = "Local Storage",
            defaultLocation = FileModel(
                fileUri = defaultLocation,
                filesystemUuid = LocalFilesystem.LOCAL_UUID
            ),
        )
        coEvery { pathDao.load(LocalFilesystem.LOCAL_UUID) } returns null

        // When
        val breadcrumbs = explorerRepository.loadBreadcrumbs(filesystemModel)

        // Then
        assertTrue(breadcrumbs.size == 1)
        assertEquals(defaultLocation, breadcrumbs[0].fileUri)
    }

    @Test
    fun `When path exists in database Then return breadcrumbs`() = runTest {
        // Given
        val defaultLocation = "file:///storage/emulated/0/"
        val lastLocation = "file:///storage/emulated/0/Documents/untitled/"
        val filesystemModel = FilesystemModel(
            uuid = LocalFilesystem.LOCAL_UUID,
            title = "Local Storage",
            defaultLocation = FileModel(
                fileUri = defaultLocation,
                filesystemUuid = LocalFilesystem.LOCAL_UUID
            ),
        )
        val pathEntity = PathEntity(
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            fileUri = lastLocation,
        )
        coEvery { pathDao.load(LocalFilesystem.LOCAL_UUID) } returns pathEntity

        val defaultLocationUri = mockk<Uri>().apply {
            every { scheme } returns "file"
            every { path } returns defaultLocation.substringAfter("file://")
        }
        val lastLocationUri = mockk<Uri>().apply {
            every { scheme } returns "file"
            every { path } returns lastLocation.substringAfter("file://")
        }
        mockkStatic(Uri::class)
        every { Uri.parse(defaultLocation) } returns defaultLocationUri
        every { Uri.parse(lastLocation) } returns lastLocationUri

        // When
        val breadcrumbs = explorerRepository.loadBreadcrumbs(filesystemModel)

        // Then
        assertTrue(breadcrumbs.size == 3)
        assertEquals("file:///storage/emulated/0/", breadcrumbs[0].fileUri)
        assertEquals("file:///storage/emulated/0/Documents", breadcrumbs[1].fileUri)
        assertEquals("file:///storage/emulated/0/Documents/untitled", breadcrumbs[2].fileUri)
    }

    @Test(expected = PermissionException::class)
    fun `When list files without permission Then throw PermissionException`() = runTest {
        // Given
        mockkStatic(Context::isStorageAccessGranted)
        every { context.isStorageAccessGranted() } returns false

        // When
        val fileModel = createFolder("Documents")
        explorerRepository.listFiles(fileModel)

        // Then - throws exception
    }

    @Test
    fun `When list files Then return file list`() = runTest {
        // Given
        mockkStatic(Context::isStorageAccessGranted)
        every { context.isStorageAccessGranted() } returns true

        val parent = createFolder("Documents")
        val path = PathEntity(
            fileUri = parent.fileUri,
            filesystemUuid = parent.filesystemUuid,
        )
        val children = listOf(
            createFile("file_1.txt"),
            createFile("file_2.txt"),
            createFile("file_3.txt"),
        )
        every { filesystem.listFiles(parent) } returns children
        coEvery { filesystemFactory.create(any()) } returns filesystem

        // When
        val fileList = explorerRepository.listFiles(parent)

        // Then
        assertEquals(children, fileList)
        verify(exactly = 1) { filesystem.listFiles(parent) }
        coVerify(exactly = 1) { pathDao.insert(path) }
    }

    @Test
    fun `When create file called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val parent = createFolder("Documents")
        val child = createFile("Documents/untitled.txt")

        every { settingsManager.filesystem } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.CREATE, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.createFile(parent, child.name, child.directory)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.CREATE, any()) }
        coVerify(exactly = 1) { filesystemFactory.create(filesystemUuid) }
        verify(exactly = 1) { filesystem.createFile(child) }
    }

    @Test
    fun `When rename file called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val source = createFile("untitled.txt")
        val fileName = "new_name.txt"

        every { settingsManager.filesystem } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.RENAME, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.renameFile(source, fileName)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.RENAME, any()) }
        coVerify(exactly = 1) { filesystemFactory.create(filesystemUuid) }
        verify(exactly = 1) { filesystem.renameFile(source, fileName) }
    }

    @Test
    fun `When delete files called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val source = listOf(
            createFile("file_1.txt"),
            createFile("file_2.txt"),
            createFile("file_3.txt"),
        )

        every { settingsManager.filesystem } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.DELETE, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.deleteFiles(source)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.DELETE, any()) }
        coVerify(exactly = 1) { filesystemFactory.create(filesystemUuid) }

        verify(exactly = 1) { filesystem.deleteFile(source[0]) }
        verify(exactly = 1) { filesystem.deleteFile(source[1]) }
        verify(exactly = 1) { filesystem.deleteFile(source[2]) }
    }

    @Test
    fun `When copy files called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val source = listOf(
            createFile("file_1.txt"),
            createFile("file_2.txt"),
            createFile("file_3.txt"),
        )
        val dest = createFolder("Documents")

        every { settingsManager.filesystem } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.COPY, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.copyFiles(source, dest)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.COPY, any()) }
        coVerify(exactly = 1) { filesystemFactory.create(filesystemUuid) }

        verify(exactly = 1) { filesystem.copyFile(source[0], dest) }
        verify(exactly = 1) { filesystem.copyFile(source[1], dest) }
        verify(exactly = 1) { filesystem.copyFile(source[2], dest) }
    }

    @Test
    fun `When cut files called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val source = listOf(
            createFile("file_1.txt"),
            createFile("file_2.txt"),
            createFile("file_3.txt"),
        )
        val dest = createFolder("Documents")

        every { settingsManager.filesystem } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.CUT, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.cutFiles(source, dest)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.CUT, any()) }
        coVerify(exactly = 1) { filesystemFactory.create(filesystemUuid) }

        verify(exactly = 1) { filesystem.copyFile(source[0], dest) }
        verify(exactly = 1) { filesystem.copyFile(source[1], dest) }
        verify(exactly = 1) { filesystem.copyFile(source[2], dest) }

        verify(exactly = 1) { filesystem.deleteFile(source[0]) }
        verify(exactly = 1) { filesystem.deleteFile(source[1]) }
        verify(exactly = 1) { filesystem.deleteFile(source[2]) }
    }

    @Test
    fun `When compress files called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val source = listOf(
            createFile("file_1.txt"),
            createFile("file_2.txt"),
            createFile("file_3.txt"),
        )
        val dest = createFolder("Documents")
        val archive = createFile("Documents/archive.zip")

        every { settingsManager.filesystem } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.COMPRESS, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.compressFiles(source, dest, archive.name)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.COMPRESS, any()) }
        coVerify(exactly = 1) { filesystemFactory.create(filesystemUuid) }
        verify(exactly = 1) { filesystem.compressFiles(source, archive) }
    }

    @Test
    fun `When extract files called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val source = createFile("archive.zip")
        val dest = createFolder("Documents")

        every { settingsManager.filesystem } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.EXTRACT, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.extractFiles(source, dest)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.EXTRACT, any()) }
        coVerify(exactly = 1) { filesystemFactory.create(filesystemUuid) }
        verify(exactly = 1) { filesystem.extractFiles(source, dest) }
    }

    @Test
    fun `When clone repository called Then execute task`() = runTest {
        // Given
        val parent = createFolder("Documents")
        val url = "https://"

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.CLONE, capture(taskActionSlot)) } returns "12345"

        // When
        explorerRepository.cloneRepository(parent, url)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.CLONE, any()) }
        coVerify(exactly = 1) { gitInteractor.cloneRepository(parent, url) }
    }
}