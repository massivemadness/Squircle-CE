/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.editorkit.converter

import androidx.core.graphics.toColorInt
import com.brackeys.ui.editorkit.feature.colorscheme.ColorScheme
import com.brackeys.ui.language.base.model.SyntaxScheme
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorSchemeConverterTest {

    @Test
    fun `convert ColorScheme to SyntaxScheme`() {
        val colorScheme = ColorScheme(
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
            numberColor = "#FF3000".toColorInt(),
            operatorColor = "#FF3000".toColorInt(),
            keywordColor = "#FF3000".toColorInt(),
            typeColor = "#FF3000".toColorInt(),
            langConstColor = "#FF3000".toColorInt(),
            methodColor = "#FF3000".toColorInt(),
            stringColor = "#FF3000".toColorInt(),
            commentColor = "#FF3000".toColorInt()
        )
        val syntaxScheme = SyntaxScheme(
            numberColor = "#FF3000".toColorInt(),
            operatorColor = "#FF3000".toColorInt(),
            keywordColor = "#FF3000".toColorInt(),
            typeColor = "#FF3000".toColorInt(),
            langConstColor = "#FF3000".toColorInt(),
            methodColor = "#FF3000".toColorInt(),
            stringColor = "#FF3000".toColorInt(),
            commentColor = "#FF3000".toColorInt()
        )
        val convert = ColorSchemeConverter.toSyntaxScheme(colorScheme)

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