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

package com.blacksquircle.ui.feature.explorer

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.model.Task
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.BreadcrumbState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.servers.api.interactor.ServersInteractor
import com.blacksquircle.ui.filesystem.base.exception.PermissionException
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListFilesTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val settingsManager = mockk<SettingsManager>()
    private val taskManager = mockk<TaskManager>()
    private val explorerRepository = mockk<ExplorerRepository>()
    private val editorInteractor = mockk<EditorInteractor>()
    private val serversInteractor = mockk<ServersInteractor>()

    @Before
    fun setup() {
        every { stringProvider.getString(R.string.message_access_denied) } returns "Access denied"
        every { stringProvider.getString(R.string.message_access_required) } returns "Access required"
        every { stringProvider.getString(R.string.storage_local) } returns "Local Storage"
        every { stringProvider.getString(R.string.storage_root) } returns "Root Directory"
        every { stringProvider.getString(R.string.storage_add) } returns "Add Server"

        every { settingsManager.showHidden } returns true
        every { settingsManager.showHidden = any() } just Runs
        every { settingsManager.foldersOnTop } returns true
        every { settingsManager.foldersOnTop = any() } just Runs
        every { settingsManager.viewMode } returns "compact_list"
        every { settingsManager.viewMode = any() } just Runs
        every { settingsManager.sortMode } returns "sort_by_name"
        every { settingsManager.sortMode = any() } just Runs
        every { settingsManager.filesystem } returns LocalFilesystem.LOCAL_UUID
        every { settingsManager.filesystem = any() } just Runs

        every { taskManager.monitor(any()) } returns MutableStateFlow(Task("", TaskType.CREATE))

        every { explorerRepository.createFile(any(), any(), any()) } returns ""
        every { explorerRepository.renameFile(any(), any()) } returns ""
        every { explorerRepository.deleteFiles(any()) } returns ""
        every { explorerRepository.copyFiles(any(), any()) } returns ""
        every { explorerRepository.cutFiles(any(), any()) } returns ""
        every { explorerRepository.compressFiles(any(), any(), any()) } returns ""
        every { explorerRepository.extractFiles(any(), any()) } returns ""

        coEvery { serversInteractor.loadServers() } returns emptyList()
        coEvery { explorerRepository.loadFilesystems() } returns defaultFilesystems()
        coEvery { explorerRepository.loadBreadcrumbs(any()) } coAnswers {
            val filesystemModel = args.first() as FilesystemModel
            listOf(filesystemModel.defaultLocation)
        }
    }

    @Test
    fun `When the user opens the app Then load default directory and select tab`() = runTest {
        // Given
        val defaultLocation = createFilesystem().defaultLocation
        val fileList = listOf(
            createFolder(fileName = "Documents/first"),
            createFolder(fileName = "Documents/second"),
            createFolder(fileName = "Documents/third"),
        )
        coEvery { explorerRepository.listFiles(defaultLocation) } returns fileList

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = createViewState().copy(
            breadcrumbs = listOf(
                BreadcrumbState(
                    fileModel = defaultLocation,
                    fileList = fileList,
                )
            ),
            selectedBreadcrumb = 0,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When opening a folder Then load files and select tab`() = runTest {
        // Given
        val defaultLocation = createFilesystem().defaultLocation
        val rootFiles = listOf(
            createFolder(fileName = "Documents/folder_1"),
            createFolder(fileName = "Documents/folder_2"),
            createFolder(fileName = "Documents/folder_3"),
        )
        val dirFiles = listOf(
            createFile(fileName = "Documents/folder_1/test_1.txt"),
            createFile(fileName = "Documents/folder_1/test_2.txt"),
            createFile(fileName = "Documents/folder_1/test_3.txt"),
        )
        coEvery { explorerRepository.listFiles(defaultLocation) } returns rootFiles
        coEvery { explorerRepository.listFiles(rootFiles[0]) } returns dirFiles

        // When
        val viewModel = createViewModel() // init {}
        viewModel.onFileClicked(rootFiles[0])

        // Then
        val viewState = createViewState().copy(
            breadcrumbs = listOf(
                BreadcrumbState(
                    fileModel = defaultLocation,
                    fileList = rootFiles,
                ),
                BreadcrumbState(
                    fileModel = rootFiles[0],
                    fileList = dirFiles,
                ),
            ),
            selectedBreadcrumb = 1,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When opening a folder Then display loading state`() = runTest {
        // Given
        val defaultLocation = createFilesystem().defaultLocation
        coEvery { explorerRepository.listFiles(defaultLocation) } coAnswers {
            delay(200)
            emptyList()
        }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = createViewState().copy(
            breadcrumbs = listOf(BreadcrumbState(defaultLocation)),
            selectedBreadcrumb = 0,
            isLoading = true,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When opening a folder without storage access Then display permission state`() = runTest {
        // Given
        val defaultLocation = createFilesystem().defaultLocation
        coEvery { explorerRepository.listFiles(defaultLocation) } throws PermissionException()

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = createViewState().copy(
            breadcrumbs = listOf(
                BreadcrumbState(
                    fileModel = defaultLocation,
                    fileList = emptyList(),
                    errorState = ErrorState(
                        title = "Access denied",
                        subtitle = "Access required",
                        action = ErrorAction.REQUEST_PERMISSIONS,
                    )
                ),
            ),
            selectedBreadcrumb = 0,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    private fun createViewModel(): ExplorerViewModel {
        return ExplorerViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            taskManager = taskManager,
            editorInteractor = editorInteractor,
            explorerRepository = explorerRepository,
            serversInteractor = serversInteractor,
        )
    }

    private fun createViewState(): ExplorerViewState {
        return ExplorerViewState(
            filesystems = defaultFilesystems(),
            selectedFilesystem = LocalFilesystem.LOCAL_UUID,
            selectedBreadcrumb = -1,
            isLoading = false,
        )
    }
}