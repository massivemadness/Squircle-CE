/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.language.kotlin.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.kotlin.lexer.KotlinLexer
import com.blacksquircle.ui.language.kotlin.lexer.KotlinToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class KotlinStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "KotlinStyler"

        private val METHOD = Pattern.compile("(?<=(fun)) (\\w+)")

        private var kotlinStyler: KotlinStyler? = null

        fun getInstance(): KotlinStyler {
            return kotlinStyler ?: KotlinStyler().also {
                kotlinStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = KotlinLexer(sourceReader)

        // FIXME flex doesn't support positive lookbehind
        val matcher = METHOD.matcher(source)
        matcher.region(0, source.length)
        while (matcher.find()) {
            val tokenType = TokenType.METHOD
            val syntaxHighlightResult = SyntaxHighlightResult(tokenType, matcher.start(), matcher.end())
            syntaxHighlightResults.add(syntaxHighlightResult)
        }

        while (true) {
            try {
                when (lexer.advance()) {
                    KotlinToken.LONG_LITERAL,
                    KotlinToken.INTEGER_LITERAL,
                    KotlinToken.FLOAT_LITERAL,
                    KotlinToken.DOUBLE_LITERAL -> {
                        val tokenType = TokenType.NUMBER
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    KotlinToken.EQEQ,
                    KotlinToken.NOTEQ,
                    KotlinToken.OROR,
                    KotlinToken.PLUSPLUS,
                    KotlinToken.MINUSMINUS,
                    KotlinToken.LT,
                    KotlinToken.LTLT,
                    KotlinToken.LTEQ,
                    KotlinToken.LTLTEQ,
                    KotlinToken.GT,
                    KotlinToken.GTGT,
                    KotlinToken.GTGTGT,
                    KotlinToken.GTEQ,
                    KotlinToken.GTGTEQ,
                    KotlinToken.AND,
                    KotlinToken.ANDAND,
                    KotlinToken.PLUSEQ,
                    KotlinToken.MINUSEQ,
                    KotlinToken.MULTEQ,
                    KotlinToken.DIVEQ,
                    KotlinToken.ANDEQ,
                    KotlinToken.OREQ,
                    KotlinToken.XOREQ,
                    KotlinToken.MODEQ,
                    KotlinToken.LPAREN,
                    KotlinToken.RPAREN,
                    KotlinToken.LBRACE,
                    KotlinToken.RBRACE,
                    KotlinToken.LBRACK,
                    KotlinToken.RBRACK,
                    KotlinToken.EQ,
                    KotlinToken.NOT,
                    KotlinToken.TILDE,
                    KotlinToken.QUEST,
                    KotlinToken.COLON,
                    KotlinToken.PLUS,
                    KotlinToken.MINUS,
                    KotlinToken.MULT,
                    KotlinToken.DIV,
                    KotlinToken.OR,
                    KotlinToken.XOR,
                    KotlinToken.MOD,
                    KotlinToken.ELVIS,
                    KotlinToken.ELLIPSIS,
                    KotlinToken.DOUBLE_COLON,
                    KotlinToken.ARROW -> {
                        val tokenType = TokenType.OPERATOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    KotlinToken.SEMICOLON,
                    KotlinToken.COMMA,
                    KotlinToken.DOT -> {
                        continue // skip
                    }
                    KotlinToken.ABSTRACT,
                    KotlinToken.ACTUAL,
                    KotlinToken.ANNOTATION_KEYWORD,
                    KotlinToken.AS,
                    KotlinToken.AS_QUEST,
                    KotlinToken.ASSERT,
                    KotlinToken.BREAK,
                    KotlinToken.BY,
                    KotlinToken.CATCH,
                    KotlinToken.CLASS,
                    KotlinToken.COMPANION,
                    KotlinToken.CONST,
                    KotlinToken.CONSTRUCTOR,
                    KotlinToken.CONTINUE,
                    KotlinToken.DATA,
                    KotlinToken.DO,
                    KotlinToken.ELSE,
                    KotlinToken.ENUM,
                    KotlinToken.EXPECT,
                    KotlinToken.FINALLY,
                    KotlinToken.FOR,
                    KotlinToken.FUN,
                    KotlinToken.GET,
                    KotlinToken.IF,
                    KotlinToken.IMPLEMENTS,
                    KotlinToken.IMPORT,
                    KotlinToken.INTERFACE,
                    KotlinToken.IN,
                    KotlinToken.INFIX,
                    KotlinToken.INIT,
                    KotlinToken.INTERNAL,
                    KotlinToken.INLINE,
                    KotlinToken.IS,
                    KotlinToken.LATEINIT,
                    KotlinToken.NATIVE,
                    KotlinToken.OBJECT,
                    KotlinToken.OPEN,
                    KotlinToken.OPERATOR,
                    KotlinToken.OR_KEYWORD,
                    KotlinToken.OUT,
                    KotlinToken.OVERRIDE,
                    KotlinToken.PACKAGE,
                    KotlinToken.PRIVATE,
                    KotlinToken.PROTECTED,
                    KotlinToken.PUBLIC,
                    KotlinToken.REIFIED,
                    KotlinToken.RETURN,
                    KotlinToken.SEALED,
                    KotlinToken.SET,
                    KotlinToken.SUPER,
                    KotlinToken.THIS,
                    KotlinToken.THROW,
                    KotlinToken.TRY,
                    KotlinToken.TYPEALIAS,
                    KotlinToken.VAL,
                    KotlinToken.VAR,
                    KotlinToken.VARARGS,
                    KotlinToken.WHEN,
                    KotlinToken.WHERE,
                    KotlinToken.WHILE -> {
                        val tokenType = TokenType.KEYWORD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    KotlinToken.TRUE,
                    KotlinToken.FALSE,
                    KotlinToken.NULL -> {
                        val tokenType = TokenType.LANG_CONST
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    KotlinToken.ANNOTATION -> {
                        val tokenType = TokenType.PREPROCESSOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    KotlinToken.DOUBLE_QUOTED_STRING,
                    KotlinToken.SINGLE_QUOTED_STRING -> {
                        val tokenType = TokenType.STRING
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    KotlinToken.LINE_COMMENT,
                    KotlinToken.BLOCK_COMMENT -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    KotlinToken.IDENTIFIER,
                    KotlinToken.WHITESPACE,
                    KotlinToken.BAD_CHARACTER -> {
                        continue
                    }
                    KotlinToken.EOF -> {
                        break
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message, e)
                break
            }
        }
        return syntaxHighlightResults
    }
}