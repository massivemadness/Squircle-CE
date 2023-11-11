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

import android.net.Uri
import com.blacksquircle.ui.core.mvi.ViewIntent
import com.blacksquircle.ui.feature.themes.domain.model.Meta
import com.blacksquircle.ui.feature.themes.domain.model.Property
import com.blacksquircle.ui.feature.themes.domain.model.PropertyItem
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel

sealed class ThemeIntent : ViewIntent() {

    data object LoadThemes : ThemeIntent()

    data class SearchThemes(val query: String) : ThemeIntent()
    data class ImportTheme(val fileUri: Uri) : ThemeIntent()
    data class ExportTheme(val themeModel: ThemeModel, val fileUri: Uri) : ThemeIntent()
    data class SelectTheme(val themeModel: ThemeModel) : ThemeIntent()
    data class RemoveTheme(val themeModel: ThemeModel) : ThemeIntent()

    data class LoadProperties(val uuid: String?) : ThemeIntent()
    data class CreateTheme(val meta: Meta, val properties: List<PropertyItem>) : ThemeIntent()
    data class ChooseColor(val key: Property, val value: String) : ThemeIntent()

    data class ChangeName(val value: String) : ThemeIntent()
    data class ChangeAuthor(val value: String) : ThemeIntent()
    data class ChangeDescription(val value: String) : ThemeIntent()
    data class ChangeColor(val key: String, val value: String) : ThemeIntent()
}