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

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.explorer.createFile
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.defaultFilesystems
import com.blacksquircle.ui.feature.explorer.domain.model.Task
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FileTaskTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val explorerRepository = mockk<ExplorerRepository>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)
    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)

    private val filesystems = defaultFilesystems()
    private val selectedFilesystem = filesystems[0]

    private val defaultLocation = selectedFilesystem.defaultLocation
    private val fileList = listOf(
        createFile(name = "Apple"),
        createFile(name = "Banana"),
        createFile(name = "Cherry"),
    )
    private val taskId = "12345"

    @Before
    fun setup() {
        coEvery { explorerRepository.loadFilesystems() } returns filesystems
        coEvery { explorerRepository.loadBreadcrumbs(selectedFilesystem) } returns
            listOf(defaultLocation)
        coEvery { explorerRepository.listFiles(any()) } returns fileList

        every { settingsManager.filesystem } returns selectedFilesystem.uuid
        every { settingsManager.filesystem = any() } answers {
            every { settingsManager.filesystem } returns firstArg()
        }
    }

    @Test
    fun `When create file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.CREATE)

        every { explorerRepository.createFile(any(), any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        viewModel.onCreateClicked()
        viewModel.createFile("file.txt", isFolder = false)

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.createFile(defaultLocation, "file.txt", isFolder = false)
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
        viewModel.onFileSelected(fileList[0])
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
        viewModel.onFileSelected(fileList[0])
        viewModel.onDeleteClicked()
        viewModel.deleteFile()

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.deleteFiles(listOf(fileList[0]))
        }
    }

    @Test
    fun `When cut file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileTask = Task(taskId, TaskType.CUT)

        every { explorerRepository.cutFiles(any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        viewModel.onFileSelected(fileList[0])
        viewModel.onCutClicked()
        viewModel.onPasteClicked()

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.cutFiles(listOf(fileList[0]), defaultLocation)
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
        viewModel.onFileSelected(fileList[0])
        viewModel.onCopyClicked()
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
        viewModel.onFileSelected(fileList[0])
        viewModel.onCompressClicked()
        viewModel.compressFiles("archive.zip")

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.compressFiles(listOf(fileList[0]), defaultLocation, "archive.zip")
        }
    }

    @Test
    fun `When extract file clicked Then execute task`() = runTest {
        // Given
        val viewModel = createViewModel()
        val archiveFile = createFile("archive.zip")
        val fileTask = Task(taskId, TaskType.EXTRACT)

        every { explorerRepository.extractFiles(any(), any()) } returns taskId
        every { taskManager.monitor(taskId) } returns MutableStateFlow(fileTask)

        // When
        viewModel.onFileClicked(archiveFile)

        // Then
        verify(exactly = 1) { taskManager.monitor(taskId) }
        coVerify(exactly = 1) {
            explorerRepository.extractFiles(archiveFile, defaultLocation)
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
        )
    }
}