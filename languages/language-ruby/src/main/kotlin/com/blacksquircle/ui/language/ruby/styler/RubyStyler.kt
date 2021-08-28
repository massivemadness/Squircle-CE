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

package com.blacksquircle.ui.language.ruby.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.ruby.lexer.RubyLexer
import com.blacksquircle.ui.language.ruby.lexer.RubyToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class RubyStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "RubyStyler"

        private val METHOD = Pattern.compile("(?<=(function)) (\\w+)")

        private var rubyStyler: RubyStyler? = null

        fun getInstance(): RubyStyler {
            return rubyStyler ?: RubyStyler().also {
                rubyStyler = it
            }
        }
    }
    
    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = RubyLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    RubyToken.LONG_LITERAL,
                    RubyToken.INTEGER_LITERAL,
                    RubyToken.FLOAT_LITERAL,
                    RubyToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    RubyToken.LPAREN,
                    RubyToken.RPAREN,
                    RubyToken.LBRACE,
                    RubyToken.RBRACE,
                    RubyToken.LBRACK,
                    RubyToken.RBRACK,
                    RubyToken.PLUS,
                    RubyToken.MINUS,
                    RubyToken.MULT,
                    RubyToken.POW,
                    RubyToken.DIV,
                    RubyToken.MOD,
                    RubyToken.LT,
                    RubyToken.GT,
                    RubyToken.EQ,
                    RubyToken.LTLT,
                    RubyToken.GTGT,
                    RubyToken.XOR,
                    RubyToken.TILDE,
                    RubyToken.LTEQ,
                    RubyToken.GTEQ,
                    RubyToken.EQEQ,
                    RubyToken.NOTEQ,
                    RubyToken.LTGT,
                    RubyToken.COMMA,
                    RubyToken.COLON,
                    RubyToken.DOT,
                    RubyToken.RANGE,
                    RubyToken.BACKTICK,
                    RubyToken.SEMICOLON,
                    RubyToken.PLUSEQ,
                    RubyToken.MINUSEQ,
                    RubyToken.MULTEQ,
                    RubyToken.DIVEQ,
                    RubyToken.MODEQ,
                    RubyToken.ANDEQ,
                    RubyToken.OREQ,
                    RubyToken.XOREQ,
                    RubyToken.GTGTEQ,
                    RubyToken.LTLTEQ,
                    RubyToken.POWEQ,
                    RubyToken.ANDAND,
                    RubyToken.OROR,
                    RubyToken.NOT -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    RubyToken.CLASS,
                    RubyToken.DEF,
                    RubyToken.END,
                    RubyToken.MODULE,
                    RubyToken.AND_KEYWORD,
                    RubyToken.BEGIN,
                    RubyToken.BREAK,
                    RubyToken.DO,
                    RubyToken.ENSURE,
                    RubyToken.FOR,
                    RubyToken.IN,
                    RubyToken.NEXT,
                    RubyToken.NOT_KEYWORD,
                    RubyToken.OR_KEYWORD,
                    RubyToken.REDO,
                    RubyToken.RESCUE,
                    RubyToken.RETRY,
                    RubyToken.YIELD,
                    RubyToken.UNLESS,
                    RubyToken.WHILE,
                    RubyToken.IF,
                    RubyToken.CASE,
                    RubyToken.WHEN,
                    RubyToken.THEN,
                    RubyToken.ELSE,
                    RubyToken.ELSIF,
                    RubyToken.UNTIL,                       
                    RubyToken.RETURN -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                   RubyToken.INSTANCE_VARIABLE -> {
                        val styleSpan = StyleSpan(syntaxScheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    RubyToken.METHOD,
                    RubyToken.EMBEDDED_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.methodColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    RubyToken.TRUE,
                    RubyToken.FALSE,
                    RubyToken.NIL -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    // Preprocessor
                    RubyToken._ENCODING, 
                    RubyToken._FILE,
                    RubyToken._LINE, 
                    RubyToken.DEFINED -> {
                        val styleSpan = StyleSpan(syntaxScheme.preprocessorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    RubyToken.DOUBLE_QUOTED_STRING,
                    RubyToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    RubyToken.LINE_COMMENT,
                    RubyToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    RubyToken.IDENTIFIER,
                    RubyToken.WHITESPACE,
                    RubyToken.BAD_CHARACTER -> {
                        continue
                    }
                    RubyToken.EOF -> {
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