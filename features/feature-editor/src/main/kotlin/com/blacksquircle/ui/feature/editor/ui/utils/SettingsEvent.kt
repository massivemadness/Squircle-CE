/*
 * Copyright 2022 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.utils

import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel

sealed class SettingsEvent<T>(val value: T) {
    class ThemePref(value: ThemeModel) : SettingsEvent<ThemeModel>(value)
    class FontSize(value: Float) : SettingsEvent<Float>(value)
    class FontType(value: String) : SettingsEvent<String>(value)
    class WordWrap(value: Boolean) : SettingsEvent<Boolean>(value)
    class CodeCompletion(value: Boolean) : SettingsEvent<Boolean>(value)
    class ErrorHighlight(value: Boolean) : SettingsEvent<Boolean>(value)
    class PinchZoom(value: Boolean) : SettingsEvent<Boolean>(value)
    class CurrentLine(value: Boolean) : SettingsEvent<Boolean>(value)
    class Delimiters(value: Boolean) : SettingsEvent<Boolean>(value)
    class ExtendedKeys(value: Boolean) : SettingsEvent<Boolean>(value)
    class KeyboardPreset(value: List<String>) : SettingsEvent<List<String>>(value)
    class SoftKeys(value: Boolean) : SettingsEvent<Boolean>(value)
    class AutoIndentation(value: Triple<Boolean, Boolean, Boolean>) :
        SettingsEvent<Triple<Boolean, Boolean, Boolean>>(value)
    class UseSpacesNotTabs(value: Boolean) : SettingsEvent<Boolean>(value)
    class TabWidth(value: Int) : SettingsEvent<Int>(value)
}