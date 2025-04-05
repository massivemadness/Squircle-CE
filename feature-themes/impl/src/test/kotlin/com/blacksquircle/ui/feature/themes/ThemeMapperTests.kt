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

import androidx.core.graphics.toColorInt
import com.blacksquircle.ui.core.database.entity.theme.ThemeEntity
import com.blacksquircle.ui.feature.themes.data.mapper.ThemeMapper
import com.blacksquircle.ui.feature.themes.data.model.ExternalScheme
import com.blacksquircle.ui.feature.themes.data.model.ExternalTheme
import com.blacksquircle.ui.feature.themes.domain.model.ColorScheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class ThemeMapperTests {

    @Test
    @Ignore("FIXME: Method parseColor in android.graphics.Color not mocked.")
    fun `When mapping ThemeEntity Then return ThemeModel`() {
        // Given
        val themeEntity = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "",
            textColor = "#FFFFFF",
            cursorColor = "#BBBBBB",
            backgroundColor = "#303030",
            gutterColor = "#F0F0F0",
            gutterDividerColor = "#FFFFFF",
            gutterCurrentLineNumberColor = "#FEFEFE",
            gutterTextColor = "#FFFFFF",
            selectedLineColor = "#EEEEEE",
            selectionColor = "#FF3000",
            suggestionQueryColor = "#FF9000",
            findResultBackgroundColor = "#FEFEFE",
            delimiterBackgroundColor = "#FEFEFE",
            numberColor = "#FF3000",
            operatorColor = "#FF3000",
            keywordColor = "#FF3000",
            typeColor = "#FF3000",
            langConstColor = "#FF3000",
            preprocessorColor = "FF3000",
            variableColor = "#FF3000",
            methodColor = "#FF3000",
            stringColor = "#FF3000",
            commentColor = "#FF3000",
            tagColor = "#FF3000",
            tagNameColor = "#FF3000",
            attrNameColor = "#FF3000",
            attrValueColor = "#FF3000",
            entityRefColor = "#FF3000",
        )
        val expected = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = "#FFFFFF".toColorInt(),
                cursorColor = "#BBBBBB".toColorInt(),
                backgroundColor = "#303030".toColorInt(),
                gutterColor = "#F0F0F0".toColorInt(),
                gutterDividerColor = "#FFFFFF".toColorInt(),
                gutterCurrentLineNumberColor = "#FEFEFE".toColorInt(),
                gutterTextColor = "#FFFFFF".toColorInt(),
                selectedLineColor = "#EEEEEE".toColorInt(),
                selectionColor = "#FF3000".toColorInt(),
                suggestionQueryColor = "#FF9000".toColorInt(),
                findResultBackgroundColor = "#FEFEFE".toColorInt(),
                delimiterBackgroundColor = "#FEFEFE".toColorInt(),
                numberColor = "#FF3000".toColorInt(),
                operatorColor = "#FF3000".toColorInt(),
                keywordColor = "#FF3000".toColorInt(),
                typeColor = "#FF3000".toColorInt(),
                langConstColor = "#FF3000".toColorInt(),
                preprocessorColor = "FF3000".toColorInt(),
                variableColor = "FF3000".toColorInt(),
                methodColor = "#FF3000".toColorInt(),
                stringColor = "#FF3000".toColorInt(),
                commentColor = "#FF3000".toColorInt(),
                tagColor = "#FF3000".toColorInt(),
                tagNameColor = "#FF3000".toColorInt(),
                attrNameColor = "#FF3000".toColorInt(),
                attrValueColor = "#FF3000".toColorInt(),
                entityRefColor = "#FF3000".toColorInt(),
            ),
        )

        // When
        val actual = ThemeMapper.toModel(themeEntity)

        // Then
        assertEquals(expected.uuid, actual.uuid)
        assertEquals(expected.name, actual.name)
        assertEquals(expected.author, actual.author)
        assertEquals(expected.isExternal, actual.isExternal)
        assertEquals(expected.colorScheme.textColor, actual.colorScheme.textColor)
        assertEquals(expected.colorScheme.cursorColor, actual.colorScheme.cursorColor)
        assertEquals(expected.colorScheme.backgroundColor, actual.colorScheme.backgroundColor)
        assertEquals(expected.colorScheme.gutterColor, actual.colorScheme.gutterColor)
        assertEquals(expected.colorScheme.gutterDividerColor, actual.colorScheme.gutterDividerColor)
        assertEquals(expected.colorScheme.gutterCurrentLineNumberColor, actual.colorScheme.gutterCurrentLineNumberColor)
        assertEquals(expected.colorScheme.gutterTextColor, actual.colorScheme.gutterTextColor)
        assertEquals(expected.colorScheme.selectedLineColor, actual.colorScheme.selectedLineColor)
        assertEquals(expected.colorScheme.selectionColor, actual.colorScheme.selectionColor)
        assertEquals(expected.colorScheme.suggestionQueryColor, actual.colorScheme.suggestionQueryColor)
        assertEquals(expected.colorScheme.findResultBackgroundColor, actual.colorScheme.findResultBackgroundColor)
        assertEquals(expected.colorScheme.delimiterBackgroundColor, actual.colorScheme.delimiterBackgroundColor)
        assertEquals(expected.colorScheme.numberColor, actual.colorScheme.numberColor)
        assertEquals(expected.colorScheme.operatorColor, actual.colorScheme.operatorColor)
        assertEquals(expected.colorScheme.keywordColor, actual.colorScheme.keywordColor)
        assertEquals(expected.colorScheme.typeColor, actual.colorScheme.typeColor)
        assertEquals(expected.colorScheme.langConstColor, actual.colorScheme.langConstColor)
        assertEquals(expected.colorScheme.preprocessorColor, actual.colorScheme.preprocessorColor)
        assertEquals(expected.colorScheme.variableColor, actual.colorScheme.variableColor)
        assertEquals(expected.colorScheme.methodColor, actual.colorScheme.methodColor)
        assertEquals(expected.colorScheme.stringColor, actual.colorScheme.stringColor)
        assertEquals(expected.colorScheme.commentColor, actual.colorScheme.commentColor)
        assertEquals(expected.colorScheme.tagColor, actual.colorScheme.tagColor)
        assertEquals(expected.colorScheme.tagNameColor, actual.colorScheme.tagNameColor)
        assertEquals(expected.colorScheme.attrNameColor, actual.colorScheme.attrNameColor)
        assertEquals(expected.colorScheme.attrValueColor, actual.colorScheme.attrValueColor)
        assertEquals(expected.colorScheme.entityRefColor, actual.colorScheme.entityRefColor)
    }

    @Test
    @Ignore("FIXME: Method parseColor in android.graphics.Color not mocked.")
    fun `When mapping ThemeModel Then return ThemeEntity`() {
        // Given
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = "#FFFFFF".toColorInt(),
                cursorColor = "#BBBBBB".toColorInt(),
                backgroundColor = "#303030".toColorInt(),
                gutterColor = "#F0F0F0".toColorInt(),
                gutterDividerColor = "#FFFFFF".toColorInt(),
                gutterCurrentLineNumberColor = "#FEFEFE".toColorInt(),
                gutterTextColor = "#FFFFFF".toColorInt(),
                selectedLineColor = "#EEEEEE".toColorInt(),
                selectionColor = "#FF3000".toColorInt(),
                suggestionQueryColor = "#FF9000".toColorInt(),
                findResultBackgroundColor = "#FEFEFE".toColorInt(),
                delimiterBackgroundColor = "#FEFEFE".toColorInt(),
                numberColor = "#FF3000".toColorInt(),
                operatorColor = "#FF3000".toColorInt(),
                keywordColor = "#FF3000".toColorInt(),
                typeColor = "#FF3000".toColorInt(),
                langConstColor = "#FF3000".toColorInt(),
                preprocessorColor = "FF3000".toColorInt(),
                variableColor = "#FF3000".toColorInt(),
                methodColor = "#FF3000".toColorInt(),
                stringColor = "#FF3000".toColorInt(),
                commentColor = "#FF3000".toColorInt(),
                tagColor = "#FF3000".toColorInt(),
                tagNameColor = "#FF3000".toColorInt(),
                attrNameColor = "#FF3000".toColorInt(),
                attrValueColor = "#FF3000".toColorInt(),
                entityRefColor = "#FF3000".toColorInt(),
            )
        )
        val expected = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "",
            textColor = "#FFFFFF",
            cursorColor = "#BBBBBB",
            backgroundColor = "#303030",
            gutterColor = "#F0F0F0",
            gutterDividerColor = "#FFFFFF",
            gutterCurrentLineNumberColor = "#FEFEFE",
            gutterTextColor = "#FFFFFF",
            selectedLineColor = "#EEEEEE",
            selectionColor = "#FF3000",
            suggestionQueryColor = "#FF9000",
            findResultBackgroundColor = "#FEFEFE",
            delimiterBackgroundColor = "#FEFEFE",
            numberColor = "#FF3000",
            operatorColor = "#FF3000",
            keywordColor = "#FF3000",
            typeColor = "#FF3000",
            langConstColor = "#FF3000",
            preprocessorColor = "FF3000",
            variableColor = "#FF3000",
            methodColor = "#FF3000",
            stringColor = "#FF3000",
            commentColor = "#FF3000",
            tagColor = "#FF3000",
            tagNameColor = "#FF3000",
            attrNameColor = "#FF3000",
            attrValueColor = "#FF3000",
            entityRefColor = "#FF3000",
        )

        // When
        val actual = ThemeMapper.toEntity(themeModel)

        // Then
        assertEquals(expected.uuid, actual.uuid)
        assertEquals(expected.name, actual.name)
        assertEquals(expected.author, actual.author)
        assertEquals(expected.textColor, actual.textColor)
        assertEquals(expected.cursorColor, actual.cursorColor)
        assertEquals(expected.backgroundColor, actual.backgroundColor)
        assertEquals(expected.gutterColor, actual.gutterColor)
        assertEquals(expected.gutterDividerColor, actual.gutterDividerColor)
        assertEquals(expected.gutterCurrentLineNumberColor, actual.gutterCurrentLineNumberColor)
        assertEquals(expected.gutterTextColor, actual.gutterTextColor)
        assertEquals(expected.selectedLineColor, actual.selectedLineColor)
        assertEquals(expected.selectionColor, actual.selectionColor)
        assertEquals(expected.suggestionQueryColor, actual.suggestionQueryColor)
        assertEquals(expected.findResultBackgroundColor, actual.findResultBackgroundColor)
        assertEquals(expected.delimiterBackgroundColor, actual.delimiterBackgroundColor)
        assertEquals(expected.numberColor, actual.numberColor)
        assertEquals(expected.operatorColor, actual.operatorColor)
        assertEquals(expected.keywordColor, actual.keywordColor)
        assertEquals(expected.typeColor, actual.typeColor)
        assertEquals(expected.langConstColor, actual.langConstColor)
        assertEquals(expected.preprocessorColor, actual.preprocessorColor)
        assertEquals(expected.variableColor, actual.variableColor)
        assertEquals(expected.methodColor, actual.methodColor)
        assertEquals(expected.stringColor, actual.stringColor)
        assertEquals(expected.commentColor, actual.commentColor)
        assertEquals(expected.tagColor, actual.tagColor)
        assertEquals(expected.tagNameColor, actual.tagNameColor)
        assertEquals(expected.attrNameColor, actual.attrNameColor)
        assertEquals(expected.attrValueColor, actual.attrValueColor)
        assertEquals(expected.entityRefColor, actual.entityRefColor)
    }

    @Test
    @Ignore("FIXME: Method parseColor in android.graphics.Color not mocked.")
    fun `When mapping ExternalTheme Then return ThemeModel`() {
        // Given
        val externalTheme = ExternalTheme(
            name = "Test",
            author = "Squircle CE",
            externalScheme = ExternalScheme(
                textColor = "#FFFFFF",
                cursorColor = "#BBBBBB",
                backgroundColor = "#303030",
                gutterColor = "#F0F0F0",
                gutterDividerColor = "#FFFFFF",
                gutterCurrentLineNumberColor = "#FEFEFE",
                gutterTextColor = "#FFFFFF",
                selectedLineColor = "#EEEEEE",
                selectionColor = "#FF3000",
                suggestionQueryColor = "#FF9000",
                findResultBackgroundColor = "#FEFEFE",
                delimiterBackgroundColor = "#FEFEFE",
                numberColor = "#FF3000",
                operatorColor = "#FF3000",
                keywordColor = "#FF3000",
                typeColor = "#FF3000",
                langConstColor = "#FF3000",
                preprocessorColor = "FF3000",
                variableColor = "#FF3000",
                methodColor = "#FF3000",
                stringColor = "#FF3000",
                commentColor = "#FF3000",
                tagColor = "#FF3000",
                tagNameColor = "#FF3000",
                attrNameColor = "#FF3000",
                attrValueColor = "#FF3000",
                entityRefColor = "#FF3000",
            ),
        )
        val expected = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = "#FFFFFF".toColorInt(),
                cursorColor = "#BBBBBB".toColorInt(),
                backgroundColor = "#303030".toColorInt(),
                gutterColor = "#F0F0F0".toColorInt(),
                gutterDividerColor = "#FFFFFF".toColorInt(),
                gutterCurrentLineNumberColor = "#FEFEFE".toColorInt(),
                gutterTextColor = "#FFFFFF".toColorInt(),
                selectedLineColor = "#EEEEEE".toColorInt(),
                selectionColor = "#FF3000".toColorInt(),
                suggestionQueryColor = "#FF9000".toColorInt(),
                findResultBackgroundColor = "#FEFEFE".toColorInt(),
                delimiterBackgroundColor = "#FEFEFE".toColorInt(),
                numberColor = "#FF3000".toColorInt(),
                operatorColor = "#FF3000".toColorInt(),
                keywordColor = "#FF3000".toColorInt(),
                typeColor = "#FF3000".toColorInt(),
                langConstColor = "#FF3000".toColorInt(),
                preprocessorColor = "FF3000".toColorInt(),
                variableColor = "#FF3000".toColorInt(),
                methodColor = "#FF3000".toColorInt(),
                stringColor = "#FF3000".toColorInt(),
                commentColor = "#FF3000".toColorInt(),
                tagColor = "#FF3000".toColorInt(),
                tagNameColor = "#FF3000".toColorInt(),
                attrNameColor = "#FF3000".toColorInt(),
                attrValueColor = "#FF3000".toColorInt(),
                entityRefColor = "#FF3000".toColorInt(),
            ),
        )

        // When
        val actual = ThemeMapper.toModel(externalTheme)

        // Then
        assertEquals(expected.uuid, actual.uuid)
        assertEquals(expected.name, actual.name)
        assertEquals(expected.author, actual.author)
        assertEquals(expected.isExternal, actual.isExternal)
        assertEquals(expected.colorScheme.textColor, actual.colorScheme.textColor)
        assertEquals(expected.colorScheme.cursorColor, actual.colorScheme.cursorColor)
        assertEquals(expected.colorScheme.backgroundColor, actual.colorScheme.backgroundColor)
        assertEquals(expected.colorScheme.gutterColor, actual.colorScheme.gutterColor)
        assertEquals(expected.colorScheme.gutterDividerColor, actual.colorScheme.gutterDividerColor)
        assertEquals(expected.colorScheme.gutterCurrentLineNumberColor, actual.colorScheme.gutterCurrentLineNumberColor)
        assertEquals(expected.colorScheme.gutterTextColor, actual.colorScheme.gutterTextColor)
        assertEquals(expected.colorScheme.selectedLineColor, actual.colorScheme.selectedLineColor)
        assertEquals(expected.colorScheme.selectionColor, actual.colorScheme.selectionColor)
        assertEquals(expected.colorScheme.suggestionQueryColor, actual.colorScheme.suggestionQueryColor)
        assertEquals(expected.colorScheme.findResultBackgroundColor, actual.colorScheme.findResultBackgroundColor)
        assertEquals(expected.colorScheme.delimiterBackgroundColor, actual.colorScheme.delimiterBackgroundColor)
        assertEquals(expected.colorScheme.numberColor, actual.colorScheme.numberColor)
        assertEquals(expected.colorScheme.operatorColor, actual.colorScheme.operatorColor)
        assertEquals(expected.colorScheme.keywordColor, actual.colorScheme.keywordColor)
        assertEquals(expected.colorScheme.typeColor, actual.colorScheme.typeColor)
        assertEquals(expected.colorScheme.langConstColor, actual.colorScheme.langConstColor)
        assertEquals(expected.colorScheme.preprocessorColor, actual.colorScheme.preprocessorColor)
        assertEquals(expected.colorScheme.variableColor, actual.colorScheme.variableColor)
        assertEquals(expected.colorScheme.methodColor, actual.colorScheme.methodColor)
        assertEquals(expected.colorScheme.stringColor, actual.colorScheme.stringColor)
        assertEquals(expected.colorScheme.commentColor, actual.colorScheme.commentColor)
        assertEquals(expected.colorScheme.tagColor, actual.colorScheme.tagColor)
        assertEquals(expected.colorScheme.tagNameColor, actual.colorScheme.tagNameColor)
        assertEquals(expected.colorScheme.attrNameColor, actual.colorScheme.attrNameColor)
        assertEquals(expected.colorScheme.attrValueColor, actual.colorScheme.attrValueColor)
        assertEquals(expected.colorScheme.entityRefColor, actual.colorScheme.entityRefColor)
    }
}