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

package com.blacksquircle.ui.feature.fonts.ui.fonts.store.reducer

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsAction
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsEvent
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsState
import com.blacksquircle.ui.redux.reducer.Reducer
import javax.inject.Inject
import com.blacksquircle.ui.ds.R as UiR

internal class FontsReducer @Inject constructor(
    private val stringProvider: StringProvider,
) : Reducer<FontsState, FontsAction, FontsEvent>() {

    override fun reduce(action: FontsAction) {
        when (action) {
            is FontsAction.QueryAction.OnQueryChanged -> {
                state {
                    copy(
                        searchQuery = action.query,
                        isLoading = true,
                    )
                }
            }

            is FontsAction.QueryAction.OnClearQueryClicked -> {
                state {
                    copy(
                        searchQuery = "",
                        isLoading = true,
                    )
                }
            }

            is FontsAction.OnFontsLoaded -> {
                state {
                    copy(
                        fonts = action.fonts,
                        selectedUuid = action.selectedUuid,
                        isLoading = false,
                    )
                }
            }

            is FontsAction.OnFontsFailed -> {
                state {
                    copy(isLoading = false)
                }
                event(
                    FontsEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred))
                )
            }

            is FontsAction.OnSelectClicked -> {
                state {
                    copy(selectedUuid = action.font.uuid)
                }
            }

            is FontsAction.OnFontSelected -> {
                event(
                    FontsEvent.Toast(
                        stringProvider.getString(
                            R.string.fonts_toast_font_selected,
                            action.font.name,
                        )
                    )
                )
            }

            is FontsAction.OnFontRemoved -> {
                state {
                    copy(
                        fonts = fonts.filterNot {
                            it.uuid == action.font.uuid
                        },
                        selectedUuid = action.selectedUuid,
                    )
                }
                event(
                    FontsEvent.Toast(
                        stringProvider.getString(
                            R.string.fonts_toast_font_removed,
                            action.font.name,
                        )
                    )
                )
            }

            is FontsAction.OnFontImported -> {
                state {
                    copy(searchQuery = "")
                }
                event(FontsEvent.Toast(stringProvider.getString(R.string.fonts_toast_font_added)))
            }

            else -> Unit
        }
    }
}