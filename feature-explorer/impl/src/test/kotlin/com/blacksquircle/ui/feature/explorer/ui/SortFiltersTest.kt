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
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SortFiltersTest {

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
        createFile(name = ".nomedia", size = 0, lastModified = 1000),
        createFile(name = "Apple", size = 100, lastModified = 2000),
        createFile(name = "Banana", size = 200, lastModified = 1500),
        createFile(name = "Cherry", size = 300, lastModified = 900),
    )

    @Before
    fun setup() {
        coEvery { explorerRepository.loadFilesystems() } returns filesystems
        coEvery { explorerRepository.listFiles(defaultLocation) } returns fileList

        every { settingsManager.filesystem } returns selectedFilesystem.uuid
        every { settingsManager.filesystem = any() } answers {
            every { settingsManager.filesystem } returns firstArg()
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
        val breadcrumb = viewModel.viewState.value.breadcrumbs.first()
        val expected = listOf(
            createFile(name = "Apple", size = 100, lastModified = 2000),
        )
        assertEquals(expected, breadcrumb.fileList)
    }

    @Test
    fun `When clearing search query Then return all files`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onQueryChanged("Apple")
        viewModel.onClearQueryClicked()

        // Then
        val breadcrumb = viewModel.viewState.value.breadcrumbs.first()
        assertEquals(fileList, breadcrumb.fileList)
    }

    @Test
    fun `When show hidden disabled Then remove hidden files from list`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onShowHiddenClicked()

        // Then
        val breadcrumb = viewModel.viewState.value.breadcrumbs.first()
        val expected = listOf(
            createFile(name = "Apple", size = 100, lastModified = 2000),
            createFile(name = "Banana", size = 200, lastModified = 1500),
            createFile(name = "Cherry", size = 300, lastModified = 900),
        )
        assertEquals(expected, breadcrumb.fileList)
    }

    @Test
    fun `When sort mode changed Then sort files by size`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onSortModeSelected(SortMode.SORT_BY_SIZE)

        // Then
        val breadcrumb = viewModel.viewState.value.breadcrumbs.first()
        val expected = listOf(
            createFile(name = "Cherry", size = 300, lastModified = 900),
            createFile(name = "Banana", size = 200, lastModified = 1500),
            createFile(name = "Apple", size = 100, lastModified = 2000),
            createFile(name = ".nomedia", size = 0, lastModified = 1000),
        )
        assertEquals(expected, breadcrumb.fileList)
    }

    @Test
    fun `When sort mode changed Then sort files by date`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onSortModeSelected(SortMode.SORT_BY_DATE)

        // Then
        val breadcrumb = viewModel.viewState.value.breadcrumbs.first()
        val expected = listOf(
            createFile(name = "Apple", size = 100, lastModified = 2000),
            createFile(name = "Banana", size = 200, lastModified = 1500),
            createFile(name = ".nomedia", size = 0, lastModified = 1000),
            createFile(name = "Cherry", size = 300, lastModified = 900),
        )
        assertEquals(expected, breadcrumb.fileList)
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