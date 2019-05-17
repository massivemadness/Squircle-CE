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

import androidx.core.graphics.toColorInt

object Themes {

    const val THEME_DARCULA = "THEME_DARCULA"
    const val THEME_MONOKAI = "THEME_MONOKAI"
    const val THEME_OBSIDIAN = "THEME_OBSIDIAN"
    const val THEME_RETTA = "THEME_RETTA"
    const val THEME_LADIES_NIGHT = "THEME_LADIES_NIGHT"
    const val THEME_TOMORROW_NIGHT = "THEME_TOMORROW_NIGHT"
    const val THEME_VISUAL_STUDIO_2013 = "THEME_VISUAL_STUDIO_2013"

    val darcula: AbstractTheme by lazy { Darcula() }
    val monokai: AbstractTheme by lazy { Monokai() }
    val obsidian: AbstractTheme by lazy { Obsidian() }
    val retta: AbstractTheme by lazy { Retta() }
    val ladiesNight: AbstractTheme by lazy { LadiesNight() }
    val tomorrowNight: AbstractTheme by lazy { TomorrowNight() }
    val visualStudio2013: AbstractTheme by lazy { VisualStudio2013() }

    private class Darcula : AbstractTheme() {
        override val textColor: Int = "#ABB7C5".toColorInt()
        override val backgroundColor: Int = "#303030".toColorInt()
        override val gutterColor: Int = "#313335".toColorInt()
        override val gutterDividerColor: Int = "#555555".toColorInt()
        override val gutterCurrentLineNumberColor: Int = "#A4A3A3".toColorInt()
        override val gutterTextColor: Int = "#616366".toColorInt()
        override val selectedLineColor: Int = "#3A3A3A".toColorInt()
        override val selectionColor: Int = "#28427F".toColorInt()
        override val filterableColor: Int = "#987DAC".toColorInt()

        override val searchBgColor: Int = "#33654B".toColorInt()
        override val bracketBgColor: Int = "#33654B".toColorInt()

        override val numbersColor: Int = "#6897BB".toColorInt()
        override val symbolsColor: Int = "#E8E2B7".toColorInt()
        override val bracketsColor: Int = "#E8E2B7".toColorInt()
        override val keywordsColor: Int = "#EC7600".toColorInt()
        override val methodsColor: Int = "#FEC76C".toColorInt()
        override val stringsColor: Int = "#6E875A".toColorInt()
        override val commentsColor: Int = "#66747B".toColorInt()
    }

    private class Monokai : AbstractTheme() {
        override val textColor: Int = "#F8F8F8".toColorInt()
        override val backgroundColor: Int = "#272823".toColorInt()
        override val gutterColor: Int = "#272823".toColorInt()
        override val gutterDividerColor: Int = "#5B5A4F".toColorInt()
        override val gutterCurrentLineNumberColor: Int = "#C8BBAC".toColorInt()
        override val gutterTextColor: Int = "#5B5A4F".toColorInt()
        override val selectedLineColor: Int = "#34352D".toColorInt()
        override val selectionColor: Int = "#666666".toColorInt()
        override val filterableColor: Int = "#7CE0F3".toColorInt()

        override val searchBgColor: Int = "#5F5E5A".toColorInt()
        override val bracketBgColor: Int = "#5F5E5A".toColorInt()

        override val numbersColor: Int = "#BB8FF8".toColorInt()
        override val symbolsColor: Int = "#F8F8F2".toColorInt()
        override val bracketsColor: Int = "#E8E2B7".toColorInt()
        override val keywordsColor: Int = "#EB347E".toColorInt()
        override val methodsColor: Int = "#B6E951".toColorInt()
        override val stringsColor: Int = "#EBE48C".toColorInt()
        override val commentsColor: Int = "#89826D".toColorInt()
    }

    private class Obsidian : AbstractTheme() {
        override val textColor: Int = "#E0E2E4".toColorInt()
        override val backgroundColor: Int = "#2A3134".toColorInt()
        override val gutterColor: Int = "#2A3134".toColorInt()
        override val gutterDividerColor: Int = "#67777B".toColorInt()
        override val gutterCurrentLineNumberColor: Int = "#E0E0E0".toColorInt()
        override val gutterTextColor: Int = "#859599".toColorInt()
        override val selectedLineColor: Int = "#31393C".toColorInt()
        override val selectionColor: Int = "#616161".toColorInt()
        override val filterableColor: Int = "#9EC56F".toColorInt()

        override val searchBgColor: Int = "#838177".toColorInt()
        override val bracketBgColor: Int = "#616161".toColorInt()

        override val numbersColor: Int = "#F8CE4E".toColorInt()
        override val symbolsColor: Int = "#E7E2BC".toColorInt()
        override val bracketsColor: Int = "#E7E2BC".toColorInt()
        override val keywordsColor: Int = "#9EC56F".toColorInt()
        override val methodsColor: Int = "#E7E2BC".toColorInt()
        override val stringsColor: Int = "#DE7C2E".toColorInt()
        override val commentsColor: Int = "#808C92".toColorInt()
    }

    private class Retta : AbstractTheme() {
        override val textColor: Int = "#A6B0BE".toColorInt()
        override val backgroundColor: Int = "#000000".toColorInt()
        override val gutterColor: Int = "#000000".toColorInt()
        override val gutterDividerColor: Int = "#2A2A2A".toColorInt()
        override val gutterCurrentLineNumberColor: Int = "#FF9650".toColorInt()
        override val gutterTextColor: Int = "#BE7544".toColorInt()
        override val selectedLineColor: Int = "#2A2A2A".toColorInt()
        override val selectionColor: Int = "#5B7C60".toColorInt()
        override val filterableColor: Int = "#5B7C60".toColorInt()

        override val searchBgColor: Int = "#405EAB".toColorInt()
        override val bracketBgColor: Int = "#616161".toColorInt()

        override val numbersColor: Int = "#D3C25D".toColorInt()
        override val symbolsColor: Int = "#F5E1B0".toColorInt()
        override val bracketsColor: Int = "#F5E1B0".toColorInt()
        override val keywordsColor: Int = "#D06C4F".toColorInt()
        override val methodsColor: Int = "#A6B0BE".toColorInt()
        override val stringsColor: Int = "#D3C25D".toColorInt()
        override val commentsColor: Int = "#81786F".toColorInt()
    }

    private class LadiesNight : AbstractTheme() {
        override val textColor: Int = "#E0E2E4".toColorInt()
        override val backgroundColor: Int = "#22282C".toColorInt()
        override val gutterColor: Int = "#2A3134".toColorInt()
        override val gutterDividerColor: Int = "#4F575A".toColorInt()
        override val gutterCurrentLineNumberColor: Int = "#E0E2E4".toColorInt()
        override val gutterTextColor: Int = "#859599".toColorInt()
        override val selectedLineColor: Int = "#373340".toColorInt()
        override val selectionColor: Int = "#5B2B41".toColorInt()
        override val filterableColor: Int = "#6E8BAE".toColorInt()

        override val searchBgColor: Int = "#8A4364".toColorInt()
        override val bracketBgColor: Int = "#616161".toColorInt()

        override val numbersColor: Int = "#7EFBFD".toColorInt()
        override val symbolsColor: Int = "#E7E2BC".toColorInt()
        override val bracketsColor: Int = "#E7E2BC".toColorInt()
        override val keywordsColor: Int = "#DA89A2".toColorInt()
        override val methodsColor: Int = "#8FB4C5".toColorInt()
        override val stringsColor: Int = "#75D367".toColorInt()
        override val commentsColor: Int = "#808C92".toColorInt()
    }

    private class TomorrowNight : AbstractTheme() {
        override val textColor: Int = "#C6C8C6".toColorInt()
        override val backgroundColor: Int = "#222426".toColorInt()
        override val gutterColor: Int = "#222426".toColorInt()
        override val gutterDividerColor: Int = "#4B4D51".toColorInt()
        override val gutterCurrentLineNumberColor: Int = "#FFFFFF".toColorInt()
        override val gutterTextColor: Int = "#C6C8C6".toColorInt()
        override val selectedLineColor: Int = "#2D2F33".toColorInt()
        override val selectionColor: Int = "#383B40".toColorInt()
        override val filterableColor: Int = "#EAC780".toColorInt()

        override val searchBgColor: Int = "#4B4E54".toColorInt()
        override val bracketBgColor: Int = "#616161".toColorInt()

        override val numbersColor: Int = "#D49668".toColorInt()
        override val symbolsColor: Int = "#CFD1CF".toColorInt()
        override val bracketsColor: Int = "#C6C8C6".toColorInt()
        override val keywordsColor: Int = "#AD95B8".toColorInt()
        override val methodsColor: Int = "#87A1BB".toColorInt()
        override val stringsColor: Int = "#B7BC73".toColorInt()
        override val commentsColor: Int = "#969896".toColorInt()
    }

    private class VisualStudio2013 : AbstractTheme() {
        override val textColor: Int = "#C8C8C8".toColorInt()
        override val backgroundColor: Int = "#232323".toColorInt()
        override val gutterColor: Int = "#232323".toColorInt()
        override val gutterDividerColor: Int = "#141414".toColorInt()
        override val gutterCurrentLineNumberColor: Int = "#64D7FF".toColorInt()
        override val gutterTextColor: Int = "#669BD1".toColorInt()
        override val selectedLineColor: Int = "#141414".toColorInt()
        override val selectionColor: Int = "#454464".toColorInt()
        override val filterableColor: Int = "#4F98F7".toColorInt()

        override val searchBgColor: Int = "#1C3D6B".toColorInt()
        override val bracketBgColor: Int = "#616161".toColorInt()

        override val numbersColor: Int = "#BACDAB".toColorInt()
        override val symbolsColor: Int = "#DCDCDC".toColorInt()
        override val bracketsColor: Int = "#FFFFFF".toColorInt()
        override val keywordsColor: Int = "#669BD1".toColorInt()
        override val methodsColor: Int = "#71C6B1".toColorInt()
        override val stringsColor: Int = "#CE9F89".toColorInt()
        override val commentsColor: Int = "#6BA455".toColorInt()
    }
}