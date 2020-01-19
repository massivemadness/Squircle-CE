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

import com.lightteam.modpeide.ui.main.customview.TextProcessor

object ThemeFactory {

    fun create(theme: String): TextProcessor.Theme {
        return when(theme) {
            Themes.THEME_DARCULA -> colorize(Themes.darcula)
            Themes.THEME_MONOKAI -> colorize(Themes.monokai)
            Themes.THEME_OBSIDIAN -> colorize(Themes.obsidian)
            Themes.THEME_RETTA -> colorize(Themes.retta)
            Themes.THEME_LADIES_NIGHT -> colorize(Themes.ladiesNight)
            Themes.THEME_TOMORROW_NIGHT -> colorize(Themes.tomorrowNight)
            Themes.THEME_VISUAL_STUDIO_2013 -> colorize(Themes.visualStudio2013)
            else -> create(Themes.THEME_DARCULA)
        }
    }

    private fun colorize(theme: AbstractTheme): TextProcessor.Theme {
        return TextProcessor.Theme(
            textColor = theme.textColor,
            backgroundColor = theme.backgroundColor,
            gutterColor = theme.gutterColor,
            gutterDividerColor = theme.gutterDividerColor,
            gutterCurrentLineNumberColor = theme.gutterCurrentLineNumberColor,
            gutterTextColor = theme.gutterTextColor,
            selectedLineColor = theme.selectedLineColor,
            selectionColor = theme.selectionColor,
            filterableColor = theme.filterableColor,
            searchBgColor = theme.searchBgColor,
            bracketsBgColor = theme.bracketBgColor,
            numbersColor = theme.numbersColor,
            symbolsColor = theme.symbolsColor,
            bracketsColor = theme.bracketsColor,
            keywordsColor = theme.keywordsColor,
            methodsColor = theme.methodsColor,
            stringsColor = theme.stringsColor,
            commentsColor = theme.commentsColor
        )
    }
}