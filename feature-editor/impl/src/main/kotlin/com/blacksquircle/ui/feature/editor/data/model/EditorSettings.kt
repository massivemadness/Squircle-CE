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

package com.blacksquircle.ui.feature.editor.data.model

import android.graphics.Typeface
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

internal data class EditorSettings(
    val theme: EditorColorScheme = EditorColorScheme(),
    val fontSize: Float = 14f,
    val fontType: Typeface = Typeface.MONOSPACE,
    val wordWrap: Boolean = true,
    val codeCompletion: Boolean = true,
    val pinchZoom: Boolean = true,
    val lineNumbers: Boolean = true,
    val highlightCurrentLine: Boolean = true,
    val highlightMatchingDelimiters: Boolean = true,
    val readOnly: Boolean = false,
    val keyboardPreset: List<KeyModel> = emptyList(),
    val softKeyboard: Boolean = false,
    val autoIndentation: Boolean = true,
    val autoCloseBrackets: Boolean = true,
    val autoCloseQuotes: Boolean = true,
    val useSpacesInsteadOfTabs: Boolean = true,
    val tabWidth: Int = 4,
    val keybindings: List<Keybinding> = emptyList(),
)