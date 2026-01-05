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
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.themes.domain.repository.ThemeRepository
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesAction
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesState
import com.blacksquircle.ui.navigation.api.Navigator
import com.blacksquircle.ui.redux.middleware.Middleware
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@OptIn(ExperimentalCoroutinesApi::class)
internal class ThemesMiddleware @Inject constructor(
    private val fontsInteractor: FontsInteractor,
    private val themeRepository: ThemeRepository,
    private val settingsManager: SettingsManager,
    private val navigator: Navigator,
) : Middleware<ThemesState, ThemesAction> {

    override fun bind(state: Flow<ThemesState>, actions: Flow<ThemesAction>): Flow<ThemesAction> {
        return merge(
            onInit(actions),
            onBackClicked(actions),
            onSelectClicked(actions),
            onRemoveClicked(actions),
        )
    }

    private fun onInit(actions: Flow<ThemesAction>): Flow<ThemesAction> {
        return actions.filterIsInstance<ThemesAction.OnInit>()
            .map {
                val themes = themeRepository.loadThemes(query = "")
                val selectedUuid = settingsManager.editorTheme
                val typeface = fontsInteractor.loadFont(settingsManager.fontType)
                ThemesAction.OnThemesLoaded(themes, selectedUuid, typeface)
            }.catch<ThemesAction> {
                emit(ThemesAction.OnError(it))
            }
    }

    private fun onBackClicked(actions: Flow<ThemesAction>): Flow<ThemesAction> {
        return actions.filterIsInstance<ThemesAction.OnBackClicked>()
            .flatMapLatest {
                navigator.goBack()
                emptyFlow()
            }
    }

    private fun onSelectClicked(actions: Flow<ThemesAction>): Flow<ThemesAction> {
        return actions.filterIsInstance<ThemesAction.OnSelectClicked>()
            .map { action ->
                themeRepository.selectTheme(action.theme)
                ThemesAction.OnThemeSelected(action.theme)
            }.catch<ThemesAction> {
                emit(ThemesAction.OnError(it))
            }
    }

    private fun onRemoveClicked(actions: Flow<ThemesAction>): Flow<ThemesAction> {
        return actions.filterIsInstance<ThemesAction.OnRemoveClicked>()
            .map { action ->
                themeRepository.removeTheme(action.theme)
                ThemesAction.OnThemeRemoved(action.theme, settingsManager.editorTheme)
            }.catch<ThemesAction> {
                emit(ThemesAction.OnError(it))
            }
    }
}