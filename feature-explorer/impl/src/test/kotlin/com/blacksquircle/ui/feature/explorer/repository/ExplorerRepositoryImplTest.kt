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
import com.blacksquircle.ui.core.database.dao.workspace.WorkspaceDao
import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.isStorageAccessGranted
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.createFile
import com.blacksquircle.ui.feature.explorer.createFolder
import com.blacksquircle.ui.feature.explorer.data.manager.TaskAction
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.repository.ExplorerRepositoryImpl
import com.blacksquircle.ui.feature.explorer.data.workspace.DefaultWorkspaceSource
import com.blacksquircle.ui.feature.explorer.data.workspace.ServerWorkspaceSource
import com.blacksquircle.ui.feature.explorer.data.workspace.UserWorkspaceSource
import com.blacksquircle.ui.feature.explorer.defaultWorkspaces
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ExplorerRepositoryImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val gitInteractor = mockk<GitInteractor>(relaxed = true)
    private val filesystemFactory = mockk<FilesystemFactory>(relaxed = true)
    private val workspaceDao = mockk<WorkspaceDao>(relaxed = true)
    private val defaultWorkspaceSource = mockk<DefaultWorkspaceSource>(relaxed = true)
    private val userWorkspaceSource = mockk<UserWorkspaceSource>(relaxed = true)
    private val serverWorkspaceSource = mockk<ServerWorkspaceSource>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private val filesystem = mockk<Filesystem>(relaxed = true)
    private val workspaces = defaultWorkspaces()
    private val selectedWorkspace = workspaces[0]

    private val explorerRepository = ExplorerRepositoryImpl(
        dispatcherProvider = dispatcherProvider,
        settingsManager = settingsManager,
        taskManager = taskManager,
        gitInteractor = gitInteractor,
        filesystemFactory = filesystemFactory,
        workspaceDao = workspaceDao,
        defaultWorkspaceSource = defaultWorkspaceSource,
        userWorkspaceSource = userWorkspaceSource,
        serverWorkspaceSource = serverWorkspaceSource,
        context = context
    )

    @Before
    fun setup() {
        every { defaultWorkspaceSource.workspaceFlow } returns flowOf(workspaces)
        every { userWorkspaceSource.workspaceFlow } returns emptyFlow()
        every { serverWorkspaceSource.workspaceFlow } returns emptyFlow()
    }

    @Test
    fun `When loading workspaces Then load from multiple sources`() = runTest {
        // When
        explorerRepository.loadWorkspaces()

        // Then
        verify(exactly = 1) { defaultWorkspaceSource.workspaceFlow }
        verify(exactly = 1) { userWorkspaceSource.workspaceFlow }
        verify(exactly = 1) { serverWorkspaceSource.workspaceFlow }
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
        val children = listOf(
            createFile("file_1.txt"),
            createFile("file_2.txt"),
            createFile("file_3.txt"),
        )
        every { filesystem.listFiles(parent) } returns children
        coEvery { filesystemFactory.create(any()) } returns filesystem

        explorerRepository.selectWorkspace(selectedWorkspace)

        // When
        val fileList = explorerRepository.listFiles(parent)

        // Then
        assertEquals(children, fileList)
        verify(exactly = 1) { filesystem.listFiles(parent) }
    }

    @Test
    fun `When create file called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val parent = createFolder("Documents")
        val child = createFile("Documents/untitled.txt")

        every { settingsManager.workspace } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.CREATE, capture(taskActionSlot)) } returns "12345"

        explorerRepository.selectWorkspace(selectedWorkspace)

        // When
        explorerRepository.createFile(parent, child.name, child.isDirectory)
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

        every { settingsManager.workspace } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.RENAME, capture(taskActionSlot)) } returns "12345"

        explorerRepository.selectWorkspace(selectedWorkspace)

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

        every { settingsManager.workspace } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.DELETE, capture(taskActionSlot)) } returns "12345"

        explorerRepository.selectWorkspace(selectedWorkspace)

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

        every { settingsManager.workspace } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.COPY, capture(taskActionSlot)) } returns "12345"

        explorerRepository.selectWorkspace(selectedWorkspace)

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
    fun `When move files called Then execute task`() = runTest {
        // Given
        val filesystemUuid = LocalFilesystem.LOCAL_UUID
        val source = listOf(
            createFile("file_1.txt"),
            createFile("file_2.txt"),
            createFile("file_3.txt"),
        )
        val dest = createFolder("Documents")

        every { settingsManager.workspace } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.MOVE, capture(taskActionSlot)) } returns "12345"

        explorerRepository.selectWorkspace(selectedWorkspace)

        // When
        explorerRepository.moveFiles(source, dest)
        taskActionSlot.captured.invoke { /* no-op */ }

        // Then
        verify(exactly = 1) { taskManager.execute(TaskType.MOVE, any()) }
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

        every { settingsManager.workspace } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.COMPRESS, capture(taskActionSlot)) } returns "12345"

        explorerRepository.selectWorkspace(selectedWorkspace)

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

        every { settingsManager.workspace } returns filesystemUuid
        coEvery { filesystemFactory.create(filesystemUuid) } returns filesystem

        val taskActionSlot = slot<TaskAction>()
        every { taskManager.execute(TaskType.EXTRACT, capture(taskActionSlot)) } returns "12345"

        explorerRepository.selectWorkspace(selectedWorkspace)

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