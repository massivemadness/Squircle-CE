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

import com.blacksquircle.ui.core.database.entity.theme.ThemeEntity
import com.blacksquircle.ui.feature.themes.domain.model.ColorScheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel

private const val STUB_COLOR_HEX = "#000000"
private const val STUB_COLOR_INT = 0x000000

internal fun createThemeEntity(
    uuid: String = "1",
    name: String = "Darcula",
    author: String = "Squircle CE",
): ThemeEntity {
    return ThemeEntity(
        uuid = uuid,
        name = name,
        author = author,
        textColor = STUB_COLOR_HEX,
        cursorColor = STUB_COLOR_HEX,
        backgroundColor = STUB_COLOR_HEX,
        gutterColor = STUB_COLOR_HEX,
        gutterDividerColor = STUB_COLOR_HEX,
        gutterCurrentLineNumberColor = STUB_COLOR_HEX,
        gutterTextColor = STUB_COLOR_HEX,
        selectedLineColor = STUB_COLOR_HEX,
        selectionColor = STUB_COLOR_HEX,
        suggestionQueryColor = STUB_COLOR_HEX,
        findResultBackgroundColor = STUB_COLOR_HEX,
        delimiterBackgroundColor = STUB_COLOR_HEX,
        numberColor = STUB_COLOR_HEX,
        operatorColor = STUB_COLOR_HEX,
        keywordColor = STUB_COLOR_HEX,
        typeColor = STUB_COLOR_HEX,
        langConstColor = STUB_COLOR_HEX,
        preprocessorColor = STUB_COLOR_HEX,
        variableColor = STUB_COLOR_HEX,
        methodColor = STUB_COLOR_HEX,
        stringColor = STUB_COLOR_HEX,
        commentColor = STUB_COLOR_HEX,
        tagColor = STUB_COLOR_HEX,
        tagNameColor = STUB_COLOR_HEX,
        attrNameColor = STUB_COLOR_HEX,
        attrValueColor = STUB_COLOR_HEX,
        entityRefColor = STUB_COLOR_HEX,
    )
}

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
        colorScheme = ColorScheme(
            textColor = STUB_COLOR_INT,
            cursorColor = STUB_COLOR_INT,
            backgroundColor = STUB_COLOR_INT,
            gutterColor = STUB_COLOR_INT,
            gutterDividerColor = STUB_COLOR_INT,
            gutterCurrentLineNumberColor = STUB_COLOR_INT,
            gutterTextColor = STUB_COLOR_INT,
            selectedLineColor = STUB_COLOR_INT,
            selectionColor = STUB_COLOR_INT,
            suggestionQueryColor = STUB_COLOR_INT,
            findResultBackgroundColor = STUB_COLOR_INT,
            delimiterBackgroundColor = STUB_COLOR_INT,
            numberColor = STUB_COLOR_INT,
            operatorColor = STUB_COLOR_INT,
            keywordColor = STUB_COLOR_INT,
            typeColor = STUB_COLOR_INT,
            langConstColor = STUB_COLOR_INT,
            preprocessorColor = STUB_COLOR_INT,
            variableColor = STUB_COLOR_INT,
            methodColor = STUB_COLOR_INT,
            stringColor = STUB_COLOR_INT,
            commentColor = STUB_COLOR_INT,
            tagColor = STUB_COLOR_INT,
            tagNameColor = STUB_COLOR_INT,
            attrNameColor = STUB_COLOR_INT,
            attrValueColor = STUB_COLOR_INT,
            entityRefColor = STUB_COLOR_INT,
        ),
        isExternal = isExternal,
    )
}