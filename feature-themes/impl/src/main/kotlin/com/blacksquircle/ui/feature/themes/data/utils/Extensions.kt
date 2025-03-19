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

package com.blacksquircle.ui.feature.themes.data.utils

import android.content.Context
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.eclipse.tm4e.core.registry.IThemeSource
import timber.log.Timber
import java.io.File

private const val ASSET_PATH = "file:///android_asset/"

internal fun Context.createThemeFromPath(themePath: String): EditorColorScheme {
    return try {
        val themeName = themePath.substringAfterLast(File.separator)
        val themeStream = if (themePath.startsWith(ASSET_PATH)) {
            assets.open(themePath.substring(ASSET_PATH.length))
        } else {
            File(themePath).inputStream()
        }
        val themeSource = IThemeSource.fromInputStream(themeStream, themeName, Charsets.UTF_8)
        val themeModel = ThemeModel(themeSource, themeName)
        TextMateColorScheme.create(themeModel)
    } catch (e: Exception) {
        Timber.e(e.message, e)
        EditorColorScheme()
    }
}