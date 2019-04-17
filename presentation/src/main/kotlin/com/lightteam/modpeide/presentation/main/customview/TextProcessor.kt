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

package com.lightteam.modpeide.presentation.main.customview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView

class TextProcessor(context: Context, attrs: AttributeSet) : AppCompatMultiAutoCompleteTextView(context, attrs) {

    private val TAG = TextProcessor::class.java.simpleName

    data class Configuration(
        var fontSize: Float = 14f,
        var fontType: Typeface = Typeface.MONOSPACE
    )

    data class Theme(
        var textColor: Int = Color.WHITE,
        var backgroundColor: Int = Color.DKGRAY,
        var gutterColor: Int = Color.GRAY,
        var gutterTextColor: Int = Color.WHITE,
        var gutterLineColor: Int = Color.DKGRAY,
        var selectedLineColor: Int = Color.GRAY,
        var searchSpanColor: Int = Color.GREEN,
        var bracketSpanColor: Int = Color.GREEN,

        //Syntax Highlighting
        var numbersColor: Int = Color.WHITE,
        var symbolsColor: Int = Color.WHITE,
        var bracketsColor: Int = Color.WHITE,
        var keywordsColor: Int = Color.WHITE,
        var methodsColor: Int = Color.WHITE,
        var stringsColor: Int = Color.WHITE,
        var commentsColor: Int = Color.WHITE
    )

    // region INIT

    init {
        colorize()
    }

    var configuration: Configuration = Configuration()
        set(value) {
            field = value
            configure()
        }

    var theme: Theme = Theme()
        set(value) {
            field = value
            colorize()
        }

    private fun configure() {
        textSize = configuration.fontSize
        typeface = configuration.fontType
    }

    private fun colorize() {
        post {
            setTextColor(theme.textColor)
            setBackgroundColor(theme.backgroundColor)
        }
    }

    // endregion INIT

    // region METHODS

    fun clearText() = setText("")

    fun insert(delta: CharSequence) {
        var selectionStart = Math.max(0, selectionStart)
        var selectionEnd = Math.max(0, selectionEnd)
        selectionStart = Math.min(selectionStart, selectionEnd)
        selectionEnd = Math.max(selectionStart, selectionEnd)
        try {
            text.delete(selectionStart, selectionEnd)
            text.insert(selectionStart, delta)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    // endregion METHODS
}