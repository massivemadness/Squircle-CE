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
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.defaultWorkspaces
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewEvent
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OpenFileTest {

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
    fun `When text file clicked Then open it in the editor`() = runTest {
        // Given
        val fileModel = createFile(name = "file.txt")
        coEvery { explorerRepository.listFiles(defaultLocation) } returns listOf(fileModel)

        // When
        val viewModel = createViewModel()
        val fileNode = viewModel.viewState.value.fileNodes[1]
        viewModel.onFileClicked(fileNode)

        // Then
        coVerify(exactly = 1) { editorInteractor.openFile(fileModel) }
    }

    @Test
    fun `When image file clicked Then open system application chooser`() = runTest {
        // Given
        val fileModel = createFile(name = "file.png")
        coEvery { explorerRepository.listFiles(defaultLocation) } returns listOf(fileModel)

        // When
        val viewModel = createViewModel()
        val fileNode = viewModel.viewState.value.fileNodes[1]
        viewModel.onFileClicked(fileNode)

        // Then
        val expected = ExplorerViewEvent.OpenFileWith(fileModel)
        assertEquals(expected, viewModel.viewEvent.first())
        coVerify(exactly = 0) { editorInteractor.openFile(fileModel) }
    }

    @Test
    fun `When image file selected Then open system application chooser`() = runTest {
        // Given
        val fileModel = createFile(name = "file.png")
        coEvery { explorerRepository.listFiles(defaultLocation) } returns listOf(fileModel)

        // When
        val viewModel = createViewModel()
        val fileNode = viewModel.viewState.value.fileNodes[1]
        viewModel.onFileSelected(fileNode)
        viewModel.onOpenWithClicked()

        // Then
        val expected = ExplorerViewEvent.OpenFileWith(fileModel)
        assertEquals(expected, viewModel.viewEvent.first())
        coVerify(exactly = 0) { editorInteractor.openFile(fileModel) }
    }

    @Test
    fun `When copy path clicked Then send copy path event`() = runTest {
        // Given
        val fileModel = createFile(name = "file.txt")
        coEvery { explorerRepository.listFiles(defaultLocation) } returns listOf(fileModel)

        // When
        val viewModel = createViewModel()
        val fileNode = viewModel.viewState.value.fileNodes[1]
        viewModel.onFileSelected(fileNode)
        viewModel.onCopyPathClicked()

        // Then
        val expected = ExplorerViewEvent.CopyPath(fileModel)
        assertEquals(expected, viewModel.viewEvent.first())
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