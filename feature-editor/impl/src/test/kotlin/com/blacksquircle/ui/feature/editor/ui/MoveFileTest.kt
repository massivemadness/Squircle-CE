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
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.github.rosemoe.sora.text.Content
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MoveFileTest {

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
    fun `When moving selected tab from 1 to 3 position Then check documents order`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[0] // selected "first.txt"
        val content = Content()

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentMoved(from = 0, to = 2)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "2", fileName = "second.txt", position = 0),
            createDocument(uuid = "3", fileName = "third.txt", position = 1),
            createDocument(uuid = "1", fileName = "first.txt", position = 2),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(2, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When moving selected tab from 3 to 1 position Then check documents order`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[2] // selected "third.txt"
        val content = Content()

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentMoved(from = 2, to = 0)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "3", fileName = "third.txt", position = 0),
            createDocument(uuid = "1", fileName = "first.txt", position = 1),
            createDocument(uuid = "2", fileName = "second.txt", position = 2),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When moving unselected tab from 1 to 2 position Then check documents order`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[1] // selected "second.txt"
        val content = Content()

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentMoved(from = 0, to = 1)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "2", fileName = "second.txt", position = 0),
            createDocument(uuid = "1", fileName = "first.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When moving unselected tab from 3 to 2 position Then check documents order`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[1] // selected "second.txt"
        val content = Content()

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentMoved(from = 2, to = 1)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "3", fileName = "third.txt", position = 1),
            createDocument(uuid = "2", fileName = "second.txt", position = 2),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(2, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When moving unselected tab from 2 to 3 position Then check documents order`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[0] // selected "first.txt"
        val content = Content()

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentMoved(from = 1, to = 2)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "3", fileName = "third.txt", position = 1),
            createDocument(uuid = "2", fileName = "second.txt", position = 2),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When moving unselected tab from 2 to 1 position Then check documents order`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[2] // selected "third.txt"
        val content = Content()

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentMoved(from = 1, to = 0)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "2", fileName = "second.txt", position = 0),
            createDocument(uuid = "1", fileName = "first.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(2, viewModel.viewState.value.selectedDocument)
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