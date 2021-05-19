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

package com.blacksquircle.ui.language.php.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.php.lexer.PhpLexer
import com.blacksquircle.ui.language.php.lexer.PhpToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class PhpStyler : LanguageStyler {

    companion object {

        private const val TAG = "PhpStyler"

        private val METHOD = Pattern.compile("(?<=(function)) (\\w+)")

        private var phpStyler: PhpStyler? = null

        fun getInstance(): PhpStyler {
            return phpStyler ?: PhpStyler().also {
                phpStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = PhpLexer(sourceReader)

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
                    PhpToken.INTEGER_LITERAL,
                    PhpToken.FLOAT_LITERAL,
                    PhpToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.VARIABLE_LITERAL -> {
                        val styleSpan = StyleSpan(syntaxScheme.variableColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.PLUS,
                    PhpToken.MINUS,
                    PhpToken.LTEQ,
                    PhpToken.XOR,
                    PhpToken.PLUSPLUS,
                    PhpToken.LT,
                    PhpToken.MULT,
                    PhpToken.GTEQ,
                    PhpToken.MOD,
                    PhpToken.MINUSMINUS,
                    PhpToken.GT,
                    PhpToken.DIV,
                    PhpToken.NOTEQ,
                    PhpToken.QUEST,
                    PhpToken.GTGT,
                    PhpToken.NOT,
                    PhpToken.AND,
                    PhpToken.EQEQ,
                    PhpToken.COLON,
                    PhpToken.TILDA,
                    PhpToken.OROR,
                    PhpToken.ANDAND,
                    PhpToken.GTGTGT,
                    PhpToken.EQ,
                    PhpToken.MINUSEQ,
                    PhpToken.MULTEQ,
                    PhpToken.DIVEQ,
                    PhpToken.OREQ,
                    PhpToken.ANDEQ,
                    PhpToken.XOREQ,
                    PhpToken.PLUSEQ,
                    PhpToken.MODEQ,
                    PhpToken.LTLTEQ,
                    PhpToken.GTGTEQ,
                    PhpToken.GTGTGTEQ,
                    PhpToken.LPAREN,
                    PhpToken.RPAREN,
                    PhpToken.LBRACE,
                    PhpToken.RBRACE,
                    PhpToken.LBRACK,
                    PhpToken.RBRACK -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.SEMICOLON,
                    PhpToken.COMMA,
                    PhpToken.DOT -> {
                        continue // skip
                    }
                    PhpToken.ABSTRACT,
                    PhpToken.AS,
                    PhpToken.BREAK,
                    PhpToken.CASE,
                    PhpToken.CATCH,
                    PhpToken.CONST,
                    PhpToken.CLASS,
                    PhpToken.CONTINUE,
                    PhpToken.DEBUGGER,
                    PhpToken.DEFAULT,
                    PhpToken.DELETE,
                    PhpToken.DO,
                    PhpToken.EACH,
                    PhpToken.ELSE,
                    PhpToken.ELSEIF,
                    PhpToken.ENUM,
                    PhpToken.EXPORT,
                    PhpToken.EXTENDS,
                    PhpToken.FINAL,
                    PhpToken.FINALLY,
                    PhpToken.FN,
                    PhpToken.FOR,
                    PhpToken.FOREACH,
                    PhpToken.FUNCTION,
                    PhpToken.GOTO,
                    PhpToken.GLOBAL,
                    PhpToken.IF,
                    PhpToken.IMPLEMENTS,
                    PhpToken.IMPORT,
                    PhpToken.IN,
                    PhpToken.INCLUDE,
                    PhpToken.INCLUDE_ONCE,
                    PhpToken.INSTANCEOF,
                    PhpToken.INSTEADOF,
                    PhpToken.INTERFACE,
                    PhpToken.LET,
                    PhpToken.NAMESPACE,
                    PhpToken.NATIVE,
                    PhpToken.NEW,
                    PhpToken.PACKAGE,
                    PhpToken.PARENT,
                    PhpToken.PRIVATE,
                    PhpToken.PROTECTED,
                    PhpToken.PUBLIC,
                    PhpToken.RETURN,
                    PhpToken.SELF,
                    PhpToken.STATIC,
                    PhpToken.SUPER,
                    PhpToken.SWITCH,
                    PhpToken.SYNCHRONIZED,
                    PhpToken.THIS,
                    PhpToken.THROW,
                    PhpToken.THROWS,
                    PhpToken.TYPEOF,
                    PhpToken.TRANSIENT,
                    PhpToken.TRY,
                    PhpToken.VAR,
                    PhpToken.VOID,
                    PhpToken.VOLATILE,
                    PhpToken.WHILE,
                    PhpToken.WITH -> {
                        val styleSpan = StyleSpan(syntaxScheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.BOOLEAN,
                    PhpToken.BYTE,
                    PhpToken.CHAR,
                    PhpToken.DOUBLE,
                    PhpToken.FLOAT,
                    PhpToken.INT,
                    PhpToken.LONG,
                    PhpToken.SHORT -> {
                        val styleSpan = StyleSpan(syntaxScheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.TRUE,
                    PhpToken.FALSE,
                    PhpToken.NULL,
                    PhpToken.NAN,
                    PhpToken.INFINITY -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.ARRAY,
                    PhpToken.DIE,
                    PhpToken.EVAL,
                    PhpToken.EMPTY,
                    PhpToken.ECHO,
                    PhpToken.EXIT,
                    PhpToken.PARSEINT,
                    PhpToken.PARSEFLOAT,
                    PhpToken.PRINT,
                    PhpToken.ESCAPE,
                    PhpToken.UNESCAPE,
                    PhpToken.ISNAN,
                    PhpToken.ISFINITE -> {
                        val styleSpan = StyleSpan(syntaxScheme.methodColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.DOUBLE_QUOTED_STRING,
                    PhpToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.LINE_COMMENT,
                    PhpToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    PhpToken.IDENTIFIER,
                    PhpToken.WHITESPACE,
                    PhpToken.BAD_CHARACTER -> {
                        continue
                    }
                    PhpToken.EOF -> {
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