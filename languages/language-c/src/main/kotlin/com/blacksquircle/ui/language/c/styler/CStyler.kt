/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.language.c.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.c.lexer.CLexer
import com.blacksquircle.ui.language.c.lexer.CToken
import java.io.IOException
import java.io.StringReader

class CStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "CStyler"

        private var cStyler: CStyler? = null

        fun getInstance(): CStyler {
            return cStyler ?: CStyler().also {
                cStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = CLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    CToken.LONG_LITERAL,
                    CToken.INTEGER_LITERAL,
                    CToken.FLOAT_LITERAL,
                    CToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.TRIGRAPH,
                    CToken.EQ,
                    CToken.PLUS,
                    CToken.MINUS,
                    CToken.MULT,
                    CToken.DIV,
                    CToken.MOD,
                    CToken.TILDA,
                    CToken.LT,
                    CToken.GT,
                    CToken.LTLT,
                    CToken.GTGT,
                    CToken.EQEQ,
                    CToken.PLUSEQ,
                    CToken.MINUSEQ,
                    CToken.MULTEQ,
                    CToken.DIVEQ,
                    CToken.MODEQ,
                    CToken.ANDEQ,
                    CToken.OREQ,
                    CToken.XOREQ,
                    CToken.GTEQ,
                    CToken.LTEQ,
                    CToken.NOTEQ,
                    CToken.GTGTEQ,
                    CToken.LTLTEQ,
                    CToken.XOR,
                    CToken.AND,
                    CToken.ANDAND,
                    CToken.OR,
                    CToken.OROR,
                    CToken.QUEST,
                    CToken.COLON,
                    CToken.NOT,
                    CToken.PLUSPLUS,
                    CToken.MINUSMINUS,
                    CToken.LPAREN,
                    CToken.RPAREN,
                    CToken.LBRACE,
                    CToken.RBRACE,
                    CToken.LBRACK,
                    CToken.RBRACK -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.SEMICOLON,
                    CToken.COMMA,
                    CToken.DOT -> {
                        continue // skip
                    }
                    CToken.AUTO,
                    CToken.BREAK,
                    CToken.CASE,
                    CToken.CONST,
                    CToken.CONTINUE,
                    CToken.DEFAULT,
                    CToken.DO,
                    CToken.ELSE,
                    CToken.ENUM,
                    CToken.EXTERN,
                    CToken.FOR,
                    CToken.GOTO,
                    CToken.IF,
                    CToken.REGISTER,
                    CToken.SIZEOF,
                    CToken.STATIC,
                    CToken.STRUCT,
                    CToken.SWITCH,
                    CToken.TYPEDEF,
                    CToken.UNION,
                    CToken.VOLATILE,
                    CToken.WHILE,
                    CToken.RETURN -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.FUNCTION -> {
                        val styleSpan = StyleSpan(syntaxScheme.methodColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.BOOL,
                    CToken.CHAR,
                    CToken.DIV_T,
                    CToken.DOUBLE,
                    CToken.FLOAT,
                    CToken.INT,
                    CToken.LDIV_T,
                    CToken.LONG,
                    CToken.SHORT,
                    CToken.SIGNED,
                    CToken.SIZE_T,
                    CToken.UNSIGNED,
                    CToken.VOID,
                    CToken.WCHAR_T -> {
                        val styleSpan = StyleSpan(syntaxScheme.typeColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.TRUE,
                    CToken.FALSE,
                    CToken.NULL -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.__DATE__,
                    CToken.__TIME__,
                    CToken.__FILE__,
                    CToken.__LINE__,
                    CToken.__STDC__,
                    CToken.PREPROCESSOR -> {
                        val styleSpan = StyleSpan(syntaxScheme.preprocessorColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.DOUBLE_QUOTED_STRING,
                    CToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.LINE_COMMENT,
                    CToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CToken.IDENTIFIER,
                    CToken.WHITESPACE,
                    CToken.BAD_CHARACTER -> {
                        continue
                    }
                    CToken.EOF -> {
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

    override fun enqueue(sourceCode: String, syntaxScheme: SyntaxScheme, stylingResult: StylingResult) {
        task?.cancelTask()
        task = StylingTask(
            doAsync = { execute(sourceCode, syntaxScheme) },
            onSuccess = stylingResult
        )
        task?.executeTask()
    }

    override fun cancel() {
        task?.cancelTask()
        task = null
    }
}