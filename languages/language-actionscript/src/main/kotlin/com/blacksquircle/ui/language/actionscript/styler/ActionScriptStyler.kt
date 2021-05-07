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

package com.blacksquircle.ui.language.actionscript.styler

import android.util.Log
import com.blacksquircle.ui.language.actionscript.lexer.ActionScriptLexer
import com.blacksquircle.ui.language.actionscript.lexer.ActionScriptToken
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class ActionScriptStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "ActionScriptStyler"

        private val METHOD = Pattern.compile("(?<=(function)) (\\w+)")

        private var actionScriptStyler: ActionScriptStyler? = null

        fun getInstance(): ActionScriptStyler {
            return actionScriptStyler ?: ActionScriptStyler().also {
                actionScriptStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = ActionScriptLexer(sourceReader)

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
                    ActionScriptToken.LONG_LITERAL,
                    ActionScriptToken.INTEGER_LITERAL,
                    ActionScriptToken.FLOAT_LITERAL,
                    ActionScriptToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.PLUS,
                    ActionScriptToken.MINUSMINUS,
                    ActionScriptToken.DIV,
                    ActionScriptToken.PLUSPLUS,
                    ActionScriptToken.MOD,
                    ActionScriptToken.MULT,
                    ActionScriptToken.MINUS,
                    ActionScriptToken.PLUSEQ,
                    ActionScriptToken.DIVEQ,
                    ActionScriptToken.MODEQ,
                    ActionScriptToken.MULTEQ,
                    ActionScriptToken.MINUSEQ,
                    ActionScriptToken.EQ,
                    ActionScriptToken.AND,
                    ActionScriptToken.LTLT,
                    ActionScriptToken.TILDE,
                    ActionScriptToken.OR,
                    ActionScriptToken.GTGT,
                    ActionScriptToken.GTGTGT,
                    ActionScriptToken.XOR,
                    ActionScriptToken.ANDEQ,
                    ActionScriptToken.LTLTEQ,
                    ActionScriptToken.OREQ,
                    ActionScriptToken.GTGTEQ,
                    ActionScriptToken.GTGTGTEQ,
                    ActionScriptToken.XOREQ,
                    ActionScriptToken.EQEQ,
                    ActionScriptToken.GT,
                    ActionScriptToken.GTEQ,
                    ActionScriptToken.NOTEQ,
                    ActionScriptToken.LT,
                    ActionScriptToken.LTEQ,
                    ActionScriptToken.EQEQEQ,
                    ActionScriptToken.NOTEQEQ,
                    ActionScriptToken.ANDAND,
                    ActionScriptToken.ANDANDEQ,
                    ActionScriptToken.NOT,
                    ActionScriptToken.OROR,
                    ActionScriptToken.OROREQ,
                    ActionScriptToken.LPAREN,
                    ActionScriptToken.RPAREN,
                    ActionScriptToken.LBRACE,
                    ActionScriptToken.RBRACE,
                    ActionScriptToken.LBRACK,
                    ActionScriptToken.RBRACK,
                    ActionScriptToken.QUEST,
                    ActionScriptToken.COLON -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.SEMICOLON,
                    ActionScriptToken.COMMA,
                    ActionScriptToken.DOT -> {
                        continue // skip
                    }
                    ActionScriptToken.BREAK,
                    ActionScriptToken.CASE,
                    ActionScriptToken.CONTINUE,
                    ActionScriptToken.DEFAULT,
                    ActionScriptToken.DO,
                    ActionScriptToken.WHILE,
                    ActionScriptToken.ELSE,
                    ActionScriptToken.FOR,
                    ActionScriptToken.IN,
                    ActionScriptToken.EACH,
                    ActionScriptToken.IF,
                    ActionScriptToken.LABEL,
                    ActionScriptToken.RETURN,
                    ActionScriptToken.SUPER,
                    ActionScriptToken.SWITCH,
                    ActionScriptToken.THROW,
                    ActionScriptToken.TRY,
                    ActionScriptToken.CATCH,
                    ActionScriptToken.FINALLY,
                    ActionScriptToken.WITH,
                    ActionScriptToken.DYNAMIC,
                    ActionScriptToken.FINAL,
                    ActionScriptToken.INTERNAL,
                    ActionScriptToken.NATIVE,
                    ActionScriptToken.OVERRIDE,
                    ActionScriptToken.PRIVATE,
                    ActionScriptToken.PROTECTED,
                    ActionScriptToken.PUBLIC,
                    ActionScriptToken.STATIC,
                    ActionScriptToken.PARAMETER,
                    ActionScriptToken.CLASS,
                    ActionScriptToken.CONST,
                    ActionScriptToken.EXTENDS,
                    ActionScriptToken.FUNCTION,
                    ActionScriptToken.GET,
                    ActionScriptToken.IMPLEMENTS,
                    ActionScriptToken.INTERFACE,
                    ActionScriptToken.NAMESPACE,
                    ActionScriptToken.PACKAGE,
                    ActionScriptToken.TYPEOF,
                    ActionScriptToken.SET,
                    ActionScriptToken.THIS,
                    ActionScriptToken.INCLUDE,
                    ActionScriptToken.INSTANCEOF,
                    ActionScriptToken.IMPORT,
                    ActionScriptToken.USE,
                    ActionScriptToken.AS,
                    ActionScriptToken.NEW,
                    ActionScriptToken.VAR -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.ARRAY,
                    ActionScriptToken.OBJECT,
                    ActionScriptToken.BOOLEAN,
                    ActionScriptToken.NUMBER,
                    ActionScriptToken.STRING,
                    ActionScriptToken.VOID,
                    ActionScriptToken.VECTOR,
                    ActionScriptToken.INT,
                    ActionScriptToken.UINT -> {
                        val styleSpan = StyleSpan(syntaxScheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.TRUE,
                    ActionScriptToken.FALSE,
                    ActionScriptToken.NULL,
                    ActionScriptToken.UNDEFINED,
                    ActionScriptToken.NAN -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.PREPROCESSOR -> {
                        val styleSpan = StyleSpan(syntaxScheme.preprocessorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.DOUBLE_QUOTED_STRING,
                    ActionScriptToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.LINE_COMMENT,
                    ActionScriptToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    ActionScriptToken.IDENTIFIER,
                    ActionScriptToken.WHITESPACE,
                    ActionScriptToken.BAD_CHARACTER -> {
                        continue
                    }
                    ActionScriptToken.EOF -> {
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