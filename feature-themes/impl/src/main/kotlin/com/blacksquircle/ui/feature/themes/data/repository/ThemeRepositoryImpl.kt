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

package com.blacksquircle.ui.feature.themes.data.repository

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.themes.api.interactor.ThemeInteractor
import com.blacksquircle.ui.feature.themes.data.mapper.ThemeMapper
import com.blacksquircle.ui.feature.themes.data.model.AssetsTheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemeRepository
import kotlinx.coroutines.withContext

internal class ThemeRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val themeInteractor: ThemeInteractor,
) : ThemeRepository {

    override suspend fun loadThemes(query: String): List<ThemeModel> {
        return withContext(dispatcherProvider.io()) {
            AssetsTheme.entries
                .filter { it.name.contains(query, ignoreCase = true) }
                .map(ThemeMapper::toModel)
        }
    }

    override suspend fun removeTheme(themeModel: ThemeModel) {
        withContext(dispatcherProvider.io()) {
            // themeDao.delete(themeModel.uuid)
            if (settingsManager.editorTheme == themeModel.uuid) {
                settingsManager.remove(SettingsManager.KEY_EDITOR_THEME)
            }
        }
    }

    override suspend fun selectTheme(themeModel: ThemeModel) {
        withContext(dispatcherProvider.io()) {
            themeInteractor.loadTheme(themeModel.uuid)
            settingsManager.editorTheme = themeModel.uuid
        }
    }
}