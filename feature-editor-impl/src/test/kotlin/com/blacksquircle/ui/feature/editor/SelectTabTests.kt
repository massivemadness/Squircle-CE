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

import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.feature.editor.data.utils.Panel
import com.blacksquircle.ui.feature.editor.domain.model.DocumentContent
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.viewstate.EditorViewState
import com.blacksquircle.ui.feature.editor.ui.viewstate.ToolbarViewState
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SelectTabTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val settingsManager = mockk<SettingsManager>()
    private val documentRepository = mockk<DocumentRepository>()
    private val themesRepository = mockk<ThemesRepository>()

    @Before
    fun setup() {
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
    fun `When the user opens the app Then load documents list and select tab`() = runTest {
        val documentList = listOf(
            createDocument(position = 0, fileName = "first.txt"),
            createDocument(position = 1, fileName = "second.txt"),
            createDocument(position = 2, fileName = "third.txt"),
        )
        val selected = documentList[0]
        val undoStack = mockk<UndoStack>()
        val redoStack = mockk<UndoStack>()
        val text = "Text of first.txt"
        val documentContent = DocumentContent(selected, undoStack, redoStack, text)

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadFile(selected) } returns documentContent

        // When
        val viewModel = editorViewModel()
        viewModel.obtainEvent(EditorIntent.LoadFiles)

        // Then
        val toolbarViewState = ToolbarViewState.ActionBar(documentList, 0, Panel.DEFAULT)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)

        val editorViewState = EditorViewState.Content(
            DocumentContent(documentList[toolbarViewState.position], undoStack, redoStack, text),
            showKeyboard = true
        )
        assertEquals(editorViewState, viewModel.editorViewState.value)
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