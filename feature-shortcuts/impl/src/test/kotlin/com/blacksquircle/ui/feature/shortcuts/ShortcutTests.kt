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

package com.blacksquircle.ui.feature.shortcuts

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.shortcuts.api.model.KeyGroup
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.domain.ShortcutsRepository
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShortcutTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val shortcutsRepository = mockk<ShortcutsRepository>()

    @Before
    fun setup() {
        coEvery { shortcutsRepository.loadShortcuts() } returns emptyList()
        coEvery { shortcutsRepository.restoreDefaults() } returns Unit

        coEvery { shortcutsRepository.reassign(any()) } returns Unit
        coEvery { shortcutsRepository.disable(any()) } returns Unit
    }

    @Test
    fun `When opening the screen Then display shortcuts`() = runTest {
        // Given
        val shortcuts = listOf(
            Keybinding(Shortcut.CUT, isCtrl = true, key = 'X'),
            Keybinding(Shortcut.COPY, isCtrl = true, key = 'C'),
            Keybinding(Shortcut.PASTE, isCtrl = true, key = 'V'),
        )
        val shortcutMap = mapOf(KeyGroup.EDITOR to shortcuts)
        coEvery { shortcutsRepository.loadShortcuts() } returns shortcuts

        // When
        val viewModel = shortcutsViewModel()

        // Then
        assertEquals(shortcutMap, viewModel.viewState.value.shortcuts)
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

        coEvery { shortcutsRepository.loadShortcuts() } returns shortcuts andThen reassigned

        // When
        val viewModel = shortcutsViewModel()
        viewModel.onSaveClicked(keybinding)

        // Then
        assertEquals(reassignedMap, viewModel.viewState.value.shortcuts)
        coVerify(exactly = 1) { shortcutsRepository.reassign(keybinding) }
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

        coEvery { shortcutsRepository.loadShortcuts() } returns shortcuts andThen reassigned

        // When
        val viewModel = shortcutsViewModel()
        viewModel.onSaveClicked(reassigned[0])

        assertEquals(shortcutMap, viewModel.viewState.value.shortcuts) // check nothing happened
        viewModel.onResolveClicked(reassign = true)

        // Then
        assertEquals(reassignedMap, viewModel.viewState.value.shortcuts) // check reassigned
        coVerify(exactly = 1) { shortcutsRepository.disable(shortcuts[1]) }
        coVerify(exactly = 1) { shortcutsRepository.reassign(reassigned[0]) }
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
        coEvery { shortcutsRepository.loadShortcuts() } returns shortcuts andThen shortcuts

        // When
        val viewModel = shortcutsViewModel()
        viewModel.onSaveClicked(reassigned)

        assertEquals(shortcutMap, viewModel.viewState.value.shortcuts) // check nothing happened
        viewModel.onResolveClicked(reassign = false)

        // Then
        coVerify(exactly = 0) { shortcutsRepository.disable(any()) }
        coVerify(exactly = 0) { shortcutsRepository.reassign(any()) }
        assertEquals(shortcutMap, viewModel.viewState.value.shortcuts) // check ignored
    }

    private fun shortcutsViewModel(): ShortcutsViewModel {
        return ShortcutsViewModel(
            stringProvider = stringProvider,
            shortcutsRepository = shortcutsRepository,
        )
    }
}