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
import com.blacksquircle.ui.feature.explorer.createFolder
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.defaultFilesystems
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.BreadcrumbState
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListFilesTest {

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

    @Before
    fun setup() {
        coEvery { explorerRepository.loadFilesystems() } returns filesystems
        coEvery { explorerRepository.loadBreadcrumbs(selectedFilesystem) } returns
            listOf(defaultLocation)
        coEvery { explorerRepository.listFiles(any()) } returns emptyList()

        every { settingsManager.filesystem } returns selectedFilesystem.uuid
        every { settingsManager.filesystem = any() } answers {
            every { settingsManager.filesystem } returns firstArg()
        }
    }

    @Test
    fun `When user opens a folder Then load file list`() = runTest {
        // Given
        val parentList = listOf(
            createFolder(name = "Documents/folder_1"),
            createFolder(name = "Documents/folder_2"),
            createFolder(name = "Documents/folder_3"),
        )
        val childList = listOf(
            createFile(name = "Documents/folder_1/test_1.txt"),
            createFile(name = "Documents/folder_1/test_2.txt"),
            createFile(name = "Documents/folder_1/test_3.txt"),
        )
        val selectedDirectory = parentList[0]

        coEvery { explorerRepository.listFiles(defaultLocation) } returns parentList
        coEvery { explorerRepository.listFiles(selectedDirectory) } returns childList

        // When
        val viewModel = createViewModel()
        viewModel.onFileClicked(selectedDirectory)

        // Then
        coVerify(exactly = 1) { explorerRepository.listFiles(selectedDirectory) }

        val breadcrumbs = listOf(
            BreadcrumbState(fileModel = defaultLocation, fileList = parentList),
            BreadcrumbState(fileModel = selectedDirectory, fileList = childList),
        )
        assertEquals(breadcrumbs, viewModel.viewState.value.breadcrumbs)
        assertEquals(breadcrumbs.size - 1, viewModel.viewState.value.selectedBreadcrumb)
    }

    @Test
    fun `When user goes back and chooses another folder Then remove breadcrumb and load files`() = runTest {
        // Given
        val fileList = listOf(
            createFolder(name = "Documents/folder_1"),
            createFolder(name = "Documents/folder_2"),
            createFolder(name = "Documents/folder_3"),
        )
        coEvery { explorerRepository.listFiles(defaultLocation) } returns fileList

        // When
        val viewModel = createViewModel()
        viewModel.onFileClicked(fileList[0])
        viewModel.onHomeClicked()

        // Then
        val breadcrumbs1 = listOf(
            BreadcrumbState(fileModel = defaultLocation, fileList = fileList),
            BreadcrumbState(fileModel = fileList[0], fileList = emptyList()),
        )
        assertEquals(breadcrumbs1, viewModel.viewState.value.breadcrumbs)
        assertEquals(0, viewModel.viewState.value.selectedBreadcrumb)

        coVerify(exactly = 1) { explorerRepository.listFiles(fileList[0]) }

        // When
        viewModel.onFileClicked(fileList[1])

        // Then
        val breadcrumbs2 = listOf(
            BreadcrumbState(fileModel = defaultLocation, fileList = fileList),
            BreadcrumbState(fileModel = fileList[1], fileList = emptyList()),
        )
        assertEquals(breadcrumbs2, viewModel.viewState.value.breadcrumbs)
        assertEquals(1, viewModel.viewState.value.selectedBreadcrumb)

        coVerify(exactly = 1) { explorerRepository.listFiles(fileList[1]) }
    }

    @Test
    fun `When home clicked Then reload list`() = runTest {
        // Given
        val firstFolder = defaultLocation
        val secondFolder = createFolder(name = "Documents")
        val thirdFolder = createFolder(name = "Documents/Projects")

        // When
        val viewModel = createViewModel()
        viewModel.onFileClicked(secondFolder)
        viewModel.onFileClicked(thirdFolder)
        viewModel.onHomeClicked()

        // Then
        coVerify(exactly = 2) { explorerRepository.listFiles(firstFolder) }
        coVerify(exactly = 1) { explorerRepository.listFiles(secondFolder) }
        coVerify(exactly = 1) { explorerRepository.listFiles(thirdFolder) }

        val breadcrumbs = listOf(firstFolder, secondFolder, thirdFolder).map(::BreadcrumbState)
        assertEquals(breadcrumbs, viewModel.viewState.value.breadcrumbs)
        assertEquals(0, viewModel.viewState.value.selectedBreadcrumb)
    }

    @Test
    fun `When home clicked when already selected Then skip reloading`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onHomeClicked()

        // Then
        coVerify(exactly = 1) { explorerRepository.listFiles(defaultLocation) }
    }

    @Test
    fun `When breadcrumb clicked Then select breadcrumb`() = runTest {
        // Given
        val firstFolder = defaultLocation
        val secondFolder = createFolder(name = "Documents")
        val thirdFolder = createFolder(name = "Documents/Projects")

        // When
        val viewModel = createViewModel()
        viewModel.onFileClicked(secondFolder)
        viewModel.onFileClicked(thirdFolder)
        viewModel.onBreadcrumbClicked(BreadcrumbState(secondFolder))

        // Then
        coVerify(exactly = 1) { explorerRepository.listFiles(firstFolder) }
        coVerify(exactly = 2) { explorerRepository.listFiles(secondFolder) }
        coVerify(exactly = 1) { explorerRepository.listFiles(thirdFolder) }

        val breadcrumbs = listOf(firstFolder, secondFolder, thirdFolder).map(::BreadcrumbState)
        assertEquals(breadcrumbs, viewModel.viewState.value.breadcrumbs)
        assertEquals(1, viewModel.viewState.value.selectedBreadcrumb)
    }

    @Test
    fun `When breadcrumb is already selected Then skip reloading`() = runTest {
        // Given
        val viewModel = createViewModel()
        val breadcrumb = BreadcrumbState(defaultLocation)

        // When
        viewModel.onBreadcrumbClicked(breadcrumb)

        // Then
        coVerify(exactly = 1) { explorerRepository.listFiles(defaultLocation) }
    }

    @Test
    fun `When refresh clicked Then always reload file list`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onRefreshClicked()

        // Then
        coVerify(exactly = 2) { explorerRepository.listFiles(defaultLocation) }
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