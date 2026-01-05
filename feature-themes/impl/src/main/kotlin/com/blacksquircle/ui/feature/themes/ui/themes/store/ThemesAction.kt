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

package com.blacksquircle.ui.feature.themes.ui.themes.store

import android.graphics.Typeface
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.redux.MVIAction

internal sealed interface ThemesAction : MVIAction {

    data object OnInit : ThemesAction

    data object OnBackClicked : ThemesAction

    sealed interface QueryAction : ThemesAction {
        data class OnQueryChanged(val query: String) : QueryAction
        data object OnClearQueryClicked : QueryAction
    }

    data class OnThemesLoaded(
        val themes: List<ThemeModel>,
        val selectedUuid: String,
        val typeface: Typeface
    ) : ThemesAction
    data class OnError(val error: Throwable) : ThemesAction

    data class OnSelectClicked(val theme: ThemeModel) : ThemesAction
    data class OnThemeSelected(val theme: ThemeModel) : ThemesAction

    data class OnRemoveClicked(val theme: ThemeModel) : ThemesAction
    data class OnThemeRemoved(val theme: ThemeModel, val selectedUuid: String) : ThemesAction
}