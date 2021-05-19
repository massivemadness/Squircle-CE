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

package com.blacksquircle.ui.language.typescript.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.typescript.lexer.TypeScriptLexer
import com.blacksquircle.ui.language.typescript.lexer.TypeScriptToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class TypeScriptStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "TypeScriptStyler"

        private val METHOD = Pattern.compile("(?<=(function)) (\\w+)")

        private var typeScriptStyler: TypeScriptStyler? = null

        fun getInstance(): TypeScriptStyler {
            return typeScriptStyler ?: TypeScriptStyler().also {
                typeScriptStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = TypeScriptLexer(sourceReader)

        // FIXME flex doesn't support positive lookbehind
        val matcher = METHOD.matcher(sourceCode)
        matcher.region(0, sourceCode.length)
        while (matcher.find()) {
            val styleSpan = StyleSpan(syntaxScheme.methodColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }

        while (true) {
            try {
                when (lexer.advance()) {
                    TypeScriptToken.LONG_LITERAL,
                    TypeScriptToken.INTEGER_LITERAL,
                    TypeScriptToken.FLOAT_LITERAL,
                    TypeScriptToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.EQEQ,
                    TypeScriptToken.NOTEQ,
                    TypeScriptToken.OROR,
                    TypeScriptToken.PLUSPLUS,
                    TypeScriptToken.MINUSMINUS,
                    TypeScriptToken.LT,
                    TypeScriptToken.LTLT,
                    TypeScriptToken.LTEQ,
                    TypeScriptToken.LTLTEQ,
                    TypeScriptToken.GT,
                    TypeScriptToken.GTGT,
                    TypeScriptToken.GTGTGT,
                    TypeScriptToken.GTEQ,
                    TypeScriptToken.GTGTEQ,
                    TypeScriptToken.GTGTGTEQ,
                    TypeScriptToken.AND,
                    TypeScriptToken.ANDAND,
                    TypeScriptToken.PLUSEQ,
                    TypeScriptToken.MINUSEQ,
                    TypeScriptToken.MULTEQ,
                    TypeScriptToken.DIVEQ,
                    TypeScriptToken.ANDEQ,
                    TypeScriptToken.OREQ,
                    TypeScriptToken.XOREQ,
                    TypeScriptToken.MODEQ,
                    TypeScriptToken.LPAREN,
                    TypeScriptToken.RPAREN,
                    TypeScriptToken.LBRACE,
                    TypeScriptToken.RBRACE,
                    TypeScriptToken.LBRACK,
                    TypeScriptToken.RBRACK,
                    TypeScriptToken.EQ,
                    TypeScriptToken.NOT,
                    TypeScriptToken.TILDE,
                    TypeScriptToken.QUEST,
                    TypeScriptToken.COLON,
                    TypeScriptToken.PLUS,
                    TypeScriptToken.MINUS,
                    TypeScriptToken.MULT,
                    TypeScriptToken.DIV,
                    TypeScriptToken.OR,
                    TypeScriptToken.XOR,
                    TypeScriptToken.MOD,
                    TypeScriptToken.ARROW,
                    TypeScriptToken.ELLIPSIS -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.SEMICOLON,
                    TypeScriptToken.COMMA,
                    TypeScriptToken.DOT -> {
                        continue // skip
                    }
                    TypeScriptToken.FUNCTION,
                    TypeScriptToken.PROTOTYPE,
                    TypeScriptToken.DEBUGGER,
                    TypeScriptToken.SUPER,
                    TypeScriptToken.ANY,
                    TypeScriptToken.THIS,
                    TypeScriptToken.ASYNC,
                    TypeScriptToken.AWAIT,
                    TypeScriptToken.EXPORT,
                    TypeScriptToken.FROM,
                    TypeScriptToken.EXTENDS,
                    TypeScriptToken.DECLARE,
                    TypeScriptToken.FINAL,
                    TypeScriptToken.IMPLEMENTS,
                    TypeScriptToken.NATIVE,
                    TypeScriptToken.PRIVATE,
                    TypeScriptToken.PROTECTED,
                    TypeScriptToken.PUBLIC,
                    TypeScriptToken.STATIC,
                    TypeScriptToken.SYNCHRONIZED,
                    TypeScriptToken.CONSTRUCTOR,
                    TypeScriptToken.THROWS,
                    TypeScriptToken.TRANSIENT,
                    TypeScriptToken.VOLATILE,
                    TypeScriptToken.YIELD,
                    TypeScriptToken.DELETE,
                    TypeScriptToken.NEW,
                    TypeScriptToken.IN,
                    TypeScriptToken.INSTANCEOF,
                    TypeScriptToken.TYPEOF,
                    TypeScriptToken.OF,
                    TypeScriptToken.KEYOF,
                    TypeScriptToken.TYPE,
                    TypeScriptToken.WITH,
                    TypeScriptToken.AS,
                    TypeScriptToken.IS,
                    TypeScriptToken.BREAK,
                    TypeScriptToken.CASE,
                    TypeScriptToken.CATCH,
                    TypeScriptToken.CONTINUE,
                    TypeScriptToken.DEFAULT,
                    TypeScriptToken.DO,
                    TypeScriptToken.ELSE,
                    TypeScriptToken.FINALLY,
                    TypeScriptToken.FOR,
                    TypeScriptToken.GOTO,
                    TypeScriptToken.IF,
                    TypeScriptToken.IMPORT,
                    TypeScriptToken.PACKAGE,
                    TypeScriptToken.READONLY,
                    TypeScriptToken.RETURN,
                    TypeScriptToken.SWITCH,
                    TypeScriptToken.THROW,
                    TypeScriptToken.TRY,
                    TypeScriptToken.WHILE,
                    TypeScriptToken.CONST,
                    TypeScriptToken.VAR,
                    TypeScriptToken.LET -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.CLASS,
                    TypeScriptToken.INTERFACE,
                    TypeScriptToken.ENUM,
                    TypeScriptToken.MODULE,
                    TypeScriptToken.UNKNOWN,
                    TypeScriptToken.OBJECT,
                    TypeScriptToken.BOOLEAN,
                    TypeScriptToken.STRING,
                    TypeScriptToken.NUMBER,
                    TypeScriptToken.BIGINT,
                    TypeScriptToken.VOID -> {
                        val styleSpan = StyleSpan(syntaxScheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.TRUE,
                    TypeScriptToken.FALSE,
                    TypeScriptToken.NULL,
                    TypeScriptToken.NAN,
                    TypeScriptToken.UNDEFINED -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.REQUIRE -> {
                        val styleSpan = StyleSpan(syntaxScheme.methodColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.DOUBLE_QUOTED_STRING,
                    TypeScriptToken.SINGLE_QUOTED_STRING,
                    TypeScriptToken.SINGLE_BACKTICK_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.LINE_COMMENT,
                    TypeScriptToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    TypeScriptToken.IDENTIFIER,
                    TypeScriptToken.WHITESPACE,
                    TypeScriptToken.BAD_CHARACTER -> {
                        continue
                    }
                    TypeScriptToken.EOF -> {
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