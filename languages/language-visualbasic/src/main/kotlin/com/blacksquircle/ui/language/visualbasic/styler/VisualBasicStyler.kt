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

package com.blacksquircle.ui.language.visualbasic.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.visualbasic.lexer.VisualBasicLexer
import com.blacksquircle.ui.language.visualbasic.lexer.VisualBasicToken
import java.io.IOException
import java.io.StringReader

class VisualBasicStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "VisualBasicStyler"

        private var visualBasicStyler: VisualBasicStyler? = null

        fun getInstance(): VisualBasicStyler {
            return visualBasicStyler ?: VisualBasicStyler().also {
                visualBasicStyler = it
            }
        }
    }

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = VisualBasicLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    VisualBasicToken.LONG_LITERAL,
                    VisualBasicToken.INTEGER_LITERAL,
                    VisualBasicToken.FLOAT_LITERAL,
                    VisualBasicToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    VisualBasicToken.AND,
                    VisualBasicToken.ANDEQ,
                    VisualBasicToken.MULT,
                    VisualBasicToken.MULTEQ,
                    VisualBasicToken.PLUS,
                    VisualBasicToken.PLUSEQ,
                    VisualBasicToken.EQ,
                    VisualBasicToken.MINUS,
                    VisualBasicToken.MINUSEQ,
                    VisualBasicToken.LT,
                    VisualBasicToken.LTLT,
                    VisualBasicToken.LTLTEQ,
                    VisualBasicToken.GT,
                    VisualBasicToken.GTGT,
                    VisualBasicToken.GTGTEQ,
                    VisualBasicToken.DIV,
                    VisualBasicToken.DIVEQ,
                    VisualBasicToken.BACKSLASH,
                    VisualBasicToken.XOR,
                    VisualBasicToken.XOREQ,
                    VisualBasicToken.LPAREN,
                    VisualBasicToken.RPAREN,
                    VisualBasicToken.LBRACE,
                    VisualBasicToken.RBRACE,
                    VisualBasicToken.LBRACK,
                    VisualBasicToken.RBRACK -> {
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    VisualBasicToken.SEMICOLON,
                    VisualBasicToken.COMMA,
                    VisualBasicToken.DOT -> {
                        continue // skip
                    }
                    VisualBasicToken.KEYWORD -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    VisualBasicToken.BOOLEAN,
                    VisualBasicToken.BYTE,
                    VisualBasicToken.CHAR,
                    VisualBasicToken.DATE,
                    VisualBasicToken.DECIMAL,
                    VisualBasicToken.DOUBLE,
                    VisualBasicToken.INTEGER,
                    VisualBasicToken.LONG,
                    VisualBasicToken.OBJECT,
                    VisualBasicToken.SBYTE,
                    VisualBasicToken.SHORT,
                    VisualBasicToken.SINGLE,
                    VisualBasicToken.STRING,
                    VisualBasicToken.UINTEGER,
                    VisualBasicToken.ULONG,
                    VisualBasicToken.USHORT -> {
                        val styleSpan = StyleSpan(scheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    VisualBasicToken.TRUE,
                    VisualBasicToken.FALSE -> {
                        val styleSpan = StyleSpan(scheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    VisualBasicToken.DOUBLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(scheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    VisualBasicToken.LINE_COMMENT -> {
                        val styleSpan = StyleSpan(scheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    VisualBasicToken.IDENTIFIER,
                    VisualBasicToken.WHITESPACE,
                    VisualBasicToken.BAD_CHARACTER -> {
                        continue
                    }
                    VisualBasicToken.EOF -> {
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