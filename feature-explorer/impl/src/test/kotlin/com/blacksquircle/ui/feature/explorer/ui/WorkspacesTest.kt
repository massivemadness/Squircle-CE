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
import com.blacksquircle.ui.feature.explorer.createFileNode
import com.blacksquircle.ui.feature.explorer.createWorkspace
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.defaultWorkspaces
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WorkspacesTest {

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
    fun `When screen opens Then load workspaces`() = runTest {
        // When
        val viewModel = createViewModel() // init {}

        // Then
        assertEquals(workspaces, viewModel.viewState.value.workspaces)
        assertEquals(selectedWorkspace, viewModel.viewState.value.selectedWorkspace)

        coVerify(exactly = 1) { explorerRepository.loadWorkspaces() }
        coVerify(exactly = 1) { explorerRepository.listFiles(selectedWorkspace.defaultLocation) }
    }

    @Test
    fun `When workspace is already selected Then ignore`() = runTest {
        // Given
        val viewModel = createViewModel()
        clearMocks(explorerRepository, answers = false, recordedCalls = true) // reset verify count
        clearMocks(settingsManager, answers = false, recordedCalls = true)

        // When
        viewModel.onWorkspaceClicked(selectedWorkspace) // already selected

        // Then
        assertEquals(workspaces, viewModel.viewState.value.workspaces)
        assertEquals(selectedWorkspace, viewModel.viewState.value.selectedWorkspace)

        verify(exactly = 0) { settingsManager.workspace = selectedWorkspace.uuid }
        coVerify(exactly = 0) { explorerRepository.listFiles(selectedWorkspace.defaultLocation) }
    }

    @Test
    fun `When workspace changed Then reload file list`() = runTest {
        // Given
        val viewModel = createViewModel()
        val nextSelected = workspaces[1]

        // When
        viewModel.onWorkspaceClicked(nextSelected)

        // Then
        assertEquals(workspaces, viewModel.viewState.value.workspaces)
        assertEquals(nextSelected, viewModel.viewState.value.selectedWorkspace)

        verify(exactly = 1) { settingsManager.workspace = nextSelected.uuid }
        coVerify(exactly = 1) { explorerRepository.listFiles(selectedWorkspace.defaultLocation) }
    }

    @Test
    fun `When workspace changed Then reset buffer`() = runTest {
        // Given
        val viewModel = createViewModel()
        val fileNode = createFileNode(name = "untitled.txt")

        // When
        viewModel.onFileSelected(fileNode)
        assertEquals(listOf(fileNode), viewModel.viewState.value.selectedNodes)

        viewModel.onDeleteClicked()
        assertEquals(TaskType.DELETE, viewModel.viewState.value.taskType)

        viewModel.onWorkspaceClicked(workspaces[1])

        // Then
        assertEquals(TaskType.CREATE, viewModel.viewState.value.taskType)
        assertEquals(emptyList<FileNode>(), viewModel.viewState.value.selectedNodes)
    }

    @Test
    fun `When workspace list changed Then reload workspaces`() = runTest {
        // Given
        val workspacesFlow = MutableStateFlow(workspaces)
        val updatedWorkspaces = workspaces + createWorkspace("new_workspace")
        coEvery { explorerRepository.loadWorkspaces() } returns workspacesFlow
        val viewModel = createViewModel()

        // When
        workspacesFlow.update {
            updatedWorkspaces
        }

        // Then
        assertEquals(updatedWorkspaces, viewModel.viewState.value.workspaces)
        coVerify(exactly = 1) { explorerRepository.loadWorkspaces() }
    }

    @Test
    fun `When selected workspace removed Then fallback to default`() = runTest {
        // Given
        val workspacesFlow = MutableStateFlow(workspaces)
        val selectedWorkspace = workspaces[1]
        val fallbackWorkspace = workspaces[0]
        val updatedWorkspaces = listOf(fallbackWorkspace)

        coEvery { explorerRepository.loadWorkspaces() } returns workspacesFlow
        every { settingsManager.workspace } returns selectedWorkspace.uuid

        val viewModel = createViewModel()

        // When
        workspacesFlow.update {
            updatedWorkspaces
        }

        // Then
        assertEquals(updatedWorkspaces, viewModel.viewState.value.workspaces)
        assertEquals(fallbackWorkspace, viewModel.viewState.value.selectedWorkspace)
        coVerify(exactly = 1) { explorerRepository.loadWorkspaces() }
        verify(exactly = 1) { settingsManager.workspace = fallbackWorkspace.uuid }
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