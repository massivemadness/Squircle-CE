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
import com.lightteam.language.scheme.SyntaxScheme
import com.lightteam.modpeide.data.feature.scheme.external.ExternalScheme
import com.lightteam.modpeide.data.feature.scheme.external.ExternalTheme
import com.lightteam.modpeide.data.feature.scheme.internal.ColorScheme
import com.lightteam.modpeide.data.feature.scheme.internal.Theme
import com.lightteam.modpeide.database.entity.theme.ThemeEntity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class ThemeConverterTest {

    @Test
    fun `convert ThemeEntity to Theme`() {
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
        val theme = Theme(
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

        assertEquals(theme.uuid, convert.uuid)
        assertEquals(theme.name, convert.name)
        assertEquals(theme.author, convert.author)
        assertEquals(theme.description, convert.description)
        assertEquals(theme.isExternal, convert.isExternal)
        assertEquals(theme.isPaid, convert.isPaid)
        assertEquals(theme.colorScheme.textColor, convert.colorScheme.textColor)
        assertEquals(theme.colorScheme.backgroundColor, convert.colorScheme.backgroundColor)
        assertEquals(theme.colorScheme.gutterColor, convert.colorScheme.gutterColor)
        assertEquals(theme.colorScheme.gutterDividerColor, convert.colorScheme.gutterDividerColor)
        assertEquals(theme.colorScheme.gutterCurrentLineNumberColor, convert.colorScheme.gutterCurrentLineNumberColor)
        assertEquals(theme.colorScheme.gutterTextColor, convert.colorScheme.gutterTextColor)
        assertEquals(theme.colorScheme.selectedLineColor, convert.colorScheme.selectedLineColor)
        assertEquals(theme.colorScheme.selectionColor, convert.colorScheme.selectionColor)
        assertEquals(theme.colorScheme.suggestionQueryColor, convert.colorScheme.suggestionQueryColor)
        assertEquals(theme.colorScheme.findResultBackgroundColor, convert.colorScheme.findResultBackgroundColor)
        assertEquals(theme.colorScheme.delimiterBackgroundColor, convert.colorScheme.delimiterBackgroundColor)
        assertEquals(theme.colorScheme.numberColor, convert.colorScheme.numberColor)
        assertEquals(theme.colorScheme.operatorColor, convert.colorScheme.operatorColor)
        assertEquals(theme.colorScheme.keywordColor, convert.colorScheme.keywordColor)
        assertEquals(theme.colorScheme.typeColor, convert.colorScheme.typeColor)
        assertEquals(theme.colorScheme.langConstColor, convert.colorScheme.langConstColor)
        assertEquals(theme.colorScheme.methodColor, convert.colorScheme.methodColor)
        assertEquals(theme.colorScheme.stringColor, convert.colorScheme.stringColor)
        assertEquals(theme.colorScheme.commentColor, convert.colorScheme.commentColor)
    }

    /*@Test
    fun `convert Theme to ThemeEntity`() {
        val theme = Theme(
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
        val convert = ThemeConverter.toEntity(theme)

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
    fun `convert ExternalTheme to ThemeEntity`() {
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
        val convert = ThemeConverter.toEntity(externalTheme)

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
    }

    @Test
    fun `convert Theme to SyntaxScheme`() {
        val theme = Theme(
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
        val convert = ThemeConverter.toSyntaxScheme(theme)

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