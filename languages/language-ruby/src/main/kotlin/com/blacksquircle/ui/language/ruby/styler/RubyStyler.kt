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

package com.blacksquircle.ui.language.ruby.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.ruby.lexer.RubyLexer
import com.blacksquircle.ui.language.ruby.lexer.RubyToken
import java.io.IOException
import java.io.StringReader

class RubyStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "RubyStyler"

        private var rubyStyler: RubyStyler? = null

        fun getInstance(): RubyStyler {
            return rubyStyler ?: RubyStyler().also {
                rubyStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = RubyLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    RubyToken.LONG_LITERAL,
                    RubyToken.INTEGER_LITERAL,
                    RubyToken.FLOAT_LITERAL,
                    RubyToken.DOUBLE_LITERAL -> {
                        val tokenType = TokenType.NUMBER
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
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
                    RubyToken.OR,
                    RubyToken.AND,
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
                        val tokenType = TokenType.OPERATOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    RubyToken.ALIAS,
                    RubyToken.SUPER,
                    RubyToken.SELF,
                    RubyToken.UNDEF,
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
                        val tokenType = TokenType.KEYWORD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    RubyToken.INSTANCE_VARIABLE -> {
                        val tokenType = TokenType.TYPE
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    RubyToken.METHOD,
                    RubyToken.EMBEDDED_LITERAL -> {
                        val tokenType = TokenType.METHOD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    RubyToken.TRUE,
                    RubyToken.FALSE,
                    RubyToken.NIL -> {
                        val tokenType = TokenType.LANG_CONST
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    RubyToken._ENCODING,
                    RubyToken._FILE,
                    RubyToken._LINE,
                    RubyToken.DEFINED -> {
                        val tokenType = TokenType.PREPROCESSOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    RubyToken.DOUBLE_QUOTED_STRING,
                    RubyToken.SINGLE_QUOTED_STRING -> {
                        val tokenType = TokenType.STRING
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    RubyToken.LINE_COMMENT,
                    RubyToken.BLOCK_COMMENT -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
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
        return syntaxHighlightResults
    }
}