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

package com.blacksquircle.ui.feature.themes

import com.blacksquircle.ui.feature.themes.domain.model.ColorModel
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel

private const val STUB_COLOR_INT = 0x000000

internal fun createThemeModel(
    uuid: String = "1",
    name: String = "Darcula",
    author: String = "Squircle CE",
    isExternal: Boolean = true,
): ThemeModel {
    return ThemeModel(
        uuid = uuid,
        name = name,
        author = author,
        colors = ColorModel(
            textColor = STUB_COLOR_INT,
            backgroundColor = STUB_COLOR_INT,
            numberColor = STUB_COLOR_INT,
            operatorColor = STUB_COLOR_INT,
            keywordColor = STUB_COLOR_INT,
            variableColor = STUB_COLOR_INT,
            functionColor = STUB_COLOR_INT,
            stringColor = STUB_COLOR_INT,
            commentColor = STUB_COLOR_INT,
        ),
        isExternal = isExternal,
    )
}