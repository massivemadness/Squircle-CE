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
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewEvent
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorCommand
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CommandsTest {

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
    fun `When cut clicked Then send cut command`() = runTest {
        // Given
        every { settingsManager.readOnly } returns false

        // When
        val viewModel = createViewModel()
        viewModel.onCutClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.Cut)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When cut clicked with read only mode Then do nothing`() = runTest {
        // Given
        every { settingsManager.readOnly } returns true

        // When
        val viewModel = createViewModel()
        viewModel.onCutClicked()

        // Then
        val events = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(events)
        }
        assertEquals(events, emptyList<ViewEvent>())
    }

    @Test
    fun `When copy clicked Then send copy command`() = runTest {
        // When
        val viewModel = createViewModel()
        viewModel.onCopyClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.Copy)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When paste clicked Then send paste command`() = runTest {
        // Given
        every { settingsManager.readOnly } returns false

        // When
        val viewModel = createViewModel()
        viewModel.onPasteClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.Paste)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When paste clicked with read only mode Then do nothing`() = runTest {
        // Given
        every { settingsManager.readOnly } returns true

        // When
        val viewModel = createViewModel()
        viewModel.onPasteClicked()

        // Then
        val events = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(events)
        }
        assertEquals(events, emptyList<ViewEvent>())
    }

    @Test
    fun `When select all clicked Then send select all command`() = runTest {
        // When
        val viewModel = createViewModel()
        viewModel.onSelectAllClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.SelectAll)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When select line clicked Then send select line command`() = runTest {
        // When
        val viewModel = createViewModel()
        viewModel.onSelectLineClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.SelectLine)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When delete line clicked Then send delete line command`() = runTest {
        // Given
        every { settingsManager.readOnly } returns false

        // When
        val viewModel = createViewModel()
        viewModel.onDeleteLineClicked()

        // Then
        val command = EditorViewEvent.Command(EditorCommand.DeleteLine)
        assertEquals(command, viewModel.viewEvent.first())
    }

    @Test
    fun `When delete line clicked with read only mode Then do nothing`() = runTest {
        // Given
        every { settingsManager.readOnly } returns true

        // When
        val viewModel = createViewModel()
        viewModel.onDeleteLineClicked()

        // Then
        val events = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(events)
        }
        assertEquals(events, emptyList<ViewEvent>())
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