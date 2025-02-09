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

package com.blacksquircle.ui.feature.shortcuts.data.mapper

import android.os.Bundle
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut

internal object ShortcutMapper {

    private const val KEY_SHORTCUT = "shortcut"
    private const val KEY_CTRL = "ctrl"
    private const val KEY_SHIFT = "shift"
    private const val KEY_ALT = "alt"
    private const val KEY_CHAR = "char"

    fun toBundle(keybinding: Keybinding): Bundle {
        return Bundle().apply {
            putString(KEY_SHORTCUT, keybinding.shortcut.key)
            putBoolean(KEY_CTRL, keybinding.isCtrl)
            putBoolean(KEY_SHIFT, keybinding.isShift)
            putBoolean(KEY_ALT, keybinding.isAlt)
            putChar(KEY_CHAR, keybinding.key)
        }
    }

    fun fromBundle(bundle: Bundle): Keybinding {
        return Keybinding(
            shortcut = Shortcut.of(bundle.getString(KEY_SHORTCUT).orEmpty()),
            isCtrl = bundle.getBoolean(KEY_CTRL),
            isShift = bundle.getBoolean(KEY_SHIFT),
            isAlt = bundle.getBoolean(KEY_ALT),
            key = bundle.getChar(KEY_CHAR),
        )
    }
}