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

package com.blacksquircle.ui.feature.fonts.ui.fonts.store

import android.net.Uri
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.redux.MVIAction

internal sealed interface FontsAction : MVIAction {

    data object OnInit : FontsAction

    data object OnBackClicked : FontsAction

    sealed interface QueryAction : FontsAction {
        data class OnQueryChanged(val query: String) : QueryAction
        data object OnClearQueryClicked : QueryAction
    }

    data class OnFontsLoaded(val fonts: List<FontModel>, val selectedUuid: String) : FontsAction
    data class OnFontsFailed(val error: Throwable) : FontsAction

    data class OnSelectClicked(val font: FontModel) : FontsAction
    data class OnFontSelected(val font: FontModel) : FontsAction

    data class OnRemoveClicked(val font: FontModel) : FontsAction
    data class OnFontRemoved(val font: FontModel, val selectedUuid: String) : FontsAction

    data class OnImportFont(val uri: Uri) : FontsAction
    data object OnFontImported : FontsAction
}