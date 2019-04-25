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

package com.lightteam.modpeide.utils.commons

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.lightteam.modpeide.presentation.main.customview.TextProcessor

object ThemeFactory {

    private const val THEME_DARCULA = "THEME_DARCULA"
    private const val THEME_MONOKAI = "THEME_MONOKAI"

    fun create(theme: String): TextProcessor.Theme {
        return when(theme) {
            THEME_DARCULA -> {
                val darcula = Darcula()
                TextProcessor.Theme(
                    textColor = darcula.textColor,
                    backgroundColor = darcula.backgroundColor,
                    gutterColor = darcula.gutterColor,
                    gutterTextColor = darcula.gutterTextColor,
                    selectedLineColor = darcula.selectedLineColor,
                    selectionColor = darcula.selectionColor,
                    searchSpanColor = darcula.searchSpanColor,
                    bracketSpanColor = darcula.bracketSpanColor,
                    numbersColor = darcula.numbersColor,
                    symbolsColor = darcula.symbolsColor,
                    bracketsColor = darcula.bracketsColor,
                    keywordsColor = darcula.keywordsColor,
                    methodsColor = darcula.methodsColor,
                    stringsColor = darcula.stringsColor,
                    commentsColor = darcula.commentsColor
                )
            }
            THEME_MONOKAI -> {
                val monokai = Monokai()
                TextProcessor.Theme(
                    textColor = monokai.textColor,
                    backgroundColor = monokai.backgroundColor,
                    gutterColor = monokai.gutterColor,
                    gutterTextColor = monokai.gutterTextColor,
                    selectedLineColor = monokai.selectedLineColor,
                    selectionColor = monokai.selectionColor,
                    searchSpanColor = monokai.searchSpanColor,
                    bracketSpanColor = monokai.bracketSpanColor,
                    numbersColor = monokai.numbersColor,
                    symbolsColor = monokai.symbolsColor,
                    bracketsColor = monokai.bracketsColor,
                    keywordsColor = monokai.keywordsColor,
                    methodsColor = monokai.methodsColor,
                    stringsColor = monokai.stringsColor,
                    commentsColor = monokai.commentsColor
                )
            }
            else -> create(THEME_DARCULA)
        }
    }

    private class Darcula {
        val textColor: Int = "#ABB7C5".toColorInt()
        val backgroundColor: Int = "#303030".toColorInt()
        val gutterColor: Int = "#313335".toColorInt()
        val gutterTextColor: Int = "#616366".toColorInt()
        val selectedLineColor: Int = "#3A3A3A".toColorInt()
        val selectionColor: Int = "#28427f".toColorInt()

        val searchSpanColor: Int = "#32593D".toColorInt()
        val bracketSpanColor: Int = "#3F504D".toColorInt()

        //Syntax Highlighting
        val numbersColor: Int = "#6897BB".toColorInt()
        val symbolsColor: Int = "#E8E2B7".toColorInt()
        val bracketsColor: Int = "#E8E2B7".toColorInt()
        val keywordsColor: Int = "#EC7600".toColorInt()
        val methodsColor: Int = "#FEC76C".toColorInt()
        val stringsColor: Int = "#6E875A".toColorInt()
        val commentsColor: Int = "#66747B".toColorInt()
    }

    private class Monokai {
        val textColor: Int = Color.GREEN
        val backgroundColor: Int = Color.DKGRAY
        val gutterColor: Int = Color.GRAY
        val gutterTextColor: Int = Color.WHITE
        val selectedLineColor: Int = Color.GRAY
        val selectionColor: Int = Color.LTGRAY
        val searchSpanColor: Int = Color.GREEN
        val bracketSpanColor: Int = Color.GREEN

        //Syntax Highlighting
        val numbersColor: Int = Color.WHITE
        val symbolsColor: Int = Color.WHITE
        val bracketsColor: Int = Color.WHITE
        val keywordsColor: Int = Color.WHITE
        val methodsColor: Int = Color.WHITE
        val stringsColor: Int = Color.WHITE
        val commentsColor: Int = Color.WHITE
    }
}