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

import android.graphics.Color
import androidx.core.graphics.toColorInt

object Themes {

    const val THEME_DARCULA = "THEME_DARCULA"
    const val THEME_MONOKAI = "THEME_MONOKAI"

    private lateinit var darcula: Darcula
    private lateinit var monokai: Monokai

    fun getDarcula(): AbstractTheme {
        if(!::darcula.isInitialized) {
            darcula = Darcula()
        }
        return darcula
    }

    fun getMonokai(): AbstractTheme {
        if(!::monokai.isInitialized) {
            monokai = Monokai()
        }
        return monokai
    }

    // region DARCULA

    private class Darcula : AbstractTheme() {
        override val textColor: Int = "#ABB7C5".toColorInt()
        override val backgroundColor: Int = "#303030".toColorInt()
        override val gutterColor: Int = "#313335".toColorInt()
        override val gutterTextColor: Int = "#616366".toColorInt()
        override val selectedLineColor: Int = "#3A3A3A".toColorInt()
        override val selectionColor: Int = "#28427f".toColorInt()

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

    // endregion DARCULA

    // region MONOKAI

    private class Monokai : AbstractTheme() {
        override val textColor: Int = Color.GREEN
        override val backgroundColor: Int = Color.DKGRAY
        override val gutterColor: Int = Color.GRAY
        override val gutterTextColor: Int = Color.WHITE
        override val selectedLineColor: Int = Color.GRAY
        override val selectionColor: Int = Color.LTGRAY

        override val searchBgColor: Int = Color.GREEN
        override val bracketBgColor: Int = Color.GREEN

        //Syntax Highlighting
        override val numbersColor: Int = Color.WHITE
        override val symbolsColor: Int = Color.WHITE
        override val bracketsColor: Int = Color.WHITE
        override val keywordsColor: Int = Color.WHITE
        override val methodsColor: Int = Color.WHITE
        override val stringsColor: Int = Color.WHITE
        override val commentsColor: Int = Color.WHITE
    }

    // endregion MONOKAI
}