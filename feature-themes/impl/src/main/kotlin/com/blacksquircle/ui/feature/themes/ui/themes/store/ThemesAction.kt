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

    data object Init : ThemesAction
    data class Error(val error: Throwable) : ThemesAction

    sealed interface UiAction : ThemesAction {

        data object OnBackClicked : UiAction
        data class OnSelectClicked(val theme: ThemeModel) : UiAction
        data class OnRemoveClicked(val theme: ThemeModel) : UiAction

        sealed interface QueryAction : UiAction
        data class OnQueryChanged(val query: String) : QueryAction
        data object OnClearQueryClicked : QueryAction
    }

    sealed interface CommandAction : ThemesAction {

        data class ThemesLoaded(
            val themes: List<ThemeModel>,
            val selectedUuid: String,
            val typeface: Typeface
        ) : CommandAction

        data class ThemeSelected(val theme: ThemeModel) : CommandAction
        data class ThemeRemoved(val theme: ThemeModel, val selectedUuid: String) : CommandAction
    }
}