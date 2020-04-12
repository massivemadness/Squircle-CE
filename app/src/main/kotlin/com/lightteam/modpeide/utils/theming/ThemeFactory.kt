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

import com.lightteam.modpeide.data.feature.scheme.*
import com.lightteam.modpeide.domain.feature.scheme.ColorScheme

object ThemeFactory {

    const val THEME_DARCULA = "THEME_DARCULA"
    const val THEME_MONOKAI = "THEME_MONOKAI"
    const val THEME_OBSIDIAN = "THEME_OBSIDIAN"
    const val THEME_LADIES_NIGHT = "THEME_LADIES_NIGHT"
    const val THEME_TOMORROW_NIGHT = "THEME_TOMORROW_NIGHT"
    const val THEME_VISUAL_STUDIO_2013 = "THEME_VISUAL_STUDIO_2013"

    fun create(theme: String): ColorScheme {
        return when(theme) {
            THEME_DARCULA -> Darcula()
            THEME_MONOKAI -> Monokai()
            THEME_OBSIDIAN -> Obsidian()
            THEME_LADIES_NIGHT -> LadiesNight()
            THEME_TOMORROW_NIGHT -> TomorrowNight()
            THEME_VISUAL_STUDIO_2013 -> VisualStudio2013()
            else -> create(THEME_DARCULA)
        }
    }
}