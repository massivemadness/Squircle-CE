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

package com.blacksquircle.ui.feature.themes

import androidx.core.graphics.toColorInt
import com.blacksquircle.ui.core.storage.database.entity.theme.ThemeEntity
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.feature.themes.data.converter.ThemeConverter
import com.blacksquircle.ui.feature.themes.data.model.ExternalScheme
import com.blacksquircle.ui.feature.themes.data.model.ExternalTheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class ThemeConverterTests {

    @Test
    @Ignore("FIXME: Method parseColor in android.graphics.Color not mocked.")
    fun `convert ThemeEntity to ThemeModel`() {
        val themeEntity = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "Default color scheme",
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
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "Default color scheme",
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
        val convert = ThemeConverter.toModel(themeEntity)

        assertEquals(themeModel.uuid, convert.uuid)
        assertEquals(themeModel.name, convert.name)
        assertEquals(themeModel.author, convert.author)
        assertEquals(themeModel.description, convert.description)
        assertEquals(themeModel.isExternal, convert.isExternal)
        assertEquals(themeModel.colorScheme.textColor, convert.colorScheme.textColor)
        assertEquals(themeModel.colorScheme.cursorColor, convert.colorScheme.cursorColor)
        assertEquals(themeModel.colorScheme.backgroundColor, convert.colorScheme.backgroundColor)
        assertEquals(themeModel.colorScheme.gutterColor, convert.colorScheme.gutterColor)
        assertEquals(themeModel.colorScheme.gutterDividerColor, convert.colorScheme.gutterDividerColor)
        assertEquals(themeModel.colorScheme.gutterCurrentLineNumberColor, convert.colorScheme.gutterCurrentLineNumberColor)
        assertEquals(themeModel.colorScheme.gutterTextColor, convert.colorScheme.gutterTextColor)
        assertEquals(themeModel.colorScheme.selectedLineColor, convert.colorScheme.selectedLineColor)
        assertEquals(themeModel.colorScheme.selectionColor, convert.colorScheme.selectionColor)
        assertEquals(themeModel.colorScheme.suggestionQueryColor, convert.colorScheme.suggestionQueryColor)
        assertEquals(themeModel.colorScheme.findResultBackgroundColor, convert.colorScheme.findResultBackgroundColor)
        assertEquals(themeModel.colorScheme.delimiterBackgroundColor, convert.colorScheme.delimiterBackgroundColor)
        assertEquals(themeModel.colorScheme.numberColor, convert.colorScheme.numberColor)
        assertEquals(themeModel.colorScheme.operatorColor, convert.colorScheme.operatorColor)
        assertEquals(themeModel.colorScheme.keywordColor, convert.colorScheme.keywordColor)
        assertEquals(themeModel.colorScheme.typeColor, convert.colorScheme.typeColor)
        assertEquals(themeModel.colorScheme.langConstColor, convert.colorScheme.langConstColor)
        assertEquals(themeModel.colorScheme.preprocessorColor, convert.colorScheme.preprocessorColor)
        assertEquals(themeModel.colorScheme.variableColor, convert.colorScheme.variableColor)
        assertEquals(themeModel.colorScheme.methodColor, convert.colorScheme.methodColor)
        assertEquals(themeModel.colorScheme.stringColor, convert.colorScheme.stringColor)
        assertEquals(themeModel.colorScheme.commentColor, convert.colorScheme.commentColor)
        assertEquals(themeModel.colorScheme.tagColor, convert.colorScheme.tagColor)
        assertEquals(themeModel.colorScheme.tagNameColor, convert.colorScheme.tagNameColor)
        assertEquals(themeModel.colorScheme.attrNameColor, convert.colorScheme.attrNameColor)
        assertEquals(themeModel.colorScheme.attrValueColor, convert.colorScheme.attrValueColor)
        assertEquals(themeModel.colorScheme.entityRefColor, convert.colorScheme.entityRefColor)
    }

    @Test
    @Ignore("FIXME: Method parseColor in android.graphics.Color not mocked.")
    fun `convert ThemeModel to ThemeEntity`() {
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "Default color scheme",
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
        val themeEntity = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "Default color scheme",
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
        val convert = ThemeConverter.toEntity(themeModel)

        assertEquals(themeEntity.uuid, convert.uuid)
        assertEquals(themeEntity.name, convert.name)
        assertEquals(themeEntity.author, convert.author)
        assertEquals(themeEntity.description, convert.description)
        assertEquals(themeEntity.textColor, convert.textColor)
        assertEquals(themeEntity.cursorColor, convert.cursorColor)
        assertEquals(themeEntity.backgroundColor, convert.backgroundColor)
        assertEquals(themeEntity.gutterColor, convert.gutterColor)
        assertEquals(themeEntity.gutterDividerColor, convert.gutterDividerColor)
        assertEquals(themeEntity.gutterCurrentLineNumberColor, convert.gutterCurrentLineNumberColor)
        assertEquals(themeEntity.gutterTextColor, convert.gutterTextColor)
        assertEquals(themeEntity.selectedLineColor, convert.selectedLineColor)
        assertEquals(themeEntity.selectionColor, convert.selectionColor)
        assertEquals(themeEntity.suggestionQueryColor, convert.suggestionQueryColor)
        assertEquals(themeEntity.findResultBackgroundColor, convert.findResultBackgroundColor)
        assertEquals(themeEntity.delimiterBackgroundColor, convert.delimiterBackgroundColor)
        assertEquals(themeEntity.numberColor, convert.numberColor)
        assertEquals(themeEntity.operatorColor, convert.operatorColor)
        assertEquals(themeEntity.keywordColor, convert.keywordColor)
        assertEquals(themeEntity.typeColor, convert.typeColor)
        assertEquals(themeEntity.langConstColor, convert.langConstColor)
        assertEquals(themeEntity.preprocessorColor, convert.preprocessorColor)
        assertEquals(themeEntity.variableColor, convert.variableColor)
        assertEquals(themeEntity.methodColor, convert.methodColor)
        assertEquals(themeEntity.stringColor, convert.stringColor)
        assertEquals(themeEntity.commentColor, convert.commentColor)
        assertEquals(themeEntity.tagColor, convert.tagColor)
        assertEquals(themeEntity.tagNameColor, convert.tagNameColor)
        assertEquals(themeEntity.attrNameColor, convert.attrNameColor)
        assertEquals(themeEntity.attrValueColor, convert.attrValueColor)
        assertEquals(themeEntity.entityRefColor, convert.entityRefColor)
    }

    @Test
    @Ignore("FIXME: Method parseColor in android.graphics.Color not mocked.")
    fun `convert ExternalTheme to ThemeModel`() {
        val externalTheme = ExternalTheme(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "Default color scheme",
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
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle CE",
            description = "Default color scheme",
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
        val convert = ThemeConverter.toModel(externalTheme)

        assertEquals(themeModel.uuid, convert.uuid)
        assertEquals(themeModel.name, convert.name)
        assertEquals(themeModel.author, convert.author)
        assertEquals(themeModel.description, convert.description)
        assertEquals(themeModel.isExternal, convert.isExternal)
        assertEquals(themeModel.colorScheme.textColor, convert.colorScheme.textColor)
        assertEquals(themeModel.colorScheme.cursorColor, convert.colorScheme.cursorColor)
        assertEquals(themeModel.colorScheme.backgroundColor, convert.colorScheme.backgroundColor)
        assertEquals(themeModel.colorScheme.gutterColor, convert.colorScheme.gutterColor)
        assertEquals(themeModel.colorScheme.gutterDividerColor, convert.colorScheme.gutterDividerColor)
        assertEquals(themeModel.colorScheme.gutterCurrentLineNumberColor, convert.colorScheme.gutterCurrentLineNumberColor)
        assertEquals(themeModel.colorScheme.gutterTextColor, convert.colorScheme.gutterTextColor)
        assertEquals(themeModel.colorScheme.selectedLineColor, convert.colorScheme.selectedLineColor)
        assertEquals(themeModel.colorScheme.selectionColor, convert.colorScheme.selectionColor)
        assertEquals(themeModel.colorScheme.suggestionQueryColor, convert.colorScheme.suggestionQueryColor)
        assertEquals(themeModel.colorScheme.findResultBackgroundColor, convert.colorScheme.findResultBackgroundColor)
        assertEquals(themeModel.colorScheme.delimiterBackgroundColor, convert.colorScheme.delimiterBackgroundColor)
        assertEquals(themeModel.colorScheme.numberColor, convert.colorScheme.numberColor)
        assertEquals(themeModel.colorScheme.operatorColor, convert.colorScheme.operatorColor)
        assertEquals(themeModel.colorScheme.keywordColor, convert.colorScheme.keywordColor)
        assertEquals(themeModel.colorScheme.typeColor, convert.colorScheme.typeColor)
        assertEquals(themeModel.colorScheme.langConstColor, convert.colorScheme.langConstColor)
        assertEquals(themeModel.colorScheme.preprocessorColor, convert.colorScheme.preprocessorColor)
        assertEquals(themeModel.colorScheme.variableColor, convert.colorScheme.variableColor)
        assertEquals(themeModel.colorScheme.methodColor, convert.colorScheme.methodColor)
        assertEquals(themeModel.colorScheme.stringColor, convert.colorScheme.stringColor)
        assertEquals(themeModel.colorScheme.commentColor, convert.colorScheme.commentColor)
        assertEquals(themeModel.colorScheme.tagColor, convert.colorScheme.tagColor)
        assertEquals(themeModel.colorScheme.tagNameColor, convert.colorScheme.tagNameColor)
        assertEquals(themeModel.colorScheme.attrNameColor, convert.colorScheme.attrNameColor)
        assertEquals(themeModel.colorScheme.attrValueColor, convert.colorScheme.attrValueColor)
        assertEquals(themeModel.colorScheme.entityRefColor, convert.colorScheme.entityRefColor)
    }
}