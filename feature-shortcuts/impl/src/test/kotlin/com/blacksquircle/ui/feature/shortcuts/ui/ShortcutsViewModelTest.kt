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
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.shortcuts.api.model.KeyGroup
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.api.navigation.EditKeybindingDialog
import com.blacksquircle.ui.feature.shortcuts.domain.ShortcutRepository
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.ShortcutsViewModel
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.ShortcutsViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ShortcutsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val shortcutRepository = mockk<ShortcutRepository>(relaxed = true)

    @Test
    fun `When screen opens Then display shortcuts`() = runTest {
        // Given
        val shortcuts = listOf(
            Keybinding(Shortcut.CUT, isCtrl = true, key = 'X'),
            Keybinding(Shortcut.COPY, isCtrl = true, key = 'C'),
            Keybinding(Shortcut.PASTE, isCtrl = true, key = 'V'),
        )
        val shortcutMap = mapOf(KeyGroup.EDITOR to shortcuts)
        coEvery { shortcutRepository.loadShortcuts() } returns shortcuts

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = ShortcutsViewState(shortcutMap)
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When reassign the keybinding Then update shortcuts`() = runTest {
        // Given
        val shortcuts = listOf(
            Keybinding(Shortcut.CUT, isCtrl = true, key = 'X'),
            Keybinding(Shortcut.COPY, isCtrl = true, key = 'C'),
            Keybinding(Shortcut.PASTE, isCtrl = true, key = 'V'),
        )
        val keybinding = Keybinding(Shortcut.CUT, isCtrl = true, isShift = true, key = 'X')

        val reassigned = shortcuts.map { if (it.shortcut == Shortcut.CUT) keybinding else it }
        val reassignedMap = mapOf(KeyGroup.EDITOR to reassigned)

        coEvery { shortcutRepository.loadShortcuts() } returns shortcuts andThen reassigned

        // When
        val viewModel = createViewModel()
        viewModel.onSaveClicked(keybinding)

        // Then
        val viewState = ShortcutsViewState(reassignedMap)
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 1) { shortcutRepository.reassign(keybinding) }
    }

    @Test
    fun `When keybinding has conflict Then reassign keybinding`() = runTest {
        // Given
        val shortcuts = listOf(
            Keybinding(Shortcut.CUT, isCtrl = true, key = 'X'),
            Keybinding(Shortcut.COPY, isCtrl = true, key = 'C'),
            Keybinding(Shortcut.PASTE, isCtrl = true, key = 'V'),
        )
        val shortcutMap = mapOf(KeyGroup.EDITOR to shortcuts)

        val reassigned = listOf(
            Keybinding(Shortcut.CUT, isCtrl = true, key = 'C'),
            Keybinding(Shortcut.COPY, isCtrl = true, key = '\u0000'),
            Keybinding(Shortcut.PASTE, isCtrl = true, key = 'V'),
        )
        val reassignedMap = mapOf(KeyGroup.EDITOR to reassigned)

        coEvery { shortcutRepository.loadShortcuts() } returns shortcuts andThen reassigned

        // When
        val viewModel = createViewModel()
        viewModel.onSaveClicked(reassigned[0])

        assertEquals(shortcutMap, viewModel.viewState.value.shortcuts) // check nothing happened
        viewModel.onResolveClicked(reassign = true)

        // Then
        assertEquals(reassignedMap, viewModel.viewState.value.shortcuts) // check reassigned
        coVerify(exactly = 1) { shortcutRepository.disable(shortcuts[1]) }
        coVerify(exactly = 1) { shortcutRepository.reassign(reassigned[0]) }
    }

    @Test
    fun `When keybinding has conflict Then ignore keybinding`() = runTest {
        // Given
        val shortcuts = listOf(
            Keybinding(Shortcut.CUT, isCtrl = true, key = 'X'),
            Keybinding(Shortcut.COPY, isCtrl = true, key = 'C'),
            Keybinding(Shortcut.PASTE, isCtrl = true, key = 'V'),
        )
        val shortcutMap = mapOf(KeyGroup.EDITOR to shortcuts)

        val reassigned = Keybinding(Shortcut.CUT, isCtrl = true, key = 'C')
        coEvery { shortcutRepository.loadShortcuts() } returns shortcuts andThen shortcuts

        // When
        val viewModel = createViewModel()
        viewModel.onSaveClicked(reassigned)

        assertEquals(shortcutMap, viewModel.viewState.value.shortcuts) // check nothing happened
        viewModel.onResolveClicked(reassign = false)

        // Then
        coVerify(exactly = 0) { shortcutRepository.disable(any()) }
        coVerify(exactly = 0) { shortcutRepository.reassign(any()) }
        assertEquals(shortcutMap, viewModel.viewState.value.shortcuts) // check ignored
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
    fun `When restore defaults clicked Then restore default keybindings`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onRestoreClicked()

        // Then
        coVerify(exactly = 1) { shortcutRepository.restoreDefaults() }
        coVerify(exactly = 2) { shortcutRepository.loadShortcuts() }
    }

    @Test
    fun `When key clicked Then open keybinding dialog`() = runTest {
        // Given
        val viewModel = createViewModel()
        val keybinding = Keybinding(
            shortcut = Shortcut.CUT,
            isCtrl = true,
            isShift = false,
            isAlt = false,
            key = 'X',
        )

        // When
        viewModel.onKeyClicked(keybinding)

        // Then
        val destination = EditKeybindingDialog(
            shortcut = keybinding.shortcut,
            isCtrl = keybinding.isCtrl,
            isShift = keybinding.isShift,
            isAlt = keybinding.isAlt,
            keyCode = keybinding.key.code,
        )
        val expected = ViewEvent.Navigation(destination)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    private fun createViewModel(): ShortcutsViewModel {
        return ShortcutsViewModel(
            stringProvider = stringProvider,
            shortcutRepository = shortcutRepository,
        )
    }
}