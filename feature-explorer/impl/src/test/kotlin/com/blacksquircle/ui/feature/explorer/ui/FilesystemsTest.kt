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
import com.blacksquircle.ui.feature.explorer.createFilesystem
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.defaultFilesystems
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.BreadcrumbState
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FilesystemsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)
    private val explorerRepository = mockk<ExplorerRepository>(relaxed = true)
    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)

    private val filesystems = defaultFilesystems()
    private val selectedFilesystem = filesystems[0]

    @Before
    fun setup() {
        coEvery { explorerRepository.loadFilesystems() } returns filesystems
        coEvery { explorerRepository.loadBreadcrumbs(selectedFilesystem) } returns
            listOf(selectedFilesystem.defaultLocation)

        every { settingsManager.filesystem } returns selectedFilesystem.uuid
        every { settingsManager.filesystem = any() } answers {
            every { settingsManager.filesystem } returns firstArg()
        }
    }

    @Test
    fun `When screen opens Then display loading state`() = runTest {
        // Given
        coEvery { explorerRepository.loadFilesystems() } coAnswers {
            delay(200)
            emptyList()
        }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        assertEquals(true, viewModel.viewState.value.isLoading)
    }

    @Test
    fun `When screen opens Then load filesystems and breadcrumbs`() = runTest {
        // When
        val viewModel = createViewModel() // init {}

        // Then
        assertEquals(filesystems, viewModel.viewState.value.filesystems)
        assertEquals(selectedFilesystem.uuid, viewModel.viewState.value.selectedFilesystem)

        val breadcrumbs = listOf(BreadcrumbState(selectedFilesystem.defaultLocation))
        assertEquals(breadcrumbs, viewModel.viewState.value.breadcrumbs)
        assertEquals(0, viewModel.viewState.value.selectedBreadcrumb)

        coVerify(exactly = 1) { explorerRepository.loadFilesystems() }
        coVerify(exactly = 1) { explorerRepository.loadBreadcrumbs(selectedFilesystem) }
    }

    @Test
    fun `When filesystems loaded Then list files called`() = runTest {
        // When
        createViewModel() // init {}

        // Then
        coVerify(exactly = 1) { explorerRepository.listFiles(selectedFilesystem.defaultLocation) }
    }

    @Test
    fun `When filesystem is already selected Then ignore`() = runTest {
        // Given
        val viewModel = createViewModel()
        clearMocks(explorerRepository, answers = false, recordedCalls = true) // reset verify count

        // When
        viewModel.onFilesystemSelected(selectedFilesystem.uuid) // already selected

        // Then
        assertEquals(filesystems, viewModel.viewState.value.filesystems)
        assertEquals(selectedFilesystem.uuid, viewModel.viewState.value.selectedFilesystem)

        verify(exactly = 0) { settingsManager.filesystem = selectedFilesystem.uuid }
        coVerify(exactly = 0) { explorerRepository.listFiles(selectedFilesystem.defaultLocation) }
    }

    @Test
    fun `When filesystem changed Then reload file list`() = runTest {
        // Given
        val viewModel = createViewModel()
        val nextSelected = filesystems[1]

        // When
        viewModel.onFilesystemSelected(nextSelected.uuid)

        // Then
        assertEquals(filesystems, viewModel.viewState.value.filesystems)
        assertEquals(nextSelected.uuid, viewModel.viewState.value.selectedFilesystem)

        verify(exactly = 1) { settingsManager.filesystem = nextSelected.uuid }
        coVerify(exactly = 1) { explorerRepository.listFiles(selectedFilesystem.defaultLocation) }
    }

    @Test
    fun `When filesystem changed Then reset buffer`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileModel = createFile("untitled.txt")

        // When
        viewModel.onFileSelected(fileModel)
        assertEquals(listOf(fileModel), viewModel.viewState.value.selectedFiles)

        viewModel.onDeleteClicked()
        assertEquals(TaskType.DELETE, viewModel.viewState.value.taskType)

        viewModel.onFilesystemSelected(filesystems[1].uuid)

        // Then
        assertEquals(TaskType.CREATE, viewModel.viewState.value.taskType)
        assertEquals(emptyList<FileModel>(), viewModel.viewState.value.selectedFiles)
    }

    @Test
    fun `When filesystem added Then load filesystems`() = runTest {
        // Given
        val viewModel = createViewModel()
        val updatedFilesystems = filesystems + createFilesystem("12345")
        clearMocks(explorerRepository, answers = false, recordedCalls = true) // reset verify count
        coEvery { explorerRepository.loadFilesystems() } returns updatedFilesystems

        // When
        viewModel.onFilesystemAdded()

        // Then
        assertEquals(updatedFilesystems, viewModel.viewState.value.filesystems)
        coVerify(exactly = 1) { explorerRepository.loadFilesystems() }
    }

    private fun createViewModel(): ExplorerViewModel {
        return ExplorerViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            taskManager = taskManager,
            editorInteractor = editorInteractor,
            explorerRepository = explorerRepository,
            serverInteractor = serverInteractor
        )
    }
}