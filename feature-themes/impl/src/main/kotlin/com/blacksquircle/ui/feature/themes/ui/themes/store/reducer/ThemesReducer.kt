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

package com.blacksquircle.ui.feature.themes.ui.themes.store.reducer

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesAction
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesEvent
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesState
import com.blacksquircle.ui.redux.reducer.Reducer
import javax.inject.Inject
import com.blacksquircle.ui.ds.R as UiR

internal class ThemesReducer @Inject constructor(
    private val stringProvider: StringProvider,
) : Reducer<ThemesState, ThemesAction, ThemesEvent>() {

    override fun reduce(action: ThemesAction) {
        when (action) {
            is ThemesAction.QueryAction.OnQueryChanged -> {
                state {
                    copy(
                        searchQuery = action.query,
                        isLoading = true,
                    )
                }
            }

            is ThemesAction.QueryAction.OnClearQueryClicked -> {
                state {
                    copy(
                        searchQuery = "",
                        isLoading = true,
                    )
                }
            }

            is ThemesAction.OnThemesLoaded -> {
                state {
                    copy(
                        themes = action.themes,
                        selectedUuid = action.selectedUuid,
                        typeface = action.typeface,
                        isLoading = false,
                    )
                }
            }

            is ThemesAction.OnError -> {
                state {
                    copy(isLoading = false)
                }
                event(
                    ThemesEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred))
                )
            }

            is ThemesAction.OnSelectClicked -> {
                state {
                    copy(selectedUuid = action.theme.uuid)
                }
            }

            is ThemesAction.OnThemeSelected -> {
                event(
                    ThemesEvent.Toast(
                        stringProvider.getString(
                            R.string.themes_toast_theme_selected,
                            action.theme.name,
                        )
                    )
                )
            }

            is ThemesAction.OnThemeRemoved -> {
                state {
                    copy(
                        themes = themes.filterNot {
                            it.uuid == action.theme.uuid
                        },
                        selectedUuid = action.selectedUuid,
                    )
                }
                event(
                    ThemesEvent.Toast(
                        stringProvider.getString(
                            R.string.themes_toast_theme_removed,
                            action.theme.name,
                        )
                    )
                )
            }

            else -> Unit
        }
    }
}