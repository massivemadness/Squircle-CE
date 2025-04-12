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

package com.blacksquircle.ui.feature.themes.data.interactor

import android.content.Context
import com.blacksquircle.ui.core.contract.FileType
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.themes.api.interactor.ThemeInteractor
import com.blacksquircle.ui.feature.themes.api.model.ColorScheme
import com.blacksquircle.ui.feature.themes.data.mapper.ThemeMapper
import com.blacksquircle.ui.feature.themes.data.model.AssetsTheme
import com.blacksquircle.ui.feature.themes.data.model.ExternalTheme
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
internal class ThemeInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val jsonParser: Json,
    private val context: Context
) : ThemeInteractor {

    private val themesDir: File
        get() = Directories.themesDir(context)

    override suspend fun loadTheme(themeId: String): ColorScheme {
        return withContext(dispatcherProvider.io()) {
            val themeRegistry = ThemeRegistry.getInstance()

            /** Check if [themeId] is in assets */
            val assetsTheme = AssetsTheme.find(themeId)
            if (assetsTheme != null) {
                val relativePath = assetsTheme.themeUri.substring(ASSET_PATH.length)
                val themeSource = IThemeSource.fromInputStream(
                    /* stream = */ context.assets.open(relativePath),
                    /* fileName = */ relativePath.substringAfterLast(File.separator),
                    /* charset = */ Charsets.UTF_8
                )
                themeRegistry.loadTheme(themeSource, true)

                val externalTheme = jsonParser
                    .decodeFromStream<ExternalTheme>(context.assets.open(relativePath))
                return@withContext ThemeMapper.toColorScheme(externalTheme)
            }

            /** Couldn't find in assets, look in [themesDir] */
            val themeFile = File(themesDir, themeId + FileType.JSON)
            if (themeFile.exists()) {
                val themeSource = IThemeSource.fromFile(themeFile)
                themeRegistry.loadTheme(themeSource, true)

                val externalTheme = jsonParser
                    .decodeFromStream<ExternalTheme>(themeFile.inputStream())
                return@withContext ThemeMapper.toColorScheme(externalTheme)
            }

            throw IllegalStateException("Theme $themeId not found")
        }
    }

    companion object {
        private const val ASSET_PATH = "file:///android_asset/"
    }
}