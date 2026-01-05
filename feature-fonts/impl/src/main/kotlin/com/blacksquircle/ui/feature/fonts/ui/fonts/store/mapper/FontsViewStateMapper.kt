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

package com.blacksquircle.ui.feature.fonts.ui.fonts.store.mapper

import com.blacksquircle.ui.feature.fonts.ui.fonts.FontsViewState
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsState
import com.blacksquircle.ui.redux.mapper.ViewStateMapper
import javax.inject.Inject

internal class FontsViewStateMapper @Inject constructor() : ViewStateMapper<FontsState, FontsViewState> {

    override fun map(state: FontsState): FontsViewState {
        return FontsViewState(
            searchQuery = state.searchQuery,
            fonts = state.fonts,
            selectedUuid = state.selectedUuid,
            isLoading = state.isLoading,
            isEmpty = state.fonts.isEmpty(),
        )
    }
}