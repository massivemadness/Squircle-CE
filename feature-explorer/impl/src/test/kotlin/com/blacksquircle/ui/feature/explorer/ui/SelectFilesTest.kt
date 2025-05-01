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
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.defaultFilesystems
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SelectFilesTest {

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

    private val defaultLocation = selectedFilesystem.defaultLocation
    private val fileList = listOf(
        createFile(name = "Apple"),
        createFile(name = "Banana"),
        createFile(name = "Cherry"),
    )

    @Before
    fun setup() {
        coEvery { explorerRepository.loadFilesystems() } returns filesystems
        coEvery { explorerRepository.listFiles(defaultLocation) } returns fileList

        every { settingsManager.filesystem } returns selectedFilesystem.uuid
        every { settingsManager.filesystem = any() } answers {
            every { settingsManager.filesystem } returns firstArg()
        }
    }

    @Test
    fun `When selecting file Then update selection list`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onFileSelected(fileList[0])
        viewModel.onFileSelected(fileList[1])

        // Then
        val expected = listOf(fileList[0], fileList[1])
        assertEquals(expected, viewModel.viewState.value.selectedFiles)
    }

    @Test
    fun `When selection mode active Then click selects a file`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onFileSelected(fileList[0])
        viewModel.onFileClicked(fileList[1])
        viewModel.onFileClicked(fileList[2])

        // Then
        assertEquals(fileList, viewModel.viewState.value.selectedFiles)
    }

    @Test
    fun `When selection mode active Then click removes a file from selection`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onFileSelected(fileList[0])
        viewModel.onFileClicked(fileList[1])
        viewModel.onFileClicked(fileList[1])

        // Then
        val expected = listOf(fileList[0])
        assertEquals(expected, viewModel.viewState.value.selectedFiles)
    }

    @Test
    fun `When selection mode active Then back press stops selection mode`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onFileSelected(fileList[0])
        viewModel.onFileSelected(fileList[1])
        viewModel.onFileSelected(fileList[2])
        viewModel.onBackClicked()

        // Then
        assertEquals(emptyList<FileModel>(), viewModel.viewState.value.selectedFiles)
    }

    @Test
    fun `When selection mode inactive Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onBackClicked()

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
    }

    @Test
    fun `When select all clicked Then select all files`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onSelectAllClicked()

        // Then
        assertEquals(fileList, viewModel.viewState.value.selectedFiles)
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