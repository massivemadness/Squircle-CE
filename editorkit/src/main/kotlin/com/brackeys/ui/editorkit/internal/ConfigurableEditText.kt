/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.editorkit.internal

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import com.brackeys.ui.editorkit.R

abstract class ConfigurableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : AppCompatMultiAutoCompleteTextView(context, attrs, defStyleAttr) {

    var config: Config = Config()
        set(value) {
            field = value
            configure()
        }

    abstract fun configure()

    data class Config(

        // Font
        var fontSize: Float = 14f,
        var fontType: Typeface = Typeface.MONOSPACE,

        // Editor
        var wordWrap: Boolean = true,
        var codeCompletion: Boolean = true,
        var pinchZoom: Boolean = true,
        var highlightCurrentLine: Boolean = true,
        var highlightDelimiters: Boolean = true,

        // Keyboard
        var softKeyboard: Boolean = false,

        // Code Style
        var autoIndentation: Boolean = true,
        var autoCloseBrackets: Boolean = true,
        var autoCloseQuotes: Boolean = false,
        var useSpacesInsteadOfTabs: Boolean = true,
        var tabWidth: Int = 4
    )
}