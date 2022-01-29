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

package com.blacksquircle.ui.language.java.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.java.lexer.JavaLexer
import com.blacksquircle.ui.language.java.lexer.JavaToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class JavaStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "JavaStyler"

        // TODO support different return types
        private val METHOD = Pattern.compile("(?<=(void)) (\\w+)")

        private var javaStyler: JavaStyler? = null

        fun getInstance(): JavaStyler {
            return javaStyler ?: JavaStyler().also {
                javaStyler = it
            }
        }
    }

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = JavaLexer(sourceReader)

        // FIXME flex doesn't support positive lookbehind
        val matcher = METHOD.matcher(source)
        matcher.region(0, source.length)
        while (matcher.find()) {
            val styleSpan = StyleSpan(scheme.methodColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }

        while (true) {
            try {
                when (lexer.advance()) {
                    JavaToken.LONG_LITERAL,
                    JavaToken.INTEGER_LITERAL,
                    JavaToken.FLOAT_LITERAL,
                    JavaToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.EQEQ,
                    JavaToken.NOTEQ,
                    JavaToken.OROR,
                    JavaToken.PLUSPLUS,
                    JavaToken.MINUSMINUS,
                    JavaToken.LT,
                    JavaToken.LTLT,
                    JavaToken.LTEQ,
                    JavaToken.LTLTEQ,
                    JavaToken.GT,
                    JavaToken.GTGT,
                    JavaToken.GTGTGT,
                    JavaToken.GTEQ,
                    JavaToken.GTGTEQ,
                    JavaToken.AND,
                    JavaToken.ANDAND,
                    JavaToken.PLUSEQ,
                    JavaToken.MINUSEQ,
                    JavaToken.MULTEQ,
                    JavaToken.DIVEQ,
                    JavaToken.ANDEQ,
                    JavaToken.OREQ,
                    JavaToken.XOREQ,
                    JavaToken.MODEQ,
                    JavaToken.LPAREN,
                    JavaToken.RPAREN,
                    JavaToken.LBRACE,
                    JavaToken.RBRACE,
                    JavaToken.LBRACK,
                    JavaToken.RBRACK,
                    JavaToken.EQ,
                    JavaToken.NOT,
                    JavaToken.TILDE,
                    JavaToken.QUEST,
                    JavaToken.COLON,
                    JavaToken.PLUS,
                    JavaToken.MINUS,
                    JavaToken.MULT,
                    JavaToken.DIV,
                    JavaToken.OR,
                    JavaToken.XOR,
                    JavaToken.MOD,
                    JavaToken.ELLIPSIS,
                    JavaToken.DOUBLE_COLON,
                    JavaToken.ARROW -> {
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.SEMICOLON,
                    JavaToken.COMMA,
                    JavaToken.DOT -> {
                        continue // skip
                    }
                    JavaToken.ABSTRACT,
                    JavaToken.ASSERT,
                    JavaToken.BREAK,
                    JavaToken.CASE,
                    JavaToken.CATCH,
                    JavaToken.CLASS,
                    JavaToken.CONST,
                    JavaToken.CONTINUE,
                    JavaToken.DEFAULT,
                    JavaToken.DO,
                    JavaToken.ELSE,
                    JavaToken.ENUM,
                    JavaToken.EXTENDS,
                    JavaToken.FINAL,
                    JavaToken.FINALLY,
                    JavaToken.FOR,
                    JavaToken.GOTO,
                    JavaToken.IF,
                    JavaToken.IMPLEMENTS,
                    JavaToken.IMPORT,
                    JavaToken.INSTANCEOF,
                    JavaToken.INTERFACE,
                    JavaToken.NATIVE,
                    JavaToken.NEW,
                    JavaToken.PACKAGE,
                    JavaToken.PRIVATE,
                    JavaToken.PROTECTED,
                    JavaToken.PUBLIC,
                    JavaToken.STATIC,
                    JavaToken.STRICTFP,
                    JavaToken.SUPER,
                    JavaToken.SWITCH,
                    JavaToken.SYNCHRONIZED,
                    JavaToken.THIS,
                    JavaToken.THROW,
                    JavaToken.THROWS,
                    JavaToken.TRANSIENT,
                    JavaToken.TRY,
                    JavaToken.VOID,
                    JavaToken.VOLATILE,
                    JavaToken.WHILE,
                    JavaToken.RETURN -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.BOOLEAN,
                    JavaToken.CHAR,
                    JavaToken.BYTE,
                    JavaToken.DOUBLE,
                    JavaToken.FLOAT,
                    JavaToken.INT,
                    JavaToken.LONG,
                    JavaToken.SHORT -> {
                        val styleSpan = StyleSpan(scheme.typeColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.TRUE,
                    JavaToken.FALSE,
                    JavaToken.NULL -> {
                        val styleSpan = StyleSpan(scheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.ANNOTATION -> {
                        val styleSpan = StyleSpan(scheme.preprocessorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.DOUBLE_QUOTED_STRING,
                    JavaToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(scheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.LINE_COMMENT,
                    JavaToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(scheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JavaToken.IDENTIFIER,
                    JavaToken.WHITESPACE,
                    JavaToken.BAD_CHARACTER -> {
                        continue
                    }
                    JavaToken.EOF -> {
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