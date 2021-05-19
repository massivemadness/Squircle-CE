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

package com.blacksquircle.ui.data.converter

import androidx.core.graphics.toColorInt
import com.blacksquircle.ui.data.model.themes.ExternalScheme
import com.blacksquircle.ui.data.model.themes.ExternalTheme
import com.blacksquircle.ui.data.storage.database.entity.theme.ThemeEntity
import com.blacksquircle.ui.domain.model.themes.ThemeModel
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeConverterTest {

    @Test
    fun `convert ThemeEntity to ThemeModel`() {
        val themeEntity = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Squircle IDE",
            description = "Default color scheme",
            textColor = "#FFFFFF",
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
            entityRefColor = "#FF3000"
        )
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle IDE",
            description = "Default color scheme",
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = "#FFFFFF".toColorInt(),
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
                syntaxScheme = SyntaxScheme(
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
                    entityRefColor = "#FF3000".toColorInt()
                )
            )
        )
        val convert = ThemeConverter.toModel(themeEntity)

        assertEquals(themeModel.uuid, convert.uuid)
        assertEquals(themeModel.name, convert.name)
        assertEquals(themeModel.author, convert.author)
        assertEquals(themeModel.description, convert.description)
        assertEquals(themeModel.isExternal, convert.isExternal)
        assertEquals(themeModel.colorScheme.textColor, convert.colorScheme.textColor)
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
        assertEquals(themeModel.colorScheme.syntaxScheme.numberColor, convert.colorScheme.syntaxScheme.numberColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.operatorColor, convert.colorScheme.syntaxScheme.operatorColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.keywordColor, convert.colorScheme.syntaxScheme.keywordColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.typeColor, convert.colorScheme.syntaxScheme.typeColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.langConstColor, convert.colorScheme.syntaxScheme.langConstColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.preprocessorColor, convert.colorScheme.syntaxScheme.preprocessorColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.variableColor, convert.colorScheme.syntaxScheme.variableColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.methodColor, convert.colorScheme.syntaxScheme.methodColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.stringColor, convert.colorScheme.syntaxScheme.stringColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.commentColor, convert.colorScheme.syntaxScheme.commentColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.tagColor, convert.colorScheme.syntaxScheme.tagColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.tagNameColor, convert.colorScheme.syntaxScheme.tagNameColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.attrNameColor, convert.colorScheme.syntaxScheme.attrNameColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.attrValueColor, convert.colorScheme.syntaxScheme.attrValueColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.entityRefColor, convert.colorScheme.syntaxScheme.entityRefColor)
    }

    /*@Test
    fun `convert ThemeModel to ThemeEntity`() {
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle IDE",
            description = "Default color scheme",
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = "#FFFFFF".toColorInt(),
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
                syntaxScheme = SyntaxScheme(
                    numberColor = "#FF3000".toColorInt(),
                    operatorColor = "#FF3000".toColorInt(),
                    keywordColor = "#FF3000".toColorInt(),
                    typeColor = "#FF3000".toColorInt(),
                    langConstColor = "#FF3000".toColorInt(),
                    preprocessorColor = "FF3000".toColorInt(),
                    variableColor = "#FF3000".toColorInt(),
                    methodColor = "#FF3000".toColorInt(),
                    stringColor = "#FF3000".toColorInt(),
                    commentColor = "#FF3000".toColorInt()
                )
            )
        )
        val themeEntity = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Squircle IDE",
            description = "Default color scheme",
            textColor = "#FFFFFF",
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
            commentColor = "#FF3000"
        )
        val convert = ThemeConverter.toEntity(themeModel)

        assertEquals(themeEntity.uuid, convert.uuid)
        assertEquals(themeEntity.name, convert.name)
        assertEquals(themeEntity.author, convert.author)
        assertEquals(themeEntity.description, convert.description)
        assertEquals(themeEntity.isExternal, convert.isExternal)
        assertEquals(themeEntity.textColor, convert.textColor)
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
    }*/

    @Test
    fun `convert ExternalTheme to ThemeModel`() {
        val externalTheme = ExternalTheme(
                uuid = "0",
                name = "Test",
                author = "Squircle IDE",
                description = "Default color scheme",
                externalScheme = ExternalScheme(
                    textColor = "#FFFFFF",
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
                    entityRefColor = "#FF3000"
                )
            )
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Squircle IDE",
            description = "Default color scheme",
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = "#FFFFFF".toColorInt(),
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
                syntaxScheme = SyntaxScheme(
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
                    entityRefColor = "#FF3000".toColorInt()
                )
            )
        )
        val convert = ThemeConverter.toModel(externalTheme)

        assertEquals(themeModel.uuid, convert.uuid)
        assertEquals(themeModel.name, convert.name)
        assertEquals(themeModel.author, convert.author)
        assertEquals(themeModel.description, convert.description)
        assertEquals(themeModel.isExternal, convert.isExternal)
        assertEquals(themeModel.colorScheme.textColor, convert.colorScheme.textColor)
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
        assertEquals(themeModel.colorScheme.syntaxScheme.numberColor, convert.colorScheme.syntaxScheme.numberColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.operatorColor, convert.colorScheme.syntaxScheme.operatorColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.keywordColor, convert.colorScheme.syntaxScheme.keywordColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.typeColor, convert.colorScheme.syntaxScheme.typeColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.langConstColor, convert.colorScheme.syntaxScheme.langConstColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.preprocessorColor, convert.colorScheme.syntaxScheme.preprocessorColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.variableColor, convert.colorScheme.syntaxScheme.variableColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.methodColor, convert.colorScheme.syntaxScheme.methodColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.stringColor, convert.colorScheme.syntaxScheme.stringColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.commentColor, convert.colorScheme.syntaxScheme.commentColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.tagColor, convert.colorScheme.syntaxScheme.tagColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.tagNameColor, convert.colorScheme.syntaxScheme.tagNameColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.attrNameColor, convert.colorScheme.syntaxScheme.attrNameColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.attrValueColor, convert.colorScheme.syntaxScheme.attrValueColor)
        assertEquals(themeModel.colorScheme.syntaxScheme.entityRefColor, convert.colorScheme.syntaxScheme.entityRefColor)
    }
}