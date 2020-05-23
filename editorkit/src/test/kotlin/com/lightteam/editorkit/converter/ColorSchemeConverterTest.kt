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

package com.lightteam.editorkit.converter

import android.graphics.Color
import com.lightteam.editorkit.feature.colorscheme.ColorScheme
import com.lightteam.language.scheme.SyntaxScheme
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class ColorSchemeConverterTest {

    @Test
    fun `convert ColorScheme to SyntaxScheme`() {
        val colorScheme = ColorScheme(
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