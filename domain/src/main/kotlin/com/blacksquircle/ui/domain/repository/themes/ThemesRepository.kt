/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.domain.repository.themes

import android.net.Uri
import com.blacksquircle.ui.domain.model.themes.Meta
import com.blacksquircle.ui.domain.model.themes.PropertyItem
import com.blacksquircle.ui.domain.model.themes.ThemeModel

interface ThemesRepository {

    suspend fun fetchThemes(searchQuery: String): List<ThemeModel>
    suspend fun fetchTheme(uuid: String): ThemeModel

    suspend fun importTheme(uri: Uri): ThemeModel
    suspend fun exportTheme(themeModel: ThemeModel)

    suspend fun createTheme(meta: Meta, properties: List<PropertyItem>)
    suspend fun removeTheme(themeModel: ThemeModel)
    suspend fun selectTheme(themeModel: ThemeModel)
}