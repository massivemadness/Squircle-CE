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
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsAction
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsState
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
internal class FontsSearchMiddleware @Inject constructor(
    private val fontsRepository: FontsRepository,
    private val settingsManager: SettingsManager,
) : Middleware<FontsState, FontsAction> {

    override fun bind(state: Flow<FontsState>, actions: Flow<FontsAction>): Flow<FontsAction> {
        return actions.filterIsInstance<FontsAction.QueryAction>()
            .flatMapLatest { action ->
                when (action) {
                    is FontsAction.QueryAction.OnQueryChanged -> {
                        val fonts = loadFonts(query = action.query)
                        val selectedUuid = settingsManager.fontType
                        flowOf(FontsAction.OnFontsLoaded(fonts, selectedUuid))
                    }

                    is FontsAction.QueryAction.OnClearQueryClicked -> {
                        val fonts = loadFonts(query = "")
                        val selectedUuid = settingsManager.fontType
                        flowOf(FontsAction.OnFontsLoaded(fonts, selectedUuid))
                    }
                }
            }.catch<FontsAction> {
                emit(FontsAction.OnError(it))
            }
    }

    private suspend fun loadFonts(query: String = ""): List<FontModel> {
        val fonts = fontsRepository.loadFonts(query)
        delay(300L) // too fast, avoid blinking
        return fonts
    }
}