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

package com.blacksquircle.ui.language.groovy.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.groovy.lexer.GroovyLexer
import com.blacksquircle.ui.language.groovy.lexer.GroovyToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class GroovyStyler : LanguageStyler {

    companion object {

        private const val TAG = "GroovyStyler"

        // TODO support different return types
        private val METHOD = Pattern.compile("(?<=(void)) (\\w+)")

        private var groovyStyler: GroovyStyler? = null

        fun getInstance(): GroovyStyler {
            return groovyStyler ?: GroovyStyler().also {
                groovyStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = GroovyLexer(sourceReader)

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
                    GroovyToken.LONG_LITERAL,
                    GroovyToken.INTEGER_LITERAL,
                    GroovyToken.FLOAT_LITERAL,
                    GroovyToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.EQEQ,
                    GroovyToken.NOTEQ,
                    GroovyToken.EQEQEQ,
                    GroovyToken.NOTEQEQEQ,
                    GroovyToken.OROR,
                    GroovyToken.PLUSPLUS,
                    GroovyToken.MINUSMINUS,
                    GroovyToken.POW,
                    GroovyToken.LTEQGT,
                    GroovyToken.LT,
                    GroovyToken.LTEQ,
                    GroovyToken.LTLTEQ,
                    GroovyToken.GT,
                    GroovyToken.GTEQ,
                    GroovyToken.GTGTEQ,
                    GroovyToken.GTGTGTEQ,
                    GroovyToken.AND,
                    GroovyToken.ANDAND,
                    GroovyToken.PLUSEQ,
                    GroovyToken.MINUSEQ,
                    GroovyToken.MULTEQ,
                    GroovyToken.DIVEQ,
                    GroovyToken.ANDEQ,
                    GroovyToken.OREQ,
                    GroovyToken.XOREQ,
                    GroovyToken.MODEQ,
                    GroovyToken.QUESTEQ,
                    GroovyToken.POWEQ,
                    GroovyToken.LPAREN,
                    GroovyToken.RPAREN,
                    GroovyToken.LBRACE,
                    GroovyToken.RBRACE,
                    GroovyToken.LBRACK,
                    GroovyToken.RBRACK,
                    GroovyToken.EQ,
                    GroovyToken.NOT,
                    GroovyToken.TILDE,
                    GroovyToken.QUEST,
                    GroovyToken.COLON,
                    GroovyToken.PLUS,
                    GroovyToken.MINUS,
                    GroovyToken.MULT,
                    GroovyToken.DIV,
                    GroovyToken.OR,
                    GroovyToken.XOR,
                    GroovyToken.MOD,
                    GroovyToken.ELLIPSIS,
                    GroovyToken.RANGE,
                    GroovyToken.ELVIS,
                    GroovyToken.SPREAD_DOT,
                    GroovyToken.SAFE_DOT,
                    GroovyToken.METHOD_CLOSURE,
                    GroovyToken.REGEX_FIND,
                    GroovyToken.REGEX_MATCH,
                    GroovyToken.DOUBLE_COLON,
                    GroovyToken.ARROW -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.SEMICOLON,
                    GroovyToken.COMMA,
                    GroovyToken.DOT -> {
                        continue // skip
                    }
                    GroovyToken.PACKAGE,
                    GroovyToken.STRICTFP,
                    GroovyToken.IMPORT,
                    GroovyToken.STATIC,
                    GroovyToken.DEF,
                    GroovyToken.VAR,
                    GroovyToken.CLASS,
                    GroovyToken.INTERFACE,
                    GroovyToken.ENUM,
                    GroovyToken.TRAIT,
                    GroovyToken.EXTENDS,
                    GroovyToken.SUPER,
                    GroovyToken.VOID,
                    GroovyToken.AS,
                    GroovyToken.PRIVATE,
                    GroovyToken.ABSTRACT,
                    GroovyToken.PUBLIC,
                    GroovyToken.PROTECTED,
                    GroovyToken.TRANSIENT,
                    GroovyToken.NATIVE,
                    GroovyToken.SYNCHRONIZED,
                    GroovyToken.VOLATILE,
                    GroovyToken.DEFAULT,
                    GroovyToken.DO,
                    GroovyToken.THROWS,
                    GroovyToken.IMPLEMENTS,
                    GroovyToken.THIS,
                    GroovyToken.IF,
                    GroovyToken.ELSE,
                    GroovyToken.WHILE,
                    GroovyToken.SWITCH,
                    GroovyToken.FOR,
                    GroovyToken.IN,
                    GroovyToken.RETURN,
                    GroovyToken.BREAK,
                    GroovyToken.CONTINUE,
                    GroovyToken.THROW,
                    GroovyToken.ASSERT,
                    GroovyToken.CASE,
                    GroovyToken.TRY,
                    GroovyToken.FINALLY,
                    GroovyToken.CATCH,
                    GroovyToken.INSTANCEOF,
                    GroovyToken.NEW,
                    GroovyToken.FINAL,
                    GroovyToken.NOT_IN,
                    GroovyToken.NOT_INSTANCEOF -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.BOOLEAN,
                    GroovyToken.CHAR,
                    GroovyToken.BYTE,
                    GroovyToken.DOUBLE,
                    GroovyToken.FLOAT,
                    GroovyToken.INT,
                    GroovyToken.LONG,
                    GroovyToken.SHORT -> {
                        val styleSpan = StyleSpan(syntaxScheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.TRUE,
                    GroovyToken.FALSE,
                    GroovyToken.NULL -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.ANNOTATION -> {
                        val styleSpan = StyleSpan(syntaxScheme.preprocessorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.SINGLE_QUOTED_STRING,
                    GroovyToken.DOUBLE_QUOTED_STRING,
                    GroovyToken.TRIPLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.SHEBANG_COMMENT,
                    GroovyToken.LINE_COMMENT,
                    GroovyToken.BLOCK_COMMENT,
                    GroovyToken.DOC_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    GroovyToken.IDENTIFIER,
                    GroovyToken.WHITESPACE,
                    GroovyToken.BAD_CHARACTER -> {
                        continue
                    }
                    GroovyToken.EOF -> {
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