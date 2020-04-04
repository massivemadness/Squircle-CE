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

object ThemeFactory {

    fun create(theme: String): AbstractTheme {
        return when(theme) {
            Themes.THEME_DARCULA -> Themes.Darcula()
            Themes.THEME_MONOKAI -> Themes.Monokai()
            Themes.THEME_OBSIDIAN -> Themes.Obsidian()
            Themes.THEME_LADIES_NIGHT -> Themes.LadiesNight()
            Themes.THEME_TOMORROW_NIGHT -> Themes.TomorrowNight()
            Themes.THEME_VISUAL_STUDIO_2013 -> Themes.VisualStudio2013()
            else -> create(Themes.THEME_DARCULA)
        }
    }
}