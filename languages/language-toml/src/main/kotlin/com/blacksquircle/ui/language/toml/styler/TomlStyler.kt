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

package com.blacksquircle.ui.language.toml.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.toml.lexer.TomlLexer
import com.blacksquircle.ui.language.toml.lexer.TomlToken
import java.io.IOException
import java.io.StringReader

class TomlStyler : LanguageStyler {

    companion object {

        private const val TAG = "TomlStyler"

        private var tomlStyler: TomlStyler? = null

        fun getInstance(): TomlStyler {
            return tomlStyler ?: TomlStyler().also {
                tomlStyler = it
            }
        }
    }

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = TomlLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    TomlToken.BARE_KEY -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TomlToken.BARE_KEY_OR_DATE,
                    TomlToken.BARE_KEY_OR_NUMBER,
                    TomlToken.DATE_TIME,
                    TomlToken.NUMBER -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TomlToken.COMMENT -> {
                        val styleSpan = StyleSpan(scheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TomlToken.BOOLEAN -> {
                        val styleSpan = StyleSpan(scheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TomlToken.BASIC_STRING,
                    TomlToken.LITERAL_STRING,
                    TomlToken.MULTILINE_BASIC_STRING,
                    TomlToken.MULTILINE_LITERAL_STRING -> {
                        val styleSpan = StyleSpan(scheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TomlToken.EQ,
                    TomlToken.LBRACE,
                    TomlToken.RBRACE,
                    TomlToken.LBRACK,
                    TomlToken.RBRACK -> {
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TomlToken.COMMA,
                    TomlToken.DOT,
                    TomlToken.IDENTIFIER,
                    TomlToken.WHITESPACE,
                    TomlToken.BAD_CHARACTER -> {
                        continue
                    }
                    TomlToken.EOF -> {
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