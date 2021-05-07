/*
 * Copyright 2021 Squircle IDE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.data.utils

import com.blacksquircle.ui.domain.model.themes.ThemeModel
import com.blacksquircle.ui.editorkit.theme.EditorTheme

object InternalTheme {

    private const val DARCULA = "DARCULA"
    private const val MONOKAI = "MONOKAI"
    private const val OBSIDIAN = "OBSIDIAN"
    private const val LADIES_NIGHT = "LADIES_NIGHT"
    private const val TOMORROW_NIGHT = "TOMORROW_NIGHT"
    private const val VISUAL_STUDIO_2013 = "VISUAL_STUDIO_2013"

    private val THEME_DARCULA = ThemeModel(
        uuid = DARCULA,
        name = "Darcula",
        author = "Squircle IDE",
        description = "Default color scheme",
        isExternal = false,
        colorScheme = EditorTheme.DARCULA
    )
    private val THEME_MONOKAI = ThemeModel(
        uuid = MONOKAI,
        name = "Monokai",
        author = "Squircle IDE",
        description = "Default color scheme",
        isExternal = false,
        colorScheme = EditorTheme.MONOKAI
    )
    private val THEME_OBSIDIAN = ThemeModel(
        uuid = OBSIDIAN,
        name = "Obsidian",
        author = "Squircle IDE",
        description = "Default color scheme",
        isExternal = false,
        colorScheme = EditorTheme.OBSIDIAN
    )
    private val THEME_LADIES_NIGHT = ThemeModel(
        uuid = LADIES_NIGHT,
        name = "Ladies Night",
        author = "Squircle IDE",
        description = "Default color scheme",
        isExternal = false,
        colorScheme = EditorTheme.LADIES_NIGHT
    )
    private val THEME_TOMORROW_NIGHT = ThemeModel(
        uuid = TOMORROW_NIGHT,
        name = "Tomorrow Night",
        author = "Squircle IDE",
        description = "Default color scheme",
        isExternal = false,
        colorScheme = EditorTheme.TOMORROW_NIGHT
    )
    private val THEME_VISUAL_STUDIO_2013 = ThemeModel(
        uuid = VISUAL_STUDIO_2013,
        name = "Visual Studio 2013",
        author = "Squircle IDE",
        description = "Default color scheme",
        isExternal = false,
        colorScheme = EditorTheme.VISUAL_STUDIO_2013
    )

    fun getTheme(themeId: String): ThemeModel? {
        return when (themeId) {
            DARCULA -> THEME_DARCULA
            MONOKAI -> THEME_MONOKAI
            OBSIDIAN -> THEME_OBSIDIAN
            LADIES_NIGHT -> THEME_LADIES_NIGHT
            TOMORROW_NIGHT -> THEME_TOMORROW_NIGHT
            VISUAL_STUDIO_2013 -> THEME_VISUAL_STUDIO_2013
            else -> null
        }
    }

    /**
     * Default color schemes from :editorkit module.
     */
    fun getThemes(): List<ThemeModel> {
        return listOf(
            THEME_DARCULA,
            THEME_MONOKAI,
            THEME_OBSIDIAN,
            THEME_LADIES_NIGHT,
            THEME_TOMORROW_NIGHT,
            THEME_VISUAL_STUDIO_2013
        )
    }
}