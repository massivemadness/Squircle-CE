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

package com.blacksquircle.ui.feature.settings

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.api.navigation.FontsScreen
import com.blacksquircle.ui.feature.settings.ui.editor.EditorHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.editor.EditorHeaderViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class EditorHeaderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val settingsManager = mockk<SettingsManager>(relaxed = true)

    @Test
    fun `When screen opens Then read settings`() = runTest {
        // Given
        every { settingsManager.fontSize } returns 14
        every { settingsManager.wordWrap } returns true
        every { settingsManager.codeCompletion } returns true
        every { settingsManager.pinchZoom } returns true
        every { settingsManager.lineNumbers } returns true
        every { settingsManager.highlightCurrentLine } returns true
        every { settingsManager.highlightMatchingDelimiters } returns true
        every { settingsManager.highlightCodeBlocks } returns true
        every { settingsManager.showInvisibleChars } returns true
        every { settingsManager.readOnly } returns false
        every { settingsManager.autoSaveFiles } returns false
        every { settingsManager.extendedKeyboard } returns false
        every { settingsManager.keyboardPreset } returns "{}()[]"
        every { settingsManager.softKeyboard } returns false

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = EditorHeaderViewState(
            fontSize = 14,
            wordWrap = true,
            codeCompletion = true,
            pinchZoom = true,
            lineNumbers = true,
            highlightCurrentLine = true,
            highlightMatchingDelimiters = true,
            highlightCodeBlocks = true,
            showInvisibleChars = true,
            readOnly = false,
            autoSaveFiles = false,
            extendedKeyboard = false,
            keyboardPreset = "{}()[]",
            softKeyboard = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When back pressed Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onBackClicked()

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
    }

    @Test
    fun `When font size changed Then update view state`() = runTest {
        // Given
        every { settingsManager.fontSize } returns 14 andThen 16

        // When
        val viewModel = createViewModel()
        viewModel.onFontSizeChanged(16)

        // Then
        assertEquals(16, viewModel.viewState.value.fontSize)
        verify(exactly = 1) { settingsManager.fontSize = 16 }
    }

    @Test
    fun `When font type clicked Then open fonts screen`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onFontTypeClicked()

        // Then
        val expected = ViewEvent.Navigation(FontsScreen)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When word wrap changed Then update view state`() = runTest {
        // Given
        every { settingsManager.wordWrap } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onWordWrapChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.wordWrap)
        verify(exactly = 1) { settingsManager.wordWrap = false }
    }

    @Test
    fun `When code completion changed Then update view state`() = runTest {
        // Given
        every { settingsManager.codeCompletion } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onCodeCompletionChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.codeCompletion)
        verify(exactly = 1) { settingsManager.codeCompletion = false }
    }

    @Test
    fun `When pinch zoom changed Then update view state`() = runTest {
        // Given
        every { settingsManager.pinchZoom } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onPinchZoomChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.pinchZoom)
        verify(exactly = 1) { settingsManager.pinchZoom = false }
    }

    @Test
    fun `When line numbers changed Then update view state`() = runTest {
        // Given
        every { settingsManager.lineNumbers } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onLineNumbersChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.lineNumbers)
        verify(exactly = 1) { settingsManager.lineNumbers = false }
    }

    @Test
    fun `When highlight current line changed Then update view state`() = runTest {
        // Given
        every { settingsManager.highlightCurrentLine } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onHighlightCurrentLineChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.highlightCurrentLine)
        verify(exactly = 1) { settingsManager.highlightCurrentLine = false }
    }

    @Test
    fun `When highlight matching delimiters changed Then update view state`() = runTest {
        // Given
        every { settingsManager.highlightMatchingDelimiters } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onHighlightMatchingDelimitersChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.highlightMatchingDelimiters)
        verify(exactly = 1) { settingsManager.highlightMatchingDelimiters = false }
    }

    @Test
    fun `When highlight code blocks changed Then update view state`() = runTest {
        // Given
        every { settingsManager.highlightCodeBlocks } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onHighlightCodeBlocksChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.highlightCodeBlocks)
        verify(exactly = 1) { settingsManager.highlightCodeBlocks = false }
    }

    @Test
    fun `When show invisible chars changed Then update view state`() = runTest {
        // Given
        every { settingsManager.showInvisibleChars } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onShowInvisibleCharsChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.showInvisibleChars)
        verify(exactly = 1) { settingsManager.showInvisibleChars = false }
    }

    @Test
    fun `When read only changed Then update view state`() = runTest {
        // Given
        every { settingsManager.readOnly } returns false andThen true

        // When
        val viewModel = createViewModel()
        viewModel.onReadOnlyChanged(true)

        // Then
        assertEquals(true, viewModel.viewState.value.readOnly)
        verify(exactly = 1) { settingsManager.readOnly = true }
    }

    @Test
    fun `When auto save changed Then update view state`() = runTest {
        // Given
        every { settingsManager.autoSaveFiles } returns false andThen true

        // When
        val viewModel = createViewModel()
        viewModel.onAutoSaveFilesChanged(true)

        // Then
        assertEquals(true, viewModel.viewState.value.autoSaveFiles)
        verify(exactly = 1) { settingsManager.autoSaveFiles = true }
    }

    @Test
    fun `When extended keyboard changed Then update view state`() = runTest {
        // Given
        every { settingsManager.extendedKeyboard } returns false andThen true

        // When
        val viewModel = createViewModel()
        viewModel.onExtendedKeyboardChanged(true)

        // Then
        assertEquals(true, viewModel.viewState.value.extendedKeyboard)
        verify(exactly = 1) { settingsManager.extendedKeyboard = true }
    }

    @Test
    fun `When keyboard preset changed Then update view state`() = runTest {
        // Given
        every { settingsManager.keyboardPreset } returns "{}()[]" andThen "abc"

        // When
        val viewModel = createViewModel()
        viewModel.onKeyboardPresetChanged("abc")

        // Then
        assertEquals("abc", viewModel.viewState.value.keyboardPreset)
        verify(exactly = 1) { settingsManager.keyboardPreset = "abc" }
    }

    @Test
    fun `When reset keyboard clicked Then restore default preset`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onResetKeyboardClicked()

        // Then
        verify(exactly = 1) { settingsManager.remove(SettingsManager.KEY_KEYBOARD_PRESET) }
    }

    @Test
    fun `When soft keyboard changed Then update view state`() = runTest {
        // Given
        every { settingsManager.softKeyboard } returns false andThen true

        // When
        val viewModel = createViewModel()
        viewModel.onSoftKeyboardChanged(true)

        // Then
        assertEquals(true, viewModel.viewState.value.softKeyboard)
        verify(exactly = 1) { settingsManager.softKeyboard = true }
    }

    private fun createViewModel(): EditorHeaderViewModel {
        return EditorHeaderViewModel(settingsManager)
    }
}