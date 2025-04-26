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
import com.blacksquircle.ui.feature.editor.api.model.EditorApiEvent
import com.blacksquircle.ui.feature.editor.createDocument
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.editor.model.ErrorAction
import com.blacksquircle.ui.feature.editor.ui.editor.model.ErrorState
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.github.rosemoe.sora.text.Content
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.UUID

class SelectFileTest {

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

    private val eventBus = MutableSharedFlow<EditorApiEvent>()

    @Before
    fun setup() {
        mockkObject(TypefaceProvider)
        every { TypefaceProvider.DEFAULT } returns mockk()

        every { editorInteractor.eventBus } returns eventBus
    }

    @Test
    fun `When selecting tab without autosave Then load document content`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[0]
        val selectedContent = Content(selected.name)
        val expected = documentList[1]
        val expectedContent = Content(expected.name)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.autoSaveFiles } returns false

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(selected) } returns selectedContent
        coEvery { documentRepository.loadDocument(expected) } returns expectedContent

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentClicked(expected)

        // Then
        val documents = listOf(
            DocumentState(document = documentList[0], content = null), // free memory
            DocumentState(document = documentList[1], content = expectedContent),
            DocumentState(document = documentList[2], content = null),
        )
        assertEquals(documents, viewModel.viewState.value.documents)
        assertEquals(1, viewModel.viewState.value.selectedDocument)

        coVerify(exactly = 0) { documentRepository.saveDocument(selected, selectedContent) }
        coVerify(exactly = 1) { documentRepository.cacheDocument(selected, selectedContent) }
        coVerify(exactly = 1) { documentRepository.loadDocument(expected) }
    }

    @Test
    fun `When selecting tab with autosave Then save file and then load document`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[0]
        val selectedContent = Content(selected.name)
        val expected = documentList[1]
        val expectedContent = Content(expected.name)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.autoSaveFiles } returns true

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(selected) } returns selectedContent
        coEvery { documentRepository.loadDocument(expected) } returns expectedContent

        // When
        val viewModel = createViewModel()
        viewModel.onDocumentClicked(expected)

        // Then
        val documents = listOf(
            DocumentState(document = documentList[0], content = null), // free memory
            DocumentState(document = documentList[1], content = expectedContent),
            DocumentState(document = documentList[2], content = null),
        )
        assertEquals(documents, viewModel.viewState.value.documents)
        assertEquals(1, viewModel.viewState.value.selectedDocument)

        coVerify(exactly = 1) { documentRepository.saveDocument(selected, selectedContent) }
        coVerify(exactly = 0) { documentRepository.cacheDocument(selected, selectedContent) }
        coVerify(exactly = 1) { documentRepository.loadDocument(expected) }
    }

    @Test
    fun `When user opens a file which is already in tabs Then select existing tab`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[0]
        val expected = documentList[1]

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        val fileModel = FileModel(
            fileUri = expected.fileUri,
            filesystemUuid = expected.filesystemUuid
        )
        eventBus.emit(EditorApiEvent.OpenFile(fileModel))

        // Then
        assertEquals(1, viewModel.viewState.value.selectedDocument)
        coVerify(exactly = 1) { documentRepository.loadDocument(expected) }
    }

    @Test
    fun `When user opens a file which isn't in tabs Then open a new tab`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
        )
        val selected = documentList[0]
        val expected = createDocument(uuid = "3", fileName = "third.txt", position = 2)
        val content = Content()

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        val fileModel = FileModel(
            fileUri = expected.fileUri,
            filesystemUuid = expected.filesystemUuid
        )
        mockkStatic(UUID::class) {
            every { UUID.randomUUID().toString() } returns expected.uuid
            eventBus.emit(EditorApiEvent.OpenFile(fileModel))
        }

        // Then
        val documents = listOf(
            DocumentState(document = documentList[0], content = null),
            DocumentState(document = documentList[1], content = null),
            DocumentState(document = expected, content = content),
        )
        assertEquals(documents, viewModel.viewState.value.documents)
        assertEquals(2, viewModel.viewState.value.selectedDocument)

        coVerify(exactly = 1) { documentRepository.loadDocument(expected) }
    }

    @Test
    fun `When file loading fails Then display error state`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val exception = IOException("Unable to load file")

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(selected) } throws exception

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val documents = listOf(
            DocumentState(
                document = documentList[0],
                content = null,
                errorState = ErrorState(
                    subtitle = exception.message.orEmpty(),
                    action = ErrorAction.CLOSE_DOCUMENT,
                )
            ),
        )
        assertEquals(documents, viewModel.viewState.value.documents)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
        assertEquals(false, viewModel.viewState.value.isLoading)
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