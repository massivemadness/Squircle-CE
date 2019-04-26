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

package com.lightteam.modpeide.utils.theming

import com.lightteam.modpeide.presentation.main.customview.TextProcessor

object ThemeFactory {

    fun create(theme: String): TextProcessor.Theme {
        return when(theme) {
            Themes.THEME_DARCULA -> {
                val darcula = Themes.getDarcula()
                return TextProcessor.Theme(
                    textColor = darcula.textColor,
                    backgroundColor = darcula.backgroundColor,
                    gutterColor = darcula.gutterColor,
                    gutterTextColor = darcula.gutterTextColor,
                    selectedLineColor = darcula.selectedLineColor,
                    selectionColor = darcula.selectionColor,
                    searchBgColor = darcula.searchBgColor,
                    bracketsBgColor = darcula.bracketBgColor,
                    numbersColor = darcula.numbersColor,
                    symbolsColor = darcula.symbolsColor,
                    bracketsColor = darcula.bracketsColor,
                    keywordsColor = darcula.keywordsColor,
                    methodsColor = darcula.methodsColor,
                    stringsColor = darcula.stringsColor,
                    commentsColor = darcula.commentsColor
                )
            }
            Themes.THEME_MONOKAI -> {
                val monokai = Themes.getMonokai()
                return TextProcessor.Theme(
                    textColor = monokai.textColor,
                    backgroundColor = monokai.backgroundColor,
                    gutterColor = monokai.gutterColor,
                    gutterTextColor = monokai.gutterTextColor,
                    selectedLineColor = monokai.selectedLineColor,
                    selectionColor = monokai.selectionColor,
                    searchBgColor = monokai.searchBgColor,
                    bracketsBgColor = monokai.bracketBgColor,
                    numbersColor = monokai.numbersColor,
                    symbolsColor = monokai.symbolsColor,
                    bracketsColor = monokai.bracketsColor,
                    keywordsColor = monokai.keywordsColor,
                    methodsColor = monokai.methodsColor,
                    stringsColor = monokai.stringsColor,
                    commentsColor = monokai.commentsColor
                )
            }
            else -> create(Themes.THEME_DARCULA)
        }
    }
}