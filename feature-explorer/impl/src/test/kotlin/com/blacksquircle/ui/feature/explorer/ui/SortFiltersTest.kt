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
import com.blacksquircle.ui.feature.explorer.createNode
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.defaultWorkspaces
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SortFiltersTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val dispatcherProvider = TestDispatcherProvider()
    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)
    private val explorerRepository = mockk<ExplorerRepository>(relaxed = true)
    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)
    private val asyncNodeBuilder = AsyncNodeBuilder(dispatcherProvider)

    private val workspaces = defaultWorkspaces()
    private val selectedWorkspace = workspaces[0]

    private val defaultLocation = selectedWorkspace.defaultLocation
    private val fileList = listOf(
        createFile(name = ".nomedia", size = 0, lastModified = 1000),
        createFile(name = "Apple", size = 100, lastModified = 2000),
        createFile(name = "Banana", size = 200, lastModified = 1500),
        createFile(name = "Cherry", size = 300, lastModified = 900),
    )
    private val fileNodes = listOf(
        createNode(file = defaultLocation, depth = 0, isExpanded = true),
        createNode(file = fileList[0], depth = 1),
        createNode(file = fileList[1], depth = 1),
        createNode(file = fileList[2], depth = 1),
        createNode(file = fileList[3], depth = 1),
    )

    @Before
    fun setup() {
        coEvery { explorerRepository.loadWorkspaces() } returns flowOf(workspaces)
        coEvery { explorerRepository.listFiles(defaultLocation) } returns fileList

        every { settingsManager.workspace } returns selectedWorkspace.uuid
        every { settingsManager.workspace = any() } answers {
            every { settingsManager.workspace } returns firstArg()
        }
        every { settingsManager.showHidden } returns true
        every { settingsManager.showHidden = any() } answers {
            every { settingsManager.showHidden } returns firstArg()
        }
        every { settingsManager.sortMode } returns SortMode.SORT_BY_NAME.value
        every { settingsManager.sortMode = any() } answers {
            every { settingsManager.sortMode } returns firstArg()
        }
    }

    @Test
    fun `When changing search query Then filter out files`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onQueryChanged("Apple")

        // Then
        val expected = listOf(
            fileNodes[0],
            fileNodes[2],
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)
    }

    @Test
    fun `When clearing search query Then return all files`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onQueryChanged("Apple")
        viewModel.onClearQueryClicked()

        // Then
        val expected = fileNodes
        assertEquals(expected, viewModel.viewState.value.fileNodes)
    }

    @Test
    fun `When show hidden disabled Then remove hidden files from list`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onShowHiddenClicked()

        // Then
        val expected = listOf(
            fileNodes[0],
            fileNodes[2],
            fileNodes[3],
            fileNodes[4],
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)
    }

    @Test
    fun `When sort mode changed Then sort files by size`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onSortModeSelected(SortMode.SORT_BY_SIZE)

        // Then
        val expected = listOf(
            fileNodes[0],
            fileNodes[4],
            fileNodes[3],
            fileNodes[2],
            fileNodes[1],
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)
    }

    @Test
    fun `When sort mode changed Then sort files by date`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onSortModeSelected(SortMode.SORT_BY_DATE)

        // Then
        val expected = listOf(
            fileNodes[0],
            fileNodes[2],
            fileNodes[3],
            fileNodes[1],
            fileNodes[4],
        )
        assertEquals(expected, viewModel.viewState.value.fileNodes)
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