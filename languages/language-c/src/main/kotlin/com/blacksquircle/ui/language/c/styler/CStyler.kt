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

package com.blacksquircle.ui.language.c.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.c.lexer.CLexer
import com.blacksquircle.ui.language.c.lexer.CToken
import java.io.IOException
import java.io.StringReader

class CStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "CStyler"

        private var cStyler: CStyler? = null

        fun getInstance(): CStyler {
            return cStyler ?: CStyler().also {
                cStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = CLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    CToken.LONG_LITERAL,
                    CToken.INTEGER_LITERAL,
                    CToken.FLOAT_LITERAL,
                    CToken.DOUBLE_LITERAL -> {
                        val tokenType = TokenType.NUMBER
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.TRIGRAPH,
                    CToken.EQ,
                    CToken.PLUS,
                    CToken.MINUS,
                    CToken.MULT,
                    CToken.DIV,
                    CToken.MOD,
                    CToken.TILDA,
                    CToken.LT,
                    CToken.GT,
                    CToken.LTLT,
                    CToken.GTGT,
                    CToken.EQEQ,
                    CToken.PLUSEQ,
                    CToken.MINUSEQ,
                    CToken.MULTEQ,
                    CToken.DIVEQ,
                    CToken.MODEQ,
                    CToken.ANDEQ,
                    CToken.OREQ,
                    CToken.XOREQ,
                    CToken.GTEQ,
                    CToken.LTEQ,
                    CToken.NOTEQ,
                    CToken.GTGTEQ,
                    CToken.LTLTEQ,
                    CToken.XOR,
                    CToken.AND,
                    CToken.ANDAND,
                    CToken.OR,
                    CToken.OROR,
                    CToken.QUEST,
                    CToken.COLON,
                    CToken.NOT,
                    CToken.PLUSPLUS,
                    CToken.MINUSMINUS,
                    CToken.LPAREN,
                    CToken.RPAREN,
                    CToken.LBRACE,
                    CToken.RBRACE,
                    CToken.LBRACK,
                    CToken.RBRACK -> {
                        val tokenType = TokenType.OPERATOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.SEMICOLON,
                    CToken.COMMA,
                    CToken.DOT -> {
                        continue // skip
                    }
                    CToken.AUTO,
                    CToken.BREAK,
                    CToken.CASE,
                    CToken.CONST,
                    CToken.CONTINUE,
                    CToken.DEFAULT,
                    CToken.DO,
                    CToken.ELSE,
                    CToken.ENUM,
                    CToken.EXTERN,
                    CToken.FOR,
                    CToken.GOTO,
                    CToken.IF,
                    CToken.REGISTER,
                    CToken.SIZEOF,
                    CToken.STATIC,
                    CToken.STRUCT,
                    CToken.SWITCH,
                    CToken.TYPEDEF,
                    CToken.UNION,
                    CToken.VOLATILE,
                    CToken.WHILE,
                    CToken.RETURN -> {
                        val tokenType = TokenType.KEYWORD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.FUNCTION -> {
                        val tokenType = TokenType.METHOD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.BOOL,
                    CToken.CHAR,
                    CToken.DIV_T,
                    CToken.DOUBLE,
                    CToken.FLOAT,
                    CToken.INT,
                    CToken.LDIV_T,
                    CToken.LONG,
                    CToken.SHORT,
                    CToken.SIGNED,
                    CToken.SIZE_T,
                    CToken.UNSIGNED,
                    CToken.VOID,
                    CToken.WCHAR_T -> {
                        val tokenType = TokenType.TYPE
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.TRUE,
                    CToken.FALSE,
                    CToken.NULL -> {
                        val tokenType = TokenType.LANG_CONST
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.__DATE__,
                    CToken.__TIME__,
                    CToken.__FILE__,
                    CToken.__LINE__,
                    CToken.__STDC__,
                    CToken.PREPROCESSOR -> {
                        val tokenType = TokenType.PREPROCESSOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.DOUBLE_QUOTED_STRING,
                    CToken.SINGLE_QUOTED_STRING -> {
                        val tokenType = TokenType.STRING
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.LINE_COMMENT,
                    CToken.BLOCK_COMMENT -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CToken.IDENTIFIER,
                    CToken.WHITESPACE,
                    CToken.BAD_CHARACTER -> {
                        continue
                    }
                    CToken.EOF -> {
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