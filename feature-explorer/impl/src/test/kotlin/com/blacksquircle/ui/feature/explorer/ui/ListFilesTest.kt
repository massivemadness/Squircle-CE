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
import com.blacksquircle.ui.feature.explorer.createNode
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.defaultWorkspaces
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListFilesTest {

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

    @Before
    fun setup() {
        coEvery { explorerRepository.loadWorkspaces() } returns flowOf(workspaces)
        coEvery { explorerRepository.listFiles(any()) } returns emptyList()

        every { settingsManager.workspace } returns selectedWorkspace.uuid
        every { settingsManager.workspace = any() } answers {
            every { settingsManager.workspace } returns firstArg()
        }
    }

    @Test
    fun `When user opens a folder Then insert file nodes`() = runTest {
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
        viewModel.onFileClicked(
            fileNode = viewModel.viewState.value.fileNodes
                .first { it.file == selectedDirectory }
        )

        // Then
        val expected = listOf(
            createNode(file = defaultLocation, depth = 0, isExpanded = true),
            createNode(file = parentList[0], depth = 1, isExpanded = true),
            createNode(file = childList[0], depth = 2),
            createNode(file = childList[1], depth = 2),
            createNode(file = childList[2], depth = 2),
            createNode(file = parentList[1], depth = 1, isExpanded = false),
            createNode(file = parentList[2], depth = 1, isExpanded = false),
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)

        coVerify(exactly = 1) { explorerRepository.listFiles(selectedDirectory) }
    }

    @Test
    fun `When user closes a folder Then remove file nodes`() = runTest {
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
        viewModel.onFileClicked(
            fileNode = viewModel.viewState.value.fileNodes
                .first { it.file == selectedDirectory }
        )
        viewModel.onFileClicked(
            fileNode = viewModel.viewState.value.fileNodes
                .first { it.file == selectedDirectory }
        )

        // Then
        val expected = listOf(
            createNode(file = defaultLocation, depth = 0, isExpanded = true),
            createNode(file = parentList[0], depth = 1, isExpanded = false),
            createNode(file = parentList[1], depth = 1, isExpanded = false),
            createNode(file = parentList[2], depth = 1, isExpanded = false),
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)

        coVerify(exactly = 1) { explorerRepository.listFiles(selectedDirectory) }
    }

    @Test
    fun `When user opens a folder with nested packages Then autoload next folder`() = runTest {
        // Given
        val parentList = listOf(
            createFolder(name = "com"),
            createFolder(name = "org"),
        )
        val subList1 = listOf(
            createFolder(name = "com/blacksquircle"),
        )
        val subList2 = listOf(
            createFolder(name = "com/blacksquircle/ui"),
        )
        val subList3 = listOf(
            createFile(name = "com/blacksquircle/ui/untitled.txt"),
        )

        coEvery { explorerRepository.listFiles(defaultLocation) } returns parentList
        coEvery { explorerRepository.listFiles(parentList[0]) } returns subList1
        coEvery { explorerRepository.listFiles(subList1[0]) } returns subList2
        coEvery { explorerRepository.listFiles(subList2[0]) } returns subList3

        every { settingsManager.compactPackages } returns true

        // When
        val viewModel = createViewModel()
        viewModel.onFileClicked(
            fileNode = viewModel.viewState.value.fileNodes
                .first { it.file == parentList[0] }
        )

        // Then
        val expected = listOf(
            createNode(
                file = defaultLocation,
                depth = 0,
                isExpanded = true
            ),
            createNode(
                file = subList2[0],
                depth = 3,
                displayName = "com/blacksquircle/ui",
                displayDepth = 1,
                isExpanded = true
            ),
            createNode(
                file = subList3[0],
                depth = 4,
                displayDepth = 2,
            ),
            createNode(
                file = parentList[1],
                depth = 1,
                isExpanded = false
            ),
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)

        coVerify(exactly = 1) { explorerRepository.listFiles(defaultLocation) }
        coVerify(exactly = 1) { explorerRepository.listFiles(parentList[0]) }
        coVerify(exactly = 1) { explorerRepository.listFiles(subList1[0]) }
        coVerify(exactly = 1) { explorerRepository.listFiles(subList2[0]) }
    }

    @Test
    fun `When refresh clicked Then reload selected directory`() = runTest {
        // Given
        val parentList = listOf(
            createFolder(name = "Documents"),
        )
        coEvery { explorerRepository.listFiles(defaultLocation) } returns parentList

        // When
        val viewModel = createViewModel()
        viewModel.onFileSelected(viewModel.viewState.value.fileNodes.first())
        viewModel.onRefreshClicked()

        // Then
        coVerify(exactly = 2) { explorerRepository.listFiles(defaultLocation) }
    }

    @Test
    fun `When refresh clicked with opened subfolder Then keep subfolder opened`() = runTest {
        // Given
        val parentList = listOf(
            createFolder(name = "Documents"),
            createFolder(name = "Downloads"),
        )
        val childList = listOf(
            createFile(name = "Documents/untitled.txt"),
        )
        val selectedDirectory = parentList[0]

        coEvery { explorerRepository.listFiles(defaultLocation) } returns parentList
        coEvery { explorerRepository.listFiles(selectedDirectory) } returns childList

        // When
        val viewModel = createViewModel()
        viewModel.onFileClicked(
            fileNode = viewModel.viewState.value.fileNodes
                .first { it.file == selectedDirectory }
        )
        viewModel.onFileSelected(viewModel.viewState.value.fileNodes.first())
        viewModel.onRefreshClicked()

        // Then
        val expected = listOf(
            createNode(file = defaultLocation, depth = 0, isExpanded = true),
            createNode(file = parentList[0], depth = 1, isExpanded = true),
            createNode(file = childList[0], depth = 2),
            createNode(file = parentList[1], depth = 1, isExpanded = false),
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)

        coVerify(exactly = 2) { explorerRepository.listFiles(defaultLocation) }
        coVerify(exactly = 1) { explorerRepository.listFiles(selectedDirectory) }
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