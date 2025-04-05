/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.themes.ui.thememaker

import androidx.annotation.ColorInt
import androidx.compose.runtime.Immutable
import androidx.core.graphics.toColorInt
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.themes.data.mapper.ThemeMapper
import com.blacksquircle.ui.feature.themes.domain.model.Property
import com.blacksquircle.ui.feature.themes.domain.model.PropertyItem

@Immutable
internal data class EditThemeViewState(
    val isEditMode: Boolean = false,
    val name: String = "",
    val author: String = "",
    val properties: List<PropertyItem> = Property.entries.map { property ->
        PropertyItem(
            propertyKey = property,
            propertyValue = ThemeMapper.FALLBACK_COLOR,
        )
    },
    val invalidName: Boolean = false,
    val invalidAuthor: Boolean = false,
) : ViewState {

    @ColorInt
    fun getColor(property: Property): Int {
        return properties
            .find { it.propertyKey == property }
            ?.propertyValue?.toColorInt()
            ?: ThemeMapper.FALLBACK_COLOR.toColorInt()
    }
}