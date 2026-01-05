/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.themes.ui.themes.store.middleware

import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemeRepository
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesAction
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesState
import com.blacksquircle.ui.redux.extensions.withLatestFrom
import com.blacksquircle.ui.redux.middleware.Middleware
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
internal class ThemesSearchMiddleware @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val settingsManager: SettingsManager,
) : Middleware<ThemesState, ThemesAction> {

    override fun bind(state: Flow<ThemesState>, actions: Flow<ThemesAction>): Flow<ThemesAction> {
        return actions.filterIsInstance<ThemesAction.QueryAction>()
            .withLatestFrom(state)
            .flatMapLatest { (action, state) ->
                when (action) {
                    is ThemesAction.QueryAction.OnQueryChanged -> {
                        val themes = loadThemes(query = action.query)
                        val selectedUuid = settingsManager.editorTheme
                        flowOf(ThemesAction.OnThemesLoaded(themes, selectedUuid, state.typeface))
                    }

                    is ThemesAction.QueryAction.OnClearQueryClicked -> {
                        val themes = loadThemes(query = "")
                        val selectedUuid = settingsManager.editorTheme
                        flowOf(ThemesAction.OnThemesLoaded(themes, selectedUuid, state.typeface))
                    }
                }
            }.catch<ThemesAction> {
                emit(ThemesAction.OnError(it))
            }
    }

    private suspend fun loadThemes(query: String = ""): List<ThemeModel> {
        val themes = themeRepository.loadThemes(query)
        delay(300L) // too fast, avoid blinking
        return themes
    }
}