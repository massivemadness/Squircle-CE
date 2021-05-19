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

package com.blacksquircle.ui.language.cpp.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.cpp.lexer.CppLexer
import com.blacksquircle.ui.language.cpp.lexer.CppToken
import java.io.IOException
import java.io.StringReader

class CppStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "CppStyler"

        private var cppStyler: CppStyler? = null

        fun getInstance(): CppStyler {
            return cppStyler ?: CppStyler().also {
                cppStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = CppLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    CppToken.LONG_LITERAL,
                    CppToken.INTEGER_LITERAL,
                    CppToken.FLOAT_LITERAL,
                    CppToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.TRIGRAPH,
                    CppToken.EQ,
                    CppToken.PLUS,
                    CppToken.MINUS,
                    CppToken.MULT,
                    CppToken.DIV,
                    CppToken.MOD,
                    CppToken.TILDA,
                    CppToken.LT,
                    CppToken.GT,
                    CppToken.LTLT,
                    CppToken.GTGT,
                    CppToken.EQEQ,
                    CppToken.PLUSEQ,
                    CppToken.MINUSEQ,
                    CppToken.MULTEQ,
                    CppToken.DIVEQ,
                    CppToken.MODEQ,
                    CppToken.ANDEQ,
                    CppToken.OREQ,
                    CppToken.XOREQ,
                    CppToken.GTEQ,
                    CppToken.LTEQ,
                    CppToken.NOTEQ,
                    CppToken.GTGTEQ,
                    CppToken.LTLTEQ,
                    CppToken.XOR,
                    CppToken.AND,
                    CppToken.ANDAND,
                    CppToken.OR,
                    CppToken.OROR,
                    CppToken.QUEST,
                    CppToken.COLON,
                    CppToken.NOT,
                    CppToken.PLUSPLUS,
                    CppToken.MINUSMINUS,
                    CppToken.LPAREN,
                    CppToken.RPAREN,
                    CppToken.LBRACE,
                    CppToken.RBRACE,
                    CppToken.LBRACK,
                    CppToken.RBRACK -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.SEMICOLON,
                    CppToken.COMMA,
                    CppToken.DOT -> {
                        continue // skip
                    }
                    CppToken.AUTO,
                    CppToken.BREAK,
                    CppToken.CASE,
                    CppToken.CATCH,
                    CppToken.CLASS,
                    CppToken.CONST,
                    CppToken.CONST_CAST,
                    CppToken.CONTINUE,
                    CppToken.DEFAULT,
                    CppToken.DELETE,
                    CppToken.DO,
                    CppToken.DYNAMIC_CAST,
                    CppToken.ELSE,
                    CppToken.ENUM,
                    CppToken.EXPLICIT,
                    CppToken.EXTERN,
                    CppToken.FOR,
                    CppToken.FRIEND,
                    CppToken.GOTO,
                    CppToken.IF,
                    CppToken.INLINE,
                    CppToken.MUTABLE,
                    CppToken.NAMESPACE,
                    CppToken.NEW,
                    CppToken.OPERATOR,
                    CppToken.PRIVATE,
                    CppToken.PROTECTED,
                    CppToken.PUBLIC,
                    CppToken.REGISTER,
                    CppToken.REINTERPRET_CAST,
                    CppToken.SIZEOF,
                    CppToken.STATIC,
                    CppToken.STATIC_CAST,
                    CppToken.STRUCT,
                    CppToken.SWITCH,
                    CppToken.TEMPLATE,
                    CppToken.THIS,
                    CppToken.THROW,
                    CppToken.TRY,
                    CppToken.TYPEDEF,
                    CppToken.TYPEID,
                    CppToken.TYPENAME,
                    CppToken.UNION,
                    CppToken.USING,
                    CppToken.VIRTUAL,
                    CppToken.VOLATILE,
                    CppToken.WHILE,
                    CppToken.RETURN -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.FUNCTION -> {
                        val styleSpan = StyleSpan(syntaxScheme.methodColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.BOOL,
                    CppToken.CHAR,
                    CppToken.DIV_T,
                    CppToken.DOUBLE,
                    CppToken.FLOAT,
                    CppToken.INT,
                    CppToken.LDIV_T,
                    CppToken.LONG,
                    CppToken.SHORT,
                    CppToken.SIGNED,
                    CppToken.SIZE_T,
                    CppToken.UNSIGNED,
                    CppToken.VOID,
                    CppToken.WCHAR_T -> {
                        val styleSpan = StyleSpan(syntaxScheme.typeColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.TRUE,
                    CppToken.FALSE,
                    CppToken.NULL -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.__DATE__,
                    CppToken.__TIME__,
                    CppToken.__FILE__,
                    CppToken.__LINE__,
                    CppToken.__STDC__,
                    CppToken.PREPROCESSOR -> {
                        val styleSpan = StyleSpan(syntaxScheme.preprocessorColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.DOUBLE_QUOTED_STRING,
                    CppToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.LINE_COMMENT,
                    CppToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan =
                            SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    CppToken.IDENTIFIER,
                    CppToken.WHITESPACE,
                    CppToken.BAD_CHARACTER -> {
                        continue
                    }
                    CppToken.EOF -> {
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