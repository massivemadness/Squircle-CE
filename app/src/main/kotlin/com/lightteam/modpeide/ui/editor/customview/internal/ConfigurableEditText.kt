/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.ui.editor.customview.internal

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import com.lightteam.modpeide.R

open class ConfigurableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : AppCompatMultiAutoCompleteTextView(context, attrs, defStyleAttr) {

    var config: Config = Config()
        set(value) {
            field = value
            configure()
        }

    open fun configure() {
        imeOptions = if (config.softKeyboard) {
            EditorInfo.IME_ACTION_UNSPECIFIED
        } else {
            EditorInfo.IME_FLAG_NO_EXTRACT_UI
        }
        inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        textSize = config.fontSize
        typeface = config.fontType
    }

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
        var autoCloseQuotes: Boolean = false
    )
}