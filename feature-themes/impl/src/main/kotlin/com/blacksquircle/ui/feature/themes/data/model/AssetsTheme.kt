/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.themes.data.model

internal enum class AssetsTheme(
    val themeId: String,
    val themeName: String,
    val themeUri: String,
) {
    THEME_DARCULA(
        themeId = "darcula",
        themeName = "Darcula",
        themeUri = "file:///android_asset/themes/darcula.json",
    ),
    THEME_ECLIPSE(
        themeId = "eclipse",
        themeName = "Eclipse",
        themeUri = "file:///android_asset/themes/eclipse.json",
    ),
    THEME_MONOKAI(
        themeId = "monokai",
        themeName = "Monokai",
        themeUri = "file:///android_asset/themes/monokai.json",
    ),
    THEME_OBSIDIAN(
        themeId = "obsidian",
        themeName = "Obsidian",
        themeUri = "file:///android_asset/themes/obsidian.json",
    ),
    THEME_INTELLIJ_LIGHT(
        themeId = "intellij_light",
        themeName = "IntelliJ Light",
        themeUri = "file:///android_asset/themes/intellij_light.json",
    ),
    THEME_LADIES_NIGHT(
        themeId = "ladies_night",
        themeName = "Ladies Night",
        themeUri = "file:///android_asset/themes/ladies_night.json",
    ),
    THEME_TOMORROW_NIGHT(
        themeId = "tomorrow_night",
        themeName = "Tomorrow Night",
        themeUri = "file:///android_asset/themes/tomorrow_night.json",
    ),
    THEME_SOLARIZED_LIGHT(
        themeId = "solarized_light",
        themeName = "Solarized Light",
        themeUri = "file:///android_asset/themes/solarized_light.json",
    ),
    THEME_VISUAL_STUDIO(
        themeId = "visual_studio",
        themeName = "Visual Studio",
        themeUri = "file:///android_asset/themes/visual_studio.json",
    );

    companion object {

        fun find(id: String): AssetsTheme? {
            return entries.find { it.themeId == id }
        }
    }
}