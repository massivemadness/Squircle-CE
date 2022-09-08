/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.language.yaml.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.yaml.lexer.YamlLexer
import com.blacksquircle.ui.language.yaml.lexer.YamlToken
import java.io.IOException
import java.io.StringReader

class YamlStyler : LanguageStyler {

    companion object {

        private const val TAG = "YamlStyler"

        private var yamlStyler: YamlStyler? = null

        fun getInstance(): YamlStyler {
            return yamlStyler ?: YamlStyler().also {
                yamlStyler = it
            }
        }
    }

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = YamlLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    YamlToken.LBRACE,
                    YamlToken.RBRACE,
                    YamlToken.LBRACKET,
                    YamlToken.RBRACKET,
                    YamlToken.COMMA,
                    YamlToken.COLON,
                    YamlToken.AMPERSAND,
                    YamlToken.STAR,
                    YamlToken.QUESTION -> {
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    YamlToken.SCALAR_KEY -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    YamlToken.SCALAR_STRING,
                    YamlToken.SCALAR_DSTRING -> {
                        val styleSpan = StyleSpan(scheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    YamlToken.COMMENT -> {
                        val styleSpan = StyleSpan(scheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    YamlToken.SEQUENCE_MARKER,
                    YamlToken.DOCUMENT_MARKER,
                    YamlToken.DOCUMENT_END -> {
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    YamlToken.SCALAR_LIST,
                    YamlToken.SCALAR_TEXT,
                    YamlToken.SCALAR_EOL,
                    YamlToken.TAG,
                    YamlToken.TEXT,
                    YamlToken.ANCHOR,
                    YamlToken.ALIAS,
                    YamlToken.INDENT,
                    YamlToken.WHITESPACE,
                    YamlToken.EOL -> {
                        continue
                    }
                    YamlToken.EOF -> {
                        break
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message, e)
                break
            }
        }
        return syntaxHighlightSpans
    }
}