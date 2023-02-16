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

package com.blacksquircle.ui.language.cpp.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.cpp.lexer.CppLexer
import com.blacksquircle.ui.language.cpp.lexer.CppToken
import java.io.IOException
import java.io.StringReader

class CppStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "CppStyler"

        private var cppStyler: CppStyler? = null

        fun getInstance(): CppStyler {
            return cppStyler ?: CppStyler().also {
                cppStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = CppLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    CppToken.LONG_LITERAL,
                    CppToken.INTEGER_LITERAL,
                    CppToken.FLOAT_LITERAL,
                    CppToken.DOUBLE_LITERAL -> {
                        val tokenType = TokenType.NUMBER
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.TRIGRAPH,
                    CppToken.EQ,
                    CppToken.PLUS,
                    CppToken.MINUS,
                    CppToken.MULT,
                    CppToken.DIV,
                    CppToken.MOD,
                    CppToken.TILDA,
                    CppToken.LT,
                    CppToken.GT,
                    CppToken.LTLT,
                    CppToken.GTGT,
                    CppToken.EQEQ,
                    CppToken.PLUSEQ,
                    CppToken.MINUSEQ,
                    CppToken.MULTEQ,
                    CppToken.DIVEQ,
                    CppToken.MODEQ,
                    CppToken.ANDEQ,
                    CppToken.OREQ,
                    CppToken.XOREQ,
                    CppToken.GTEQ,
                    CppToken.LTEQ,
                    CppToken.NOTEQ,
                    CppToken.GTGTEQ,
                    CppToken.LTLTEQ,
                    CppToken.XOR,
                    CppToken.AND,
                    CppToken.ANDAND,
                    CppToken.OR,
                    CppToken.OROR,
                    CppToken.QUEST,
                    CppToken.COLON,
                    CppToken.NOT,
                    CppToken.PLUSPLUS,
                    CppToken.MINUSMINUS,
                    CppToken.LPAREN,
                    CppToken.RPAREN,
                    CppToken.LBRACE,
                    CppToken.RBRACE,
                    CppToken.LBRACK,
                    CppToken.RBRACK -> {
                        val tokenType = TokenType.OPERATOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.SEMICOLON,
                    CppToken.COMMA,
                    CppToken.DOT -> {
                        continue // skip
                    }
                    CppToken.AUTO,
                    CppToken.BREAK,
                    CppToken.CASE,
                    CppToken.CATCH,
                    CppToken.CLASS,
                    CppToken.CONST,
                    CppToken.CONST_CAST,
                    CppToken.CONTINUE,
                    CppToken.DEFAULT,
                    CppToken.DELETE,
                    CppToken.DO,
                    CppToken.DYNAMIC_CAST,
                    CppToken.ELSE,
                    CppToken.ENUM,
                    CppToken.EXPLICIT,
                    CppToken.EXTERN,
                    CppToken.FOR,
                    CppToken.FRIEND,
                    CppToken.GOTO,
                    CppToken.IF,
                    CppToken.INLINE,
                    CppToken.MUTABLE,
                    CppToken.NAMESPACE,
                    CppToken.NEW,
                    CppToken.OPERATOR,
                    CppToken.PRIVATE,
                    CppToken.PROTECTED,
                    CppToken.PUBLIC,
                    CppToken.REGISTER,
                    CppToken.REINTERPRET_CAST,
                    CppToken.SIZEOF,
                    CppToken.STATIC,
                    CppToken.STATIC_CAST,
                    CppToken.STRUCT,
                    CppToken.SWITCH,
                    CppToken.TEMPLATE,
                    CppToken.THIS,
                    CppToken.THROW,
                    CppToken.TRY,
                    CppToken.TYPEDEF,
                    CppToken.TYPEID,
                    CppToken.TYPENAME,
                    CppToken.UNION,
                    CppToken.USING,
                    CppToken.VIRTUAL,
                    CppToken.VOLATILE,
                    CppToken.WHILE,
                    CppToken.RETURN -> {
                        val tokenType = TokenType.KEYWORD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.FUNCTION -> {
                        val tokenType = TokenType.METHOD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.BOOL,
                    CppToken.CHAR,
                    CppToken.DIV_T,
                    CppToken.DOUBLE,
                    CppToken.FLOAT,
                    CppToken.INT,
                    CppToken.LDIV_T,
                    CppToken.LONG,
                    CppToken.SHORT,
                    CppToken.SIGNED,
                    CppToken.SIZE_T,
                    CppToken.UNSIGNED,
                    CppToken.VOID,
                    CppToken.WCHAR_T -> {
                        val tokenType = TokenType.TYPE
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.TRUE,
                    CppToken.FALSE,
                    CppToken.NULL -> {
                        val tokenType = TokenType.LANG_CONST
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.__DATE__,
                    CppToken.__TIME__,
                    CppToken.__FILE__,
                    CppToken.__LINE__,
                    CppToken.__STDC__,
                    CppToken.PREPROCESSOR -> {
                        val tokenType = TokenType.PREPROCESSOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.DOUBLE_QUOTED_STRING,
                    CppToken.SINGLE_QUOTED_STRING -> {
                        val tokenType = TokenType.STRING
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.LINE_COMMENT,
                    CppToken.BLOCK_COMMENT -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CppToken.IDENTIFIER,
                    CppToken.WHITESPACE,
                    CppToken.BAD_CHARACTER -> {
                        continue
                    }
                    CppToken.EOF -> {
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