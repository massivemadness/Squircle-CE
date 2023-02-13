/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor

import android.util.Log
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.feature.editor.data.utils.Panel
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.viewstate.ToolbarViewState
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class MoveTabTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val stringProvider = mockk<StringProvider>()
    private val settingsManager = mockk<SettingsManager>()
    private val documentRepository = mockk<DocumentRepository>()
    private val themesRepository = mockk<ThemesRepository>()

    @Before
    fun setup() {
        mockkStatic(Log::class) // TODO Timber?
        every { Log.e(any(), any(), any()) } answers { println(arg<Exception>(2).message); 0 }

        mockkStatic(UUID::class) // TODO remove UUID in EditorViewState
        every { UUID.randomUUID() } returns UUID(0, 0)

        every { settingsManager.extendedKeyboard } returns true
        every { settingsManager.selectedUuid = any() } returns Unit
        every { settingsManager.selectedUuid } returns ""

        coEvery { documentRepository.loadDocuments() } returns emptyList()
        coEvery { documentRepository.updateDocument(any()) } returns Unit
        coEvery { documentRepository.deleteDocument(any()) } returns Unit

        coEvery { documentRepository.loadFile(any()) } returns mockk()
        coEvery { documentRepository.saveFile(any(), any()) } returns Unit
        coEvery { documentRepository.saveFileAs(any(), any()) } returns Unit
    }

    @Test
    fun `When moving selected tab from 1 to 3 position Then check documents order`() = runTest {
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[0] // selected "first.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns mockk()

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.MoveTab(0, 2))

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "second.txt"),
            createDocument(position = 1, fileName = "third.txt"),
            createDocument(position = 2, fileName = "first.txt"),
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 2, Panel.DEFAULT)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When moving selected tab from 3 to 1 position Then check documents order`() = runTest {
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[2] // selected "third.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns mockk()

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.MoveTab(2, 0))

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "third.txt"),
            createDocument(position = 1, fileName = "first.txt"),
            createDocument(position = 2, fileName = "second.txt"),
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 0, Panel.DEFAULT)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When moving unselected tab from 1 to 2 position Then check documents order`() = runTest {
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[1] // selected "second.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns mockk()

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.MoveTab(0, 1))

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "second.txt"),
            createDocument(position = 1, fileName = "first.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 0, Panel.DEFAULT)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When moving unselected tab from 3 to 2 position Then check documents order`() = runTest {
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[1] // selected "second.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns mockk()

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.MoveTab(2, 1))

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "third.txt"),
            createDocument(position = 2, fileName = "second.txt"),
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 2, Panel.DEFAULT)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When moving unselected tab from 2 to 3 position Then check documents order`() = runTest {
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[0] // selected "first.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns mockk()

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.MoveTab(1, 2))

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "third.txt"),
            createDocument(position = 2, fileName = "second.txt"),
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 0, Panel.DEFAULT)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When moving unselected tab from 2 to 1 position Then check documents order`() = runTest {
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[2] // selected "third.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns mockk()

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.MoveTab(1, 0))

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "second.txt"),
            createDocument(position = 1, fileName = "first.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 2, Panel.DEFAULT)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    private fun editorViewModel(): EditorViewModel {
        return EditorViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            documentRepository = documentRepository,
            themesRepository = themesRepository,
        )
    }
}