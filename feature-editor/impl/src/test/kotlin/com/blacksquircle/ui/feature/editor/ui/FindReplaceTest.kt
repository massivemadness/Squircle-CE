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

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.provider.typeface.TypefaceProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.createDocument
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewEvent
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorCommand
import com.blacksquircle.ui.feature.editor.ui.editor.model.SearchState
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FindReplaceTest {

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

        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt"),
        )
        val selected = documentList[0]

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList
    }

    @Test
    fun `When toggle find clicked Then show or hide find panel`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onToggleFindClicked()

        // Then
        assertEquals(SearchState(), viewModel.viewState.value.currentDocument?.searchState)

        // When
        viewModel.onToggleFindClicked()

        // Then
        assertEquals(null, viewModel.viewState.value.currentDocument?.searchState)
    }

    @Test
    fun `When toggle replace clicked Then show or hide replace panel`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onToggleFindClicked()

        // Then
        assertEquals(
            SearchState(replaceShown = false),
            viewModel.viewState.value.currentDocument?.searchState
        )

        // When
        viewModel.onToggleReplaceClicked()

        // Then
        assertEquals(
            SearchState(replaceShown = true),
            viewModel.viewState.value.currentDocument?.searchState
        )

        // When
        viewModel.onToggleReplaceClicked()

        // Then
        assertEquals(
            SearchState(replaceShown = false),
            viewModel.viewState.value.currentDocument?.searchState
        )
    }

    @Test
    fun `When find text changed Then update view state and send command`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onToggleFindClicked()
        viewModel.onFindTextChanged("function")

        // Then
        val expected = SearchState(findText = "function", replaceShown = false)
        assertEquals(expected, viewModel.viewState.value.currentDocument?.searchState)

        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val command = EditorViewEvent.Command(EditorCommand.Find(expected))
        assertTrue(command in viewEvents)
    }

    @Test
    fun `When replace text changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onToggleFindClicked()
        viewModel.onFindTextChanged("function")
        viewModel.onToggleReplaceClicked()
        viewModel.onReplaceTextChanged("func")

        // Then
        val expected = SearchState(
            findText = "function",
            replaceText = "func",
            replaceShown = true,
        )
        assertEquals(expected, viewModel.viewState.value.currentDocument?.searchState)
    }

    @Test
    fun `When toggle regex clicked Then toggle regex and send command`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onToggleFindClicked()
        viewModel.onRegexClicked()

        // Then
        val enabled = SearchState(regex = true)
        assertEquals(enabled, viewModel.viewState.value.currentDocument?.searchState)

        // When
        viewModel.onRegexClicked()

        // Then
        val disabled = SearchState(regex = false)
        assertEquals(disabled, viewModel.viewState.value.currentDocument?.searchState)

        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val command1 = EditorViewEvent.Command(EditorCommand.Find(enabled))
        assertTrue(command1 in viewEvents)

        val command2 = EditorViewEvent.Command(EditorCommand.Find(disabled))
        assertTrue(command2 in viewEvents)
    }

    @Test
    fun `When toggle match case clicked Then toggle match case and send command`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onToggleFindClicked()
        viewModel.onMatchCaseClicked()

        // Then
        val enabled = SearchState(matchCase = true)
        assertEquals(enabled, viewModel.viewState.value.currentDocument?.searchState)

        // When
        viewModel.onMatchCaseClicked()

        // Then
        val disabled = SearchState(matchCase = false)
        assertEquals(disabled, viewModel.viewState.value.currentDocument?.searchState)

        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val command1 = EditorViewEvent.Command(EditorCommand.Find(enabled))
        assertTrue(command1 in viewEvents)

        val command2 = EditorViewEvent.Command(EditorCommand.Find(disabled))
        assertTrue(command2 in viewEvents)
    }

    @Test
    fun `When toggle words only clicked Then toggle words only and send command`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onToggleFindClicked()
        viewModel.onWordsOnlyClicked()

        // Then
        val enabled = SearchState(wordsOnly = true)
        assertEquals(enabled, viewModel.viewState.value.currentDocument?.searchState)

        // When
        viewModel.onWordsOnlyClicked()

        // Then
        val disabled = SearchState(wordsOnly = false)
        assertEquals(disabled, viewModel.viewState.value.currentDocument?.searchState)

        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val command1 = EditorViewEvent.Command(EditorCommand.Find(enabled))
        assertTrue(command1 in viewEvents)

        val command2 = EditorViewEvent.Command(EditorCommand.Find(disabled))
        assertTrue(command2 in viewEvents)
    }

    @Test
    fun `When previous match clicked Then send previous match command`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPreviousMatchClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.PreviousMatch)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When next match clicked Then send next match command`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onNextMatchClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.NextMatch)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When replace match clicked Then send replace match command`() = runTest {
        // Given
        val viewModel = createViewModel()
        val replacement = "replace"

        // When
        viewModel.onToggleReplaceClicked()
        viewModel.onReplaceTextChanged(replacement)
        viewModel.onReplaceMatchClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.Replace(replacement))
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When replace all clicked Then send replace all command`() = runTest {
        // Given
        val viewModel = createViewModel()
        val replacement = "replace"

        // When
        viewModel.onToggleReplaceClicked()
        viewModel.onReplaceTextChanged(replacement)
        viewModel.onReplaceAllClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.ReplaceAll(replacement))
        assertEquals(command, viewModel.viewEvent.first())
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