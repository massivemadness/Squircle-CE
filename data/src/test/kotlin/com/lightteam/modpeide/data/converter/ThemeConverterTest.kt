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
import com.lightteam.modpeide.data.feature.scheme.ColorScheme
import com.lightteam.modpeide.data.feature.scheme.Theme
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
            suggestionMatchColor = "#FF9000",
            searchBackgroundColor = "#FEFEFE",
            bracketBackgroundColor = "#FEFEFE",
            numberColor = "#FF3000",
            operatorColor = "#FF3000",
            bracketColor = "#FF3000",
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
                suggestionMatchColor = Color.parseColor("#FF9000"),
                searchBackgroundColor = Color.parseColor("#FEFEFE"),
                bracketBackgroundColor = Color.parseColor("#FEFEFE"),
                numberColor = Color.parseColor("#FF3000"),
                operatorColor = Color.parseColor("#FF3000"),
                bracketColor = Color.parseColor("#FF3000"),
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
        assertEquals(theme.colorScheme.suggestionMatchColor, convert.colorScheme.suggestionMatchColor)
        assertEquals(theme.colorScheme.searchBackgroundColor, convert.colorScheme.searchBackgroundColor)
        assertEquals(theme.colorScheme.bracketBackgroundColor, convert.colorScheme.bracketBackgroundColor)
        assertEquals(theme.colorScheme.numberColor, convert.colorScheme.numberColor)
        assertEquals(theme.colorScheme.operatorColor, convert.colorScheme.operatorColor)
        assertEquals(theme.colorScheme.bracketColor, convert.colorScheme.bracketColor)
        assertEquals(theme.colorScheme.keywordColor, convert.colorScheme.keywordColor)
        assertEquals(theme.colorScheme.typeColor, convert.colorScheme.typeColor)
        assertEquals(theme.colorScheme.langConstColor, convert.colorScheme.langConstColor)
        assertEquals(theme.colorScheme.methodColor, convert.colorScheme.methodColor)
        assertEquals(theme.colorScheme.stringColor, convert.colorScheme.stringColor)
        assertEquals(theme.colorScheme.commentColor, convert.colorScheme.commentColor)
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
                suggestionMatchColor = Color.parseColor("#FF9000"),
                searchBackgroundColor = Color.parseColor("#FEFEFE"),
                bracketBackgroundColor = Color.parseColor("#FEFEFE"),
                numberColor = Color.parseColor("#FF3000"),
                operatorColor = Color.parseColor("#FF3000"),
                bracketColor = Color.parseColor("#FF3000"),
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
            bracketColor = Color.parseColor("#FF3000"),
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
        assertEquals(syntaxScheme.bracketColor, convert.bracketColor)
        assertEquals(syntaxScheme.keywordColor, convert.keywordColor)
        assertEquals(syntaxScheme.typeColor, convert.typeColor)
        assertEquals(syntaxScheme.langConstColor, convert.langConstColor)
        assertEquals(syntaxScheme.methodColor, convert.methodColor)
        assertEquals(syntaxScheme.stringColor, convert.stringColor)
        assertEquals(syntaxScheme.commentColor, convert.commentColor)
    }
}