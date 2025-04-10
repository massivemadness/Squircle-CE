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

package com.blacksquircle.ui.feature.shortcuts.ui

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.KeybindingViewEvent
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.KeybindingViewModel
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.KeybindingViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class KeybindingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val keybinding = Keybinding(
        shortcut = Shortcut.CUT,
        isCtrl = true,
        isShift = false,
        isAlt = false,
        key = 'X'
    )

    @Test
    fun `When screen opens Then init view state`() = runTest {
        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = keybinding.isCtrl,
            isShift = keybinding.isShift,
            isAlt = keybinding.isAlt,
            key = keybinding.key,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When lowercase key pressed Then turn to uppercase`() = runTest {
        // Given
        val lowercaseKey = 'q'
        val uppercaseKey = 'Q'
        val viewModel = createViewModel()

        // When
        viewModel.onKeyPressed(lowercaseKey)

        // Then
        val viewState = KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = keybinding.isCtrl,
            isShift = keybinding.isShift,
            isAlt = keybinding.isAlt,
            key = uppercaseKey,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When uppercase key pressed Then toggle shift`() = runTest {
        // Given
        val uppercaseKey = 'Q'
        val viewModel = createViewModel()

        // When
        viewModel.onKeyPressed(uppercaseKey)

        // Then
        val viewState = KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = keybinding.isCtrl,
            isShift = true,
            isAlt = keybinding.isAlt,
            key = uppercaseKey,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When multiple keys pressed Then update keybinding`() = runTest {
        // Given
        val ctrl = true
        val shift = true
        val alt = true
        val key = 'J'
        val viewModel = createViewModel()

        // When
        viewModel.onMultiKeyPressed(ctrl, shift, alt, key)

        // Then
        val viewState = KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = ctrl,
            isShift = shift,
            isAlt = alt,
            key = key,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When ctrl clicked Then toggle ctrl`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onCtrlClicked()

        // Then
        val viewState = KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = !keybinding.isCtrl,
            isShift = keybinding.isShift,
            isAlt = keybinding.isAlt,
            key = keybinding.key,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When shift clicked Then toggle shift`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onShiftClicked()

        // Then
        val viewState = KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = keybinding.isCtrl,
            isShift = !keybinding.isShift,
            isAlt = keybinding.isAlt,
            key = keybinding.key,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When alt clicked Then toggle alt`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onAltClicked()

        // Then
        val viewState = KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = keybinding.isCtrl,
            isShift = keybinding.isShift,
            isAlt = !keybinding.isAlt,
            key = keybinding.key,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When save clicked Then send result to previous screen`() = runTest {
        // Given
        val ctrl = true
        val shift = true
        val alt = true
        val key = 'J'
        val viewModel = createViewModel()

        // When
        viewModel.onMultiKeyPressed(ctrl, shift, alt, key)
        viewModel.onSaveClicked()

        // Then
        val keybinding = Keybinding(
            shortcut = keybinding.shortcut,
            isCtrl = ctrl,
            isShift = shift,
            isAlt = alt,
            key = key,
        )
        assertEquals(KeybindingViewEvent.SendSaveResult(keybinding), viewModel.viewEvent.first())
    }

    @Test
    fun `When cancel clicked Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onCancelClicked()

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
    }

    private fun createViewModel(): KeybindingViewModel {
        return KeybindingViewModel(keybinding)
    }
}