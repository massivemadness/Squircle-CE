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

package com.blacksquircle.ui.feature.themes.ui.themes.compose

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.util.fastForEach
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType

@Composable
internal fun CodeView(
    text: String,
    language: Language,
    colorScheme: ColorScheme,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(text, language, colorScheme) {
        val textStructure = TextStructure(text)
        val syntaxResults = language.getStyler().execute(textStructure)
        syntaxHighlight(
            text = textStructure.text,
            colorScheme = colorScheme,
            syntaxResults = syntaxResults,
        )
    }
    Text(
        text = annotatedString,
        color = Color(colorScheme.textColor),
        style = textStyle,
        modifier = modifier
    )
}

private fun syntaxHighlight(
    text: CharSequence,
    colorScheme: ColorScheme,
    syntaxResults: List<SyntaxHighlightResult>
) = buildAnnotatedString {
    append(text)
    syntaxResults.fastForEach { result ->
        val colorInt = when (result.tokenType) {
            TokenType.NUMBER -> colorScheme.numberColor
            TokenType.OPERATOR -> colorScheme.operatorColor
            TokenType.KEYWORD -> colorScheme.keywordColor
            TokenType.TYPE -> colorScheme.typeColor
            TokenType.LANG_CONST -> colorScheme.langConstColor
            TokenType.PREPROCESSOR -> colorScheme.preprocessorColor
            TokenType.VARIABLE -> colorScheme.variableColor
            TokenType.METHOD -> colorScheme.methodColor
            TokenType.STRING -> colorScheme.stringColor
            TokenType.COMMENT -> colorScheme.commentColor
            TokenType.TAG -> colorScheme.tagColor
            TokenType.TAG_NAME -> colorScheme.tagNameColor
            TokenType.ATTR_NAME -> colorScheme.attrNameColor
            TokenType.ATTR_VALUE -> colorScheme.attrValueColor
            TokenType.ENTITY_REF -> colorScheme.entityRefColor
        }
        val spanStyle = SpanStyle(color = Color(colorInt))
        addStyle(spanStyle, result.start, result.end)
    }
}