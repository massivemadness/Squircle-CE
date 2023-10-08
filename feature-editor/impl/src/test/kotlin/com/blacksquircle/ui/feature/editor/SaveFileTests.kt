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
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.mvi.ToolbarViewState
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.settings.domain.repository.SettingsRepository
import com.blacksquircle.ui.feature.shortcuts.domain.repository.ShortcutsRepository
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SaveFileTests {

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
    fun `When modifying the text Then set modified status true`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(position = 0, fileName = "dirty.txt", modified = false)
        )
        val selected = documentList[0]

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.ModifyContent)

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "dirty.txt", modified = true)
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 0)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When saving the file to local storage Then set modified status false`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(position = 0, fileName = "dirty.txt", modified = true)
        )
        val selected = documentList[0]

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = editorViewModel()
        val intent = EditorIntent.SaveFile(
            local = true,
            unselected = false,
            text = "whatever",
            undoStack = UndoStack(),
            redoStack = UndoStack(),
            scrollX = selected.scrollX,
            scrollY = selected.scrollY,
            selectionStart = selected.selectionStart,
            selectionEnd = selected.selectionEnd,
        )
        viewModel.obtainEvent(intent)

        // Then
        val updatedList = listOf(
            createDocument(position = 0, fileName = "dirty.txt", modified = false)
        )
        val toolbarViewState = ToolbarViewState.ActionBar(updatedList, 0)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
    }

    @Test
    fun `When saving the file to cache Then keep modified status false`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(position = 0, fileName = "dirty.txt", modified = true)
        )
        val selected = documentList[0]

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = editorViewModel()
        val intent = EditorIntent.SaveFile(
            local = false,
            unselected = false,
            text = "whatever",
            undoStack = UndoStack(),
            redoStack = UndoStack(),
            scrollX = selected.scrollX,
            scrollY = selected.scrollY,
            selectionStart = selected.selectionStart,
            selectionEnd = selected.selectionEnd,
        )
        viewModel.obtainEvent(intent)

        // Then
        val toolbarViewState = ToolbarViewState.ActionBar(documentList, 0)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)
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