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

package com.blacksquircle.ui.language.shell.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.shell.lexer.ShellLexer
import com.blacksquircle.ui.language.shell.lexer.ShellToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class ShellStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "ShellStyler"

        private val METHOD = Pattern.compile("(\\w+\\s*\\w*)\\(\\)\\s*\\{")

        private var shellStyler: ShellStyler? = null

        fun getInstance(): ShellStyler {
            return shellStyler ?: ShellStyler().also {
                shellStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = ShellLexer(sourceReader)

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
                    ShellToken.INTEGER_LITERAL,
                    ShellToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ShellToken.BREAK,
                    ShellToken.CASE,
                    ShellToken.CONTINUE,
                    ShellToken.ECHO,
                    ShellToken.ESAC,
                    ShellToken.EVAL,
                    ShellToken.ELIF,
                    ShellToken.ELSE,
                    ShellToken.EXIT,
                    ShellToken.EXEC,
                    ShellToken.EXPORT,
                    ShellToken.DONE,
                    ShellToken.DO,
                    ShellToken.FI,
                    ShellToken.FOR,
                    ShellToken.IN,
                    ShellToken.FUNCTION,
                    ShellToken.IF,
                    ShellToken.SET,
                    ShellToken.SELECT,
                    ShellToken.SHIFT,
                    ShellToken.TRAP,
                    ShellToken.THEN,
                    ShellToken.ULIMIT,
                    ShellToken.UMASK,
                    ShellToken.UNSET,
                    ShellToken.UNTIL,
                    ShellToken.WAIT,
                    ShellToken.WHILE,
                    ShellToken.LET,
                    ShellToken.LOCAL,
                    ShellToken.READ,
                    ShellToken.READONLY,
                    ShellToken.RETURN,
                    ShellToken.TEST -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ShellToken.TRUE,
                    ShellToken.FALSE -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ShellToken.MULTEQ,
                    ShellToken.DIVEQ,
                    ShellToken.MODEQ,
                    ShellToken.PLUSEQ,
                    ShellToken.MINUSEQ,
                    ShellToken.SHIFT_RIGHT_EQ,
                    ShellToken.SHIFT_LEFT_EQ,
                    ShellToken.BIT_AND_EQ,
                    ShellToken.BIT_OR_EQ,
                    ShellToken.BIT_XOR_EQ,
                    ShellToken.NOTEQ,
                    ShellToken.EQEQ,
                    ShellToken.REGEXP,
                    ShellToken.GTEQ,
                    ShellToken.LTEQ,
                    ShellToken.PLUS_PLUS,
                    ShellToken.MINUS_MINUS,
                    ShellToken.EXPONENT,
                    ShellToken.BANG,
                    ShellToken.TILDE,
                    ShellToken.PLUS,
                    ShellToken.MINUS,
                    ShellToken.MULT,
                    ShellToken.DIV,
                    ShellToken.MOD,
                    ShellToken.SHIFT_LEFT,
                    ShellToken.SHIFT_RIGHT,
                    ShellToken.LT,
                    ShellToken.GT,
                    ShellToken.AND_AND,
                    ShellToken.OR_OR,
                    ShellToken.AND,
                    ShellToken.XOR,
                    ShellToken.OR,
                    ShellToken.DOLLAR,
                    ShellToken.EQ,
                    ShellToken.BACKTICK,
                    ShellToken.QUEST,
                    ShellToken.COLON,
                    ShellToken.LPAREN,
                    ShellToken.RPAREN,
                    ShellToken.LBRACE,
                    ShellToken.RBRACE,
                    ShellToken.LBRACK,
                    ShellToken.RBRACK,
                    ShellToken.EVAL_CONTENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ShellToken.SEMICOLON,
                    ShellToken.COMMA,
                    ShellToken.DOT -> {
                        continue // skip
                    }
                    ShellToken.SHEBANG,
                    ShellToken.COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ShellToken.DOUBLE_QUOTED_STRING,
                    ShellToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ShellToken.IDENTIFIER,
                    ShellToken.WHITESPACE,
                    ShellToken.BAD_CHARACTER -> {
                        continue
                    }
                    ShellToken.EOF -> {
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