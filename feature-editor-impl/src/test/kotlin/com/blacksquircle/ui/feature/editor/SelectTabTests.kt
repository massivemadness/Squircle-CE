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

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.feature.editor.domain.model.DocumentContent
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorViewState
import com.blacksquircle.ui.feature.editor.ui.mvi.ToolbarViewState
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.settings.domain.repository.SettingsRepository
import com.blacksquircle.ui.feature.shortcuts.domain.repository.ShortcutsRepository
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.blacksquircle.ui.filesystem.base.model.FileModel
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SelectTabTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val settingsManager = mockk<SettingsManager>()
    private val documentRepository = mockk<DocumentRepository>()
    private val themesRepository = mockk<ThemesRepository>()
    private val fontsRepository = mockk<FontsRepository>()
    private val shortcutsRepository = mockk<ShortcutsRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    @Before
    fun setup() {
        every { settingsManager.extendedKeyboard } returns true
        every { settingsManager.autoSaveFiles } returns false
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
    fun `When selecting tab Then load document content`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[0]
        val expected = documentList[1]
        val undoStack = mockk<UndoStack>()
        val redoStack = mockk<UndoStack>()
        val selectedContent = DocumentContent(selected, undoStack, redoStack, "Text of first.txt")
        val expectedContent = DocumentContent(expected, undoStack, redoStack, "Text of second.txt")

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns selectedContent
        coEvery { documentRepository.loadFile(expected) } returns expectedContent

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.SelectTab(1))

        // Then
        val toolbarViewState = ToolbarViewState.ActionBar(documentList, 1)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)

        val editorViewState = EditorViewState.Content(expectedContent)
        assertEquals(editorViewState, viewModel.editorViewState.value)
    }

    @Test
    fun `When opening a file that is in the documents list Then select existing tab`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[0]

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = editorViewModel()
        val fileModel = FileModel(documentList[1].fileUri, "local")
        viewModel.obtainEvent(EditorIntent.OpenFile(fileModel))

        // Then
        val toolbarViewState = ToolbarViewState.ActionBar(documentList, 1)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When opening a file that isn't in the documents list Then select a new tab`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
        )
        val selected = documentList[0]

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = editorViewModel()
        val document = createDocument(position = 2, fileName = "third.txt")
        val fileModel = FileModel(document.fileUri, "local")
        viewModel.obtainEvent(EditorIntent.OpenFile(fileModel))

        // Then
        val expectedViewState = ToolbarViewState.ActionBar(documentList + document, 2)
        val actualViewState = viewModel.toolbarViewState.value as ToolbarViewState.ActionBar

        assertEquals(expectedViewState.position, actualViewState.position)
        assertEquals(expectedViewState.documents.size, actualViewState.documents.size)

        assertEquals(expectedViewState.documents[0].fileUri, actualViewState.documents[0].fileUri)
        assertEquals(expectedViewState.documents[0].position, actualViewState.documents[0].position)

        assertEquals(expectedViewState.documents[1].fileUri, actualViewState.documents[1].fileUri)
        assertEquals(expectedViewState.documents[1].position, actualViewState.documents[1].position)

        assertEquals(expectedViewState.documents[2].fileUri, actualViewState.documents[2].fileUri)
        assertEquals(expectedViewState.documents[2].position, actualViewState.documents[2].position)
    }

    private fun editorViewModel(): EditorViewModel {
        return EditorViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            documentRepository = documentRepository,
            themesRepository = themesRepository,
            fontsRepository = fontsRepository,
            shortcutsRepository = shortcutsRepository,
            settingsRepository = settingsRepository,
        )
    }
}