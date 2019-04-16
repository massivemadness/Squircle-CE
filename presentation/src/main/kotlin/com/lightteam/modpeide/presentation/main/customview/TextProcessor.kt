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
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView

class TextProcessor(context: Context, attrs: AttributeSet) : AppCompatMultiAutoCompleteTextView(context, attrs) {

    data class Configuration(
        var fontSize: Float = 14f,
        var fontType: Typeface = Typeface.MONOSPACE
    )

    data class Theme(
        var textColor: Int = -1,
        var backgroundColor: Int = -1,
        var gutterColor: Int = -1,
        var gutterTextColor: Int = -1,
        var gutterLineColor: Int = -1,
        var selectedLineColor: Int = -1,
        var searchSpanColor: Int = -1,
        var bracketSpanColor: Int = -1,

        //Syntax Highlighting
        var numbersColor: Int = -1,
        var symbolsColor: Int = -1,
        var bracketsColor: Int = -1,
        var keywordsColor: Int = -1,
        var methodsColor: Int = -1,
        var stringsColor: Int = -1,
        var commentsColor: Int = -1
    )

    private var configuration: Configuration = Configuration()
    private var theme: Theme = Theme()

    init {
        configure(configuration)
        colorize(theme)
    }

    fun getConfiguration() = configuration
    fun setConfiguration(configuration: Configuration) = configure(configuration)
    fun getTheme() = theme
    fun setTheme(theme: Theme) = colorize(theme)

    private fun configure(configuration: Configuration) {
        this.configuration = configuration

        textSize = configuration.fontSize
        typeface = configuration.fontType
    }

    private fun colorize(theme: Theme) {
        this.theme = theme
        //invalidate()
    }

    fun clearText() = setText("")
}