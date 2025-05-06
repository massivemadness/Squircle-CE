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

package com.blacksquircle.ui.feature.editor.ui

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.provider.typeface.TypefaceProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.createDocument
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.github.rosemoe.sora.text.Content
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UndoRedoTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val documentRepository = mockk<DocumentRepository>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)
    private val fontsInteractor = mockk<FontsInteractor>(relaxed = true)
    private val gitInteractor = mockk<GitInteractor>(relaxed = true)
    private val shortcutsInteractor = mockk<ShortcutsInteractor>(relaxed = true)
    private val languageInteractor = mockk<LanguageInteractor>(relaxed = true)

    @Before
    fun setup() {
        mockkObject(TypefaceProvider)
        every { TypefaceProvider.DEFAULT } returns mockk()
    }

    @Test
    fun `When undo clicked Then undo changes`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns false

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onUndoClicked()

        // Then
        coVerify(exactly = 1) { content.undo() }
    }

    @Test
    fun `When undo clicked with read only mode Then do nothing`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns true

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onUndoClicked()

        // Then
        coVerify(exactly = 0) { content.undo() }
    }

    @Test
    fun `When redo clicked Then redo changes`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns false

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onRedoClicked()

        // Then
        coVerify(exactly = 1) { content.redo() }
    }

    @Test
    fun `When redo clicked with read only mode Then do nothing`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns true

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onRedoClicked()

        // Then
        coVerify(exactly = 0) { content.redo() }
    }

    @Test
    fun `When closing unselected file Then preserve undo history`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
        )
        val selected = documentList[0]
        val selectedContent = mockk<Content>(relaxed = true).apply {
            every { canUndo() } returns true
            every { canRedo() } returns true
        }

        val unselected = documentList[1]
        val unselectedContent = mockk<Content>(relaxed = true).apply {
            every { canUndo() } returns true
            every { canRedo() } returns false
        }

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(selected) } returns selectedContent
        coEvery { documentRepository.loadDocument(unselected) } returns unselectedContent

        // When
        val viewModel = createViewModel()
        viewModel.onCloseClicked(unselected)

        // Then
        assertEquals(true, viewModel.viewState.value.canUndo)
        assertEquals(true, viewModel.viewState.value.canRedo)
    }

    @Test
    fun `When closing selected file Then reload undo history`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
        )
        val selected = documentList[0]
        val selectedContent = mockk<Content>(relaxed = true).apply {
            every { canUndo() } returns true
            every { canRedo() } returns true
        }

        val unselected = documentList[1]
        val unselectedContent = mockk<Content>(relaxed = true).apply {
            every { canUndo() } returns true
            every { canRedo() } returns false
        }

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(selected) } returns selectedContent
        coEvery { documentRepository.loadDocument(unselected.copy(position = 0)) } returns
            unselectedContent

        // When
        val viewModel = createViewModel()
        viewModel.onCloseClicked(selected)

        // Then
        assertEquals(true, viewModel.viewState.value.canUndo)
        assertEquals(false, viewModel.viewState.value.canRedo)
    }

    private fun createViewModel(): EditorViewModel {
        return EditorViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            documentRepository = documentRepository,
            editorInteractor = editorInteractor,
            fontsInteractor = fontsInteractor,
            gitInteractor = gitInteractor,
            shortcutsInteractor = shortcutsInteractor,
            languageInteractor = languageInteractor,
        )
    }
}