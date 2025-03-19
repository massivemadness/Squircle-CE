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
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.themes.api.interactor.ThemesInteractor
import com.blacksquircle.ui.feature.themes.data.model.InternalTheme
import com.blacksquircle.ui.feature.themes.data.utils.createThemeFromPath
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import kotlinx.coroutines.withContext
import java.io.File

internal class ThemesInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val context: Context
) : ThemesInteractor {

    private val themesDir: File
        get() = Directories.themesDir(context)

    init {
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(context.assets)
        )
    }

    override suspend fun loadTheme(): EditorColorScheme {
        return withContext(dispatcherProvider.io()) {
            val themeUuid = settingsManager.editorTheme

            val internalTheme = InternalTheme.find(themeUuid)
            if (internalTheme != null) {
                return@withContext context.createThemeFromPath(internalTheme.themeUri)
            }

            val externalTheme = File(themesDir, themeUuid + FileType.JSON)
            if (externalTheme.exists()) {
                return@withContext context.createThemeFromPath(externalTheme.absolutePath)
            }

            throw IllegalStateException("Theme with id $themeUuid not found")
        }
    }
}