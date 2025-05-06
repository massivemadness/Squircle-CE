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

package com.blacksquircle.ui.feature.explorer.ui

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.explorer.createFile
import com.blacksquircle.ui.feature.explorer.createFolder
import com.blacksquircle.ui.feature.explorer.createNode
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.defaultWorkspaces
import com.blacksquircle.ui.feature.explorer.domain.model.Task
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FileTaskTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val dispatcherProvider = TestDispatcherProvider()
    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val explorerRepository = mockk<ExplorerRepository>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)
    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)
    private val asyncNodeBuilder = AsyncNodeBuilder(dispatcherProvider)

    private val workspaces = defaultWorkspaces()
    private val selectedWorkspace = workspaces[0]

    private val defaultLocation = selectedWorkspace.defaultLocation
    private val fileList = listOf(
        createFile(name = "Apple"),
        createFile(name = "Banana"),
        createFile(name = "Cherry"),
    )
    private val taskId = "12345"

    @Before
    fun setup() {
        coEvery { explorerRepository.loadWorkspaces() } returns flowOf(workspaces)
        coEvery { explorerRepository.listFiles(any()) } returns fileList

        every { settingsManager.workspace } returns selectedWorkspace.uuid
        every { settingsManager.workspace = any() } answers {
            every { settingsManager.workspace } returns firstArg()
        }
    }

    @Test
    fun `When create file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.CREATE)

        every { explorerRepository.createFile(any(), any(), isFolder = false) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(defaultLocation)
        viewModel.onFileSelected(fileNode)
        viewModel.onCreateClicked()
        viewModel.createFile("file.txt")

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.createFile(defaultLocation, "file.txt", isFolder = false)
        }
    }

    @Test
    fun `When create folder clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.CREATE)

        every { explorerRepository.createFile(any(), any(), isFolder = true) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(defaultLocation)
        viewModel.onFileSelected(fileNode)
        viewModel.onCreateClicked()
        viewModel.createFolder("folder")

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.createFile(defaultLocation, "folder", isFolder = true)
        }
    }

    @Test
    fun `When clone repository clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.CLONE)

        every { explorerRepository.cloneRepository(any(), any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(defaultLocation)
        viewModel.onFileSelected(fileNode)
        viewModel.onCloneClicked()
        viewModel.cloneRepository("https://...", false)

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.cloneRepository(defaultLocation, "https://...", false)
        }
    }

    @Test
    fun `When rename file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.RENAME)

        every { explorerRepository.renameFile(any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(fileList[0])
        viewModel.onFileSelected(fileNode)
        viewModel.onRenameClicked()
        viewModel.renameFile("file.txt")

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.renameFile(fileList[0], "file.txt")
        }
    }

    @Test
    fun `When delete file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.DELETE)

        every { explorerRepository.deleteFiles(any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(fileList[0])
        viewModel.onFileSelected(fileNode)
        viewModel.onDeleteClicked()
        viewModel.deleteFile()

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.deleteFiles(listOf(fileList[0]))
        }
    }

    @Test
    fun `When move file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.MOVE)

        every { explorerRepository.moveFiles(any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(fileList[0])
        viewModel.onFileSelected(fileNode)
        viewModel.onCutClicked()

        val rootNode = createNode(defaultLocation)
        viewModel.onFileSelected(rootNode)
        viewModel.onPasteClicked()

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.moveFiles(listOf(fileList[0]), defaultLocation)
        }
    }

    @Test
    fun `When copy file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.COPY)

        every { explorerRepository.copyFiles(any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(fileList[0])
        viewModel.onFileSelected(fileNode)
        viewModel.onCopyClicked()

        val rootNode = createNode(defaultLocation)
        viewModel.onFileSelected(rootNode)
        viewModel.onPasteClicked()

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.copyFiles(listOf(fileList[0]), defaultLocation)
        }
    }

    @Test
    fun `When compress file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.COMPRESS)

        every { explorerRepository.compressFiles(any(), any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(fileList[0])
        viewModel.onFileSelected(fileNode)
        viewModel.onCompressClicked()
        viewModel.compressFiles("archive.zip")

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.compressFiles(listOf(fileList[0]), defaultLocation, "archive.zip")
        }
    }

    @Test
    fun `When compressing files in different directories Then show error`() = runTest {
        // Given
        val parentList = listOf(
            createFolder(name = "Documents"),
            createFolder(name = "Downloads"),
        )
        val childList = listOf(
            createFile(name = "Documents/one.txt"),
            createFile(name = "Documents/two.txt"),
        )
        coEvery { explorerRepository.listFiles(defaultLocation) } returns parentList
        coEvery { explorerRepository.listFiles(parentList[0]) } returns childList

        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.COMPRESS)

        every { explorerRepository.compressFiles(any(), any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val parentNode = createNode(parentList[0]) // Documents
        val childNode = createNode(childList[0]) // one.txt
        viewModel.onFileClicked(parentNode)

        viewModel.onFileSelected(parentNode)
        viewModel.onFileSelected(childNode)
        viewModel.onCompressClicked()

        // Then
        assertTrue(viewModel.viewEvent.first() is ViewEvent.Toast)

        verify(exactly = 0) { taskManager.monitor(taskId) }
        coVerify(exactly = 0) { explorerRepository.compressFiles(any(), any(), any()) }
    }

    @Test
    fun `When extract file clicked Then execute task`() = runTest {
        // Given
        val fileModel = createFile("archive.zip")
        coEvery { explorerRepository.listFiles(any()) } returns listOf(fileModel)

        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.EXTRACT)

        every { explorerRepository.extractFiles(any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        val fileNode = createNode(fileModel)
        viewModel.onFileClicked(fileNode)

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.extractFiles(fileModel, defaultLocation)
        }
    }

    private fun createViewModel(): ExplorerViewModel {
        return ExplorerViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            taskManager = taskManager,
            editorInteractor = editorInteractor,
            explorerRepository = explorerRepository,
            serverInteractor = serverInteractor,
            asyncNodeBuilder = asyncNodeBuilder,
        )
    }
}