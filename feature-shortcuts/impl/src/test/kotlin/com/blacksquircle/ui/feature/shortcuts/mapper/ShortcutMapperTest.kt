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

package com.blacksquircle.ui.feature.shortcuts.mapper

import android.os.Bundle
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.data.mapper.ShortcutMapper
import junit.framework.TestCase.assertEquals
import org.junit.Ignore
import org.junit.Test

class ShortcutMapperTest {

    @Test
    @Ignore("TODO: Method putString in android.os.BaseBundle not mocked")
    fun `When toBundle is called Then all fields are correctly stored`() {
        // Given
        val keybinding = Keybinding(
            shortcut = Shortcut.CUT,
            isCtrl = true,
            isShift = false,
            isAlt = true,
            key = 'X'
        )

        // When
        val bundle = ShortcutMapper.toBundle(keybinding)

        // Then
        assertEquals(Shortcut.CUT.key, bundle.getString("shortcut"))
        assertEquals(true, bundle.getBoolean("ctrl"))
        assertEquals(false, bundle.getBoolean("shift"))
        assertEquals(true, bundle.getBoolean("alt"))
        assertEquals('X', bundle.getChar("char"))
    }

    @Test
    @Ignore("TODO: Method putString in android.os.BaseBundle not mocked")
    fun `When fromBundle is called Then Keybinding is correctly reconstructed`() {
        // Given
        val bundle = Bundle().apply {
            putString("shortcut", Shortcut.CUT.key)
            putBoolean("ctrl", true)
            putBoolean("shift", true)
            putBoolean("alt", false)
            putChar("char", 'X')
        }

        // When
        val result = ShortcutMapper.fromBundle(bundle)

        // Then
        assertEquals(Shortcut.CUT.key, result.shortcut.key)
        assertEquals(true, result.isCtrl)
        assertEquals(true, result.isShift)
        assertEquals(false, result.isAlt)
        assertEquals('X', result.key)
    }
}