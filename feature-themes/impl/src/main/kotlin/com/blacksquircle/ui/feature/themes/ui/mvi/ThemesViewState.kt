/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.themes.ui.mvi

import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel

sealed class ThemesViewState : ViewState() {

    abstract val query: String

    data object Loading : ThemesViewState() {
        override val query: String = ""
    }

    data class Empty(
        override val query: String,
    ) : ThemesViewState()

    data class Data(
        override val query: String,
        val themes: List<ThemeModel>,
    ) : ThemesViewState()
}