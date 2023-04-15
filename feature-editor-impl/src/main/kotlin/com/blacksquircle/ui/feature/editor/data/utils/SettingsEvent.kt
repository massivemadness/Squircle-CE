/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.data.utils

import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.settings.domain.model.KeyModel
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel

sealed class SettingsEvent<T>(val value: T) {
    class ColorScheme(value: ThemeModel) : SettingsEvent<ThemeModel>(value)
    class FontSize(value: Float) : SettingsEvent<Float>(value)
    class FontType(value: FontModel) : SettingsEvent<FontModel>(value)

    class WordWrap(value: Boolean) : SettingsEvent<Boolean>(value)
    class CodeCompletion(value: Boolean) : SettingsEvent<Boolean>(value)

    // class ErrorHighlight(value: Boolean) : SettingsEvent<Boolean>(value)
    class PinchZoom(value: Boolean) : SettingsEvent<Boolean>(value)
    class LineNumbers(value: Pair<Boolean, Boolean>) :
        SettingsEvent<Pair<Boolean, Boolean>>(value)
    class Delimiters(value: Boolean) : SettingsEvent<Boolean>(value)
    class ReadOnly(value: Boolean) : SettingsEvent<Boolean>(value)
    class KeyboardPreset(value: List<KeyModel>) : SettingsEvent<List<KeyModel>>(value)
    class SoftKeys(value: Boolean) : SettingsEvent<Boolean>(value)

    class AutoIndentation(value: Triple<Boolean, Boolean, Boolean>) :
        SettingsEvent<Triple<Boolean, Boolean, Boolean>>(value)
    class UseSpacesNotTabs(value: Boolean) : SettingsEvent<Boolean>(value)
    class TabWidth(value: Int) : SettingsEvent<Int>(value)

    class Keybindings(value: List<Keybinding>) : SettingsEvent<List<Keybinding>>(value)
}