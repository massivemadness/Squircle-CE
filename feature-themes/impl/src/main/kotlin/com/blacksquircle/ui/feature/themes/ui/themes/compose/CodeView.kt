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

import android.graphics.Typeface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.feature.themes.domain.model.ColorScheme
import com.blacksquircle.ui.feature.themes.domain.model.EditorTheme

@Composable
internal fun CodeView(
    colorScheme: ColorScheme,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(colorScheme) {
        parseStyledText(JAVASCRIPT_SAMPLE, colorScheme)
    }
    Text(
        text = annotatedString,
        color = Color(colorScheme.textColor),
        style = textStyle,
        modifier = modifier
    )
}

@Preview
@Composable
private fun CodeViewPreview() {
    PreviewBackground {
        CodeView(
            colorScheme = EditorTheme.DARCULA,
            textStyle = TextStyle(
                fontFamily = FontFamily(Typeface.MONOSPACE),
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp
            ),
        )
    }
}

/**
 * It would be an "overkill" to use sora editor for a theme preview.
 * So, this is an approximation of how the theme would look like.
 */
private val JAVASCRIPT_SAMPLE = """
    <keyword>function</keyword> <function>makeIterator</function>(array) {
      <keyword>var</keyword> index <operator>=</operator> <number>0</number>;
      <keyword>return</keyword> {
        <variable>next</variable>: <keyword>function</keyword>() {
          <keyword>return</keyword> index <operator><</operator> array.length
            <operator>?</operator> { <variable>value</variable>: array[index<operator>++</operator>], <variable>done</variable>: <keyword>false</keyword> }
            <operator>:</operator> { <variable>done</variable>: <keyword>true</keyword> }
        }
      };
    }

    <keyword>var</keyword> it = <function>makeIterator</function>([<string>"simple"</string>, <string>"iterator"</string>]);

    console.<function>log</function>(it.<function>next</function>()); <comment>// done: false</comment>
    console.<function>log</function>(it.<function>next</function>()); <comment>// done: false</comment>
    console.<function>log</function>(it.<function>next</function>()); <comment>// done: true</comment>
""".trimIndent()

@Suppress("SameParameterValue")
private fun parseStyledText(input: String, colorScheme: ColorScheme): AnnotatedString {
    return buildAnnotatedString {
        val regex = "<(\\w+)>(.*?)</\\1>".toRegex()
        var lastIndex = 0

        regex.findAll(input).forEach { match ->
            val tag = match.groups[1]?.value ?: ""
            val content = match.groups[2]?.value ?: ""
            val startIndex = match.range.first
            val endIndex = match.range.last + 1

            append(input.substring(lastIndex, startIndex))

            withStyle(
                style = when (tag) {
                    "keyword" -> SpanStyle(color = Color(colorScheme.keywordColor))
                    "function" -> SpanStyle(color = Color(colorScheme.methodColor))
                    "operator" -> SpanStyle(color = Color(colorScheme.operatorColor))
                    "number" -> SpanStyle(color = Color(colorScheme.numberColor))
                    "variable" -> SpanStyle(color = Color(colorScheme.variableColor))
                    "string" -> SpanStyle(color = Color(colorScheme.stringColor))
                    "comment" -> SpanStyle(color = Color(colorScheme.commentColor))
                    else -> SpanStyle()
                }
            ) {
                append(content)
            }

            lastIndex = endIndex
        }

        append(input.substring(lastIndex))
    }
}