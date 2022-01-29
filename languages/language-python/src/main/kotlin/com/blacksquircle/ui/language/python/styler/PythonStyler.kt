/*
 * Copyright 2022 Squircle IDE contributors.
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

package com.blacksquircle.ui.language.python.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.python.lexer.PythonLexer
import com.blacksquircle.ui.language.python.lexer.PythonToken
import java.io.IOException
import java.io.StringReader

class PythonStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "PythonStyler"

        private var pythonStyler: PythonStyler? = null

        fun getInstance(): PythonStyler {
            return pythonStyler ?: PythonStyler().also {
                pythonStyler = it
            }
        }
    }

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = PythonLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    PythonToken.LONG_LITERAL,
                    PythonToken.INTEGER_LITERAL,
                    PythonToken.FLOAT_LITERAL,
                    PythonToken.IMAGINARY_LITERAL -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.PLUSEQ,
                    PythonToken.MINUSEQ,
                    PythonToken.EXPEQ,
                    PythonToken.MULTEQ,
                    PythonToken.ATEQ,
                    PythonToken.FLOORDIVEQ,
                    PythonToken.DIVEQ,
                    PythonToken.MODEQ,
                    PythonToken.ANDEQ,
                    PythonToken.OREQ,
                    PythonToken.XOREQ,
                    PythonToken.GTGTEQ,
                    PythonToken.LTLTEQ,
                    PythonToken.LTLT,
                    PythonToken.GTGT,
                    PythonToken.EXP,
                    PythonToken.FLOORDIV,
                    PythonToken.LTEQ,
                    PythonToken.GTEQ,
                    PythonToken.EQEQ,
                    PythonToken.NOTEQ,
                    PythonToken.NOTEQ_OLD,
                    PythonToken.RARROW,
                    PythonToken.PLUS,
                    PythonToken.MINUS,
                    PythonToken.MULT,
                    PythonToken.DIV,
                    PythonToken.MOD,
                    PythonToken.AND,
                    PythonToken.OR,
                    PythonToken.XOR,
                    PythonToken.TILDE,
                    PythonToken.LT,
                    PythonToken.GT,
                    PythonToken.AT,
                    PythonToken.COLON,
                    PythonToken.TICK,
                    PythonToken.EQ,
                    PythonToken.COLONEQ,
                    PythonToken.LPAREN,
                    PythonToken.RPAREN,
                    PythonToken.LBRACE,
                    PythonToken.RBRACE,
                    PythonToken.LBRACK,
                    PythonToken.RBRACK -> {
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.SEMICOLON,
                    PythonToken.COMMA,
                    PythonToken.DOT -> {
                        continue // skip
                    }
                    PythonToken.AND_KEYWORD,
                    PythonToken.AS,
                    PythonToken.ASSERT,
                    PythonToken.BREAK,
                    PythonToken.CLASS,
                    PythonToken.CONTINUE,
                    PythonToken.DEF,
                    PythonToken.DEL,
                    PythonToken.ELIF,
                    PythonToken.ELSE,
                    PythonToken.EXCEPT,
                    PythonToken.EXEC,
                    PythonToken.FINALLY,
                    PythonToken.FOR,
                    PythonToken.FROM,
                    PythonToken.GLOBAL,
                    PythonToken.IF,
                    PythonToken.IMPORT,
                    PythonToken.IN,
                    PythonToken.IS,
                    PythonToken.LAMBDA,
                    PythonToken.NOT_KEYWORD,
                    PythonToken.OR_KEYWORD,
                    PythonToken.PASS,
                    PythonToken.PRINT,
                    PythonToken.RAISE,
                    PythonToken.RETURN,
                    PythonToken.TRY,
                    PythonToken.WHILE,
                    PythonToken.YIELD -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.CHAR,
                    PythonToken.DOUBLE,
                    PythonToken.FLOAT,
                    PythonToken.INT,
                    PythonToken.LONG,
                    PythonToken.SHORT,
                    PythonToken.SIGNED,
                    PythonToken.UNSIGNED,
                    PythonToken.VOID -> {
                        val styleSpan = StyleSpan(scheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.METHOD -> {
                        val styleSpan = StyleSpan(scheme.methodColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.TRUE,
                    PythonToken.FALSE,
                    PythonToken.NONE -> {
                        val styleSpan = StyleSpan(scheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.DECORATOR -> {
                        val styleSpan = StyleSpan(scheme.preprocessorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.DOUBLE_QUOTED_STRING,
                    PythonToken.SINGLE_QUOTED_STRING,
                    PythonToken.LONG_DOUBLE_QUOTED_STRING,
                    PythonToken.LONG_SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(scheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.LINE_COMMENT -> {
                        val styleSpan = StyleSpan(scheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PythonToken.IDENTIFIER,
                    PythonToken.WHITESPACE,
                    PythonToken.BAD_CHARACTER -> {
                        continue
                    }
                    PythonToken.EOF -> {
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