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

package com.blacksquircle.ui.feature.fonts.ui.fonts.store.middleware

import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsAction
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsState
import com.blacksquircle.ui.navigation.api.Navigator
import com.blacksquircle.ui.redux.middleware.Middleware
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@OptIn(ExperimentalCoroutinesApi::class)
internal class FontsMiddleware @Inject constructor(
    private val fontsRepository: FontsRepository,
    private val settingsManager: SettingsManager,
    private val navigator: Navigator,
) : Middleware<FontsState, FontsAction> {

    override fun bind(state: Flow<FontsState>, actions: Flow<FontsAction>): Flow<FontsAction> {
        return merge(
            onInit(actions),
            onBackClicked(actions),
            onSelectClicked(actions),
            onRemoveClicked(actions),
            onImportFont(actions)
        )
    }

    private fun onInit(actions: Flow<FontsAction>): Flow<FontsAction> {
        return actions.filterIsInstance<FontsAction.OnInit>()
            .map {
                val fonts = fontsRepository.loadFonts(query = "")
                val selectedUuid = settingsManager.fontType
                FontsAction.OnFontsLoaded(fonts, selectedUuid)
            }.catch<FontsAction> {
                emit(FontsAction.OnError(it))
            }
    }

    private fun onBackClicked(actions: Flow<FontsAction>): Flow<FontsAction> {
        return actions.filterIsInstance<FontsAction.OnBackClicked>()
            .flatMapLatest {
                navigator.goBack()
                emptyFlow()
            }
    }

    private fun onSelectClicked(actions: Flow<FontsAction>): Flow<FontsAction> {
        return actions.filterIsInstance<FontsAction.OnSelectClicked>()
            .map { action ->
                fontsRepository.selectFont(action.font)
                FontsAction.OnFontSelected(action.font)
            }.catch<FontsAction> {
                emit(FontsAction.OnError(it))
            }
    }

    private fun onRemoveClicked(actions: Flow<FontsAction>): Flow<FontsAction> {
        return actions.filterIsInstance<FontsAction.OnRemoveClicked>()
            .map { action ->
                fontsRepository.removeFont(action.font)
                FontsAction.OnFontRemoved(action.font, settingsManager.fontType)
            }.catch<FontsAction> {
                emit(FontsAction.OnError(it))
            }
    }

    private fun onImportFont(actions: Flow<FontsAction>): Flow<FontsAction> {
        return actions.filterIsInstance<FontsAction.OnImportFont>()
            .flatMapLatest { action ->
                fontsRepository.importFont(action.uri)

                flowOf(
                    FontsAction.OnFontImported,
                    FontsAction.OnInit
                )
            }.catch {
                emit(FontsAction.OnError(it))
            }
    }
}