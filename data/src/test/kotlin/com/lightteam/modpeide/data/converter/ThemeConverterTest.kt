/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.data.converter

import android.graphics.Color
import com.lightteam.editorkit.feature.colorscheme.ColorScheme
import com.lightteam.language.base.model.SyntaxScheme
import com.lightteam.modpeide.data.model.theme.ExternalScheme
import com.lightteam.modpeide.data.model.theme.ExternalTheme
import com.lightteam.modpeide.database.entity.theme.ThemeEntity
import com.lightteam.modpeide.domain.model.theme.ThemeModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeConverterTest {

    @Test
    fun `convert ThemeEntity to ThemeModel`() {
        val themeEntity = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Light Team Software",
            description = "Default color scheme",
            isExternal = false,
            isPaid = false,
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
            methodColor = "#FF3000",
            stringColor = "#FF3000",
            commentColor = "#FF3000"
        )
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Light Team Software",
            description = "Default color scheme",
            isExternal = false,
            isPaid = false,
            colorScheme = ColorScheme(
                textColor = Color.parseColor("#FFFFFF"),
                backgroundColor = Color.parseColor("#303030"),
                gutterColor = Color.parseColor("#F0F0F0"),
                gutterDividerColor = Color.parseColor("#FFFFFF"),
                gutterCurrentLineNumberColor = Color.parseColor("#FEFEFE"),
                gutterTextColor = Color.parseColor("#FFFFFF"),
                selectedLineColor = Color.parseColor("#EEEEEE"),
                selectionColor = Color.parseColor("#FF3000"),
                suggestionQueryColor = Color.parseColor("#FF9000"),
                findResultBackgroundColor = Color.parseColor("#FEFEFE"),
                delimiterBackgroundColor = Color.parseColor("#FEFEFE"),
                numberColor = Color.parseColor("#FF3000"),
                operatorColor = Color.parseColor("#FF3000"),
                keywordColor = Color.parseColor("#FF3000"),
                typeColor = Color.parseColor("#FF3000"),
                langConstColor = Color.parseColor("#FF3000"),
                methodColor = Color.parseColor("#FF3000"),
                stringColor = Color.parseColor("#FF3000"),
                commentColor = Color.parseColor("#FF3000")
            )
        )
        val convert = ThemeConverter.toModel(themeEntity)

        assertEquals(themeModel.uuid, convert.uuid)
        assertEquals(themeModel.name, convert.name)
        assertEquals(themeModel.author, convert.author)
        assertEquals(themeModel.description, convert.description)
        assertEquals(themeModel.isExternal, convert.isExternal)
        assertEquals(themeModel.isPaid, convert.isPaid)
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
        assertEquals(themeModel.colorScheme.numberColor, convert.colorScheme.numberColor)
        assertEquals(themeModel.colorScheme.operatorColor, convert.colorScheme.operatorColor)
        assertEquals(themeModel.colorScheme.keywordColor, convert.colorScheme.keywordColor)
        assertEquals(themeModel.colorScheme.typeColor, convert.colorScheme.typeColor)
        assertEquals(themeModel.colorScheme.langConstColor, convert.colorScheme.langConstColor)
        assertEquals(themeModel.colorScheme.methodColor, convert.colorScheme.methodColor)
        assertEquals(themeModel.colorScheme.stringColor, convert.colorScheme.stringColor)
        assertEquals(themeModel.colorScheme.commentColor, convert.colorScheme.commentColor)
    }

    /*@Test
    fun `convert ThemeModel to ThemeEntity`() {
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Light Team Software",
            description = "Default color scheme",
            isExternal = false,
            isPaid = false,
            colorScheme = ColorScheme(
                textColor = Color.parseColor("#FFFFFF"),
                backgroundColor = Color.parseColor("#303030"),
                gutterColor = Color.parseColor("#F0F0F0"),
                gutterDividerColor = Color.parseColor("#FFFFFF"),
                gutterCurrentLineNumberColor = Color.parseColor("#FEFEFE"),
                gutterTextColor = Color.parseColor("#FFFFFF"),
                selectedLineColor = Color.parseColor("#EEEEEE"),
                selectionColor = Color.parseColor("#FF3000"),
                suggestionQueryColor = Color.parseColor("#FF9000"),
                findResultBackgroundColor = Color.parseColor("#FEFEFE"),
                delimiterBackgroundColor = Color.parseColor("#FEFEFE"),
                numberColor = Color.parseColor("#FF3000"),
                operatorColor = Color.parseColor("#FF3000"),
                keywordColor = Color.parseColor("#FF3000"),
                typeColor = Color.parseColor("#FF3000"),
                langConstColor = Color.parseColor("#FF3000"),
                methodColor = Color.parseColor("#FF3000"),
                stringColor = Color.parseColor("#FF3000"),
                commentColor = Color.parseColor("#FF3000")
            )
        )
        val themeEntity = ThemeEntity(
            uuid = "0",
            name = "Test",
            author = "Light Team Software",
            description = "Default color scheme",
            isExternal = false,
            isPaid = false,
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
        assertEquals(themeEntity.isPaid, convert.isPaid)
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
        assertEquals(themeEntity.methodColor, convert.methodColor)
        assertEquals(themeEntity.stringColor, convert.stringColor)
        assertEquals(themeEntity.commentColor, convert.commentColor)
    }*/

    @Test
    fun `convert ExternalTheme to ThemeModel`() {
        val externalTheme = ExternalTheme(
                uuid = "0",
                name = "Test",
                author = "Light Team Software",
                description = "Default color scheme",
                isExternal = false,
                isPaid = false,
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
                    methodColor = "#FF3000",
                    stringColor = "#FF3000",
                    commentColor = "#FF3000"
                )
            )
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Light Team Software",
            description = "Default color scheme",
            isExternal = false,
            isPaid = false,
            colorScheme = ColorScheme(
                textColor = Color.parseColor("#FFFFFF"),
                backgroundColor = Color.parseColor("#303030"),
                gutterColor = Color.parseColor("#F0F0F0"),
                gutterDividerColor = Color.parseColor("#FFFFFF"),
                gutterCurrentLineNumberColor = Color.parseColor("#FEFEFE"),
                gutterTextColor = Color.parseColor("#FFFFFF"),
                selectedLineColor = Color.parseColor("#EEEEEE"),
                selectionColor = Color.parseColor("#FF3000"),
                suggestionQueryColor = Color.parseColor("#FF9000"),
                findResultBackgroundColor = Color.parseColor("#FEFEFE"),
                delimiterBackgroundColor = Color.parseColor("#FEFEFE"),
                numberColor = Color.parseColor("#FF3000"),
                operatorColor = Color.parseColor("#FF3000"),
                keywordColor = Color.parseColor("#FF3000"),
                typeColor = Color.parseColor("#FF3000"),
                langConstColor = Color.parseColor("#FF3000"),
                methodColor = Color.parseColor("#FF3000"),
                stringColor = Color.parseColor("#FF3000"),
                commentColor = Color.parseColor("#FF3000")
            )
        )
        val convert = ThemeConverter.toModel(externalTheme)

        assertEquals(themeModel.uuid, convert.uuid)
        assertEquals(themeModel.name, convert.name)
        assertEquals(themeModel.author, convert.author)
        assertEquals(themeModel.description, convert.description)
        assertEquals(themeModel.isExternal, convert.isExternal)
        assertEquals(themeModel.isPaid, convert.isPaid)
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
        assertEquals(themeModel.colorScheme.numberColor, convert.colorScheme.numberColor)
        assertEquals(themeModel.colorScheme.operatorColor, convert.colorScheme.operatorColor)
        assertEquals(themeModel.colorScheme.keywordColor, convert.colorScheme.keywordColor)
        assertEquals(themeModel.colorScheme.typeColor, convert.colorScheme.typeColor)
        assertEquals(themeModel.colorScheme.langConstColor, convert.colorScheme.langConstColor)
        assertEquals(themeModel.colorScheme.methodColor, convert.colorScheme.methodColor)
        assertEquals(themeModel.colorScheme.stringColor, convert.colorScheme.stringColor)
        assertEquals(themeModel.colorScheme.commentColor, convert.colorScheme.commentColor)
    }

    @Test
    fun `convert ThemeModel to SyntaxScheme`() {
        val themeModel = ThemeModel(
            uuid = "0",
            name = "Test",
            author = "Light Team Software",
            description = "Default color scheme",
            isExternal = false,
            isPaid = false,
            colorScheme = ColorScheme(
                textColor = Color.parseColor("#FFFFFF"),
                backgroundColor = Color.parseColor("#303030"),
                gutterColor = Color.parseColor("#F0F0F0"),
                gutterDividerColor = Color.parseColor("#FFFFFF"),
                gutterCurrentLineNumberColor = Color.parseColor("#FEFEFE"),
                gutterTextColor = Color.parseColor("#FFFFFF"),
                selectedLineColor = Color.parseColor("#EEEEEE"),
                selectionColor = Color.parseColor("#FF3000"),
                suggestionQueryColor = Color.parseColor("#FF9000"),
                findResultBackgroundColor = Color.parseColor("#FEFEFE"),
                delimiterBackgroundColor = Color.parseColor("#FEFEFE"),
                numberColor = Color.parseColor("#FF3000"),
                operatorColor = Color.parseColor("#FF3000"),
                keywordColor = Color.parseColor("#FF3000"),
                typeColor = Color.parseColor("#FF3000"),
                langConstColor = Color.parseColor("#FF3000"),
                methodColor = Color.parseColor("#FF3000"),
                stringColor = Color.parseColor("#FF3000"),
                commentColor = Color.parseColor("#FF3000")
            )
        )
        val syntaxScheme = SyntaxScheme(
            numberColor = Color.parseColor("#FF3000"),
            operatorColor = Color.parseColor("#FF3000"),
            keywordColor = Color.parseColor("#FF3000"),
            typeColor = Color.parseColor("#FF3000"),
            langConstColor = Color.parseColor("#FF3000"),
            methodColor = Color.parseColor("#FF3000"),
            stringColor = Color.parseColor("#FF3000"),
            commentColor = Color.parseColor("#FF3000")
        )
        val convert = ThemeConverter.toSyntaxScheme(themeModel)

        assertEquals(syntaxScheme.numberColor, convert.numberColor)
        assertEquals(syntaxScheme.operatorColor, convert.operatorColor)
        assertEquals(syntaxScheme.keywordColor, convert.keywordColor)
        assertEquals(syntaxScheme.typeColor, convert.typeColor)
        assertEquals(syntaxScheme.langConstColor, convert.langConstColor)
        assertEquals(syntaxScheme.methodColor, convert.methodColor)
        assertEquals(syntaxScheme.stringColor, convert.stringColor)
        assertEquals(syntaxScheme.commentColor, convert.commentColor)
    }
}