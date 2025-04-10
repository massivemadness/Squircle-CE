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

package com.blacksquircle.ui.feature.shortcuts.repository

import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.data.repository.ShortcutRepositoryImpl
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ShortcutRepositoryImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val settingsManager = mockk<SettingsManager>(relaxed = true)

    private val shortcutRepository = ShortcutRepositoryImpl(
        dispatcherProvider = dispatcherProvider,
        settingsManager = settingsManager
    )

    @Test
    fun `When loading shortcuts Then load from settings`() = runTest {
        // Given
        every { settingsManager.load(any(), any()) } returns Shortcut.CUT.defaultValue

        // When
        val shortcuts = shortcutRepository.loadShortcuts()

        // Then
        assertTrue(shortcuts.size == Shortcut.entries.size)
        verify(exactly = Shortcut.entries.size) { settingsManager.load(any(), any()) }
    }

    @Test
    fun `When restore defaults Then remove shortcut keys`() = runTest {
        // When
        shortcutRepository.restoreDefaults()

        // Then
        verify(exactly = Shortcut.entries.size) { settingsManager.remove(any()) }
    }

    @Test
    fun `When reassign valid keybinding Then update keybinding`() = runTest {
        // Given
        val keybinding = Keybinding(
            shortcut = Shortcut.CUT,
            isCtrl = true,
            isShift = true,
            isAlt = true,
            key = 'X',
        )

        // When
        shortcutRepository.reassign(keybinding)

        // Then
        val encodedValue = "111X"
        verify(exactly = 1) { settingsManager.update(Shortcut.CUT.key, encodedValue) }
    }

    @Test
    fun `When reassign invalid keybinding Then remove keybinding`() = runTest {
        // Given
        val keybinding = Keybinding(
            shortcut = Shortcut.CUT,
            isCtrl = false,
            isShift = true,
            isAlt = false,
            key = 'X',
        )

        // When
        shortcutRepository.reassign(keybinding)

        // Then
        val encodedValue = "010\u0000" // ctrl or alt required
        verify(exactly = 1) { settingsManager.update(Shortcut.CUT.key, encodedValue) }
    }

    @Test
    fun `When disable keybinding Then remove keybinding`() = runTest {
        // Given
        val keybinding = Keybinding(
            shortcut = Shortcut.CUT,
            isCtrl = true,
            isShift = false,
            isAlt = false,
            key = 'X',
        )

        // When
        shortcutRepository.disable(keybinding)

        // Then
        val encodedValue = "000\u0000"
        verify(exactly = 1) { settingsManager.update(Shortcut.CUT.key, encodedValue) }
    }
}