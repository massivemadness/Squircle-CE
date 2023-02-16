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

package com.blacksquircle.ui.language.csharp.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.csharp.lexer.CSharpLexer
import com.blacksquircle.ui.language.csharp.lexer.CSharpToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class CSharpStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "CSharpStyler"

        // TODO support different return types
        private val METHOD = Pattern.compile("(?<=(void)) (\\w+)")

        private var csharpStyler: CSharpStyler? = null

        fun getInstance(): CSharpStyler {
            return csharpStyler ?: CSharpStyler().also {
                csharpStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = CSharpLexer(sourceReader)

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
                    CSharpToken.LONG_LITERAL,
                    CSharpToken.INTEGER_LITERAL,
                    CSharpToken.FLOAT_LITERAL,
                    CSharpToken.DOUBLE_LITERAL -> {
                        val tokenType = TokenType.NUMBER
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.PLUS,
                    CSharpToken.MINUSMINUS,
                    CSharpToken.DIV,
                    CSharpToken.PLUSPLUS,
                    CSharpToken.MOD,
                    CSharpToken.MULT,
                    CSharpToken.MINUS,
                    CSharpToken.PLUSEQ,
                    CSharpToken.DIVEQ,
                    CSharpToken.MODEQ,
                    CSharpToken.MULTEQ,
                    CSharpToken.MINUSEQ,
                    CSharpToken.EQ,
                    CSharpToken.AND,
                    CSharpToken.LTLT,
                    CSharpToken.TILDE,
                    CSharpToken.OR,
                    CSharpToken.GTGT,
                    CSharpToken.XOR,
                    CSharpToken.ANDEQ,
                    CSharpToken.LTLTEQ,
                    CSharpToken.OREQ,
                    CSharpToken.GTGTEQ,
                    CSharpToken.XOREQ,
                    CSharpToken.EQEQ,
                    CSharpToken.GT,
                    CSharpToken.GTEQ,
                    CSharpToken.NOTEQ,
                    CSharpToken.LT,
                    CSharpToken.LTEQ,
                    CSharpToken.ANDAND,
                    CSharpToken.NOT,
                    CSharpToken.OROR,
                    CSharpToken.LPAREN,
                    CSharpToken.RPAREN,
                    CSharpToken.LBRACE,
                    CSharpToken.RBRACE,
                    CSharpToken.LBRACK,
                    CSharpToken.RBRACK,
                    CSharpToken.QUEST,
                    CSharpToken.COLON -> {
                        val tokenType = TokenType.OPERATOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.SEMICOLON,
                    CSharpToken.COMMA,
                    CSharpToken.DOT -> {
                        continue // skip
                    }
                    CSharpToken.ABSTRACT,
                    CSharpToken.AS,
                    CSharpToken.ASYNC,
                    CSharpToken.AWAIT,
                    CSharpToken.BASE,
                    CSharpToken.BREAK,
                    CSharpToken.CASE,
                    CSharpToken.CATCH,
                    CSharpToken.CHECKED,
                    CSharpToken.CLASS,
                    CSharpToken.CONST,
                    CSharpToken.CONTINUE,
                    CSharpToken.DECIMAL,
                    CSharpToken.DEFAULT,
                    CSharpToken.DELEGATE,
                    CSharpToken.DO,
                    CSharpToken.DYNAMIC,
                    CSharpToken.ELSE,
                    CSharpToken.ENUM,
                    CSharpToken.EVENT,
                    CSharpToken.EXPLICIT,
                    CSharpToken.EXTERN,
                    CSharpToken.FINALLY,
                    CSharpToken.FIXED,
                    CSharpToken.FOR,
                    CSharpToken.FOREACH,
                    CSharpToken.GOTO,
                    CSharpToken.IF,
                    CSharpToken.IMPLICIT,
                    CSharpToken.IN,
                    CSharpToken.INTERFACE,
                    CSharpToken.INTERNAL,
                    CSharpToken.IS,
                    CSharpToken.LOCK,
                    CSharpToken.NAMESPACE,
                    CSharpToken.NEW,
                    CSharpToken.OPERATOR,
                    CSharpToken.OUT,
                    CSharpToken.OVERRIDE,
                    CSharpToken.PARAMS,
                    CSharpToken.PRIVATE,
                    CSharpToken.PROTECTED,
                    CSharpToken.PUBLIC,
                    CSharpToken.READONLY,
                    CSharpToken.REF,
                    CSharpToken.RETURN,
                    CSharpToken.SEALED,
                    CSharpToken.SIZEOF,
                    CSharpToken.STACKALLOC,
                    CSharpToken.STATIC,
                    CSharpToken.STRUCT,
                    CSharpToken.SWITCH,
                    CSharpToken.THIS,
                    CSharpToken.THROW,
                    CSharpToken.TYPEOF,
                    CSharpToken.UNCHECKED,
                    CSharpToken.UNSAFE,
                    CSharpToken.USING,
                    CSharpToken.VAR,
                    CSharpToken.VIRTUAL,
                    CSharpToken.VOID,
                    CSharpToken.VOLATILE,
                    CSharpToken.WHILE -> {
                        val tokenType = TokenType.KEYWORD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.BOOL,
                    CSharpToken.BYTE,
                    CSharpToken.CHAR,
                    CSharpToken.DOUBLE,
                    CSharpToken.FLOAT,
                    CSharpToken.INT,
                    CSharpToken.LONG,
                    CSharpToken.OBJECT,
                    CSharpToken.SBYTE,
                    CSharpToken.SHORT,
                    CSharpToken.STRING,
                    CSharpToken.UINT,
                    CSharpToken.USHORT,
                    CSharpToken.ULONG -> {
                        val tokenType = TokenType.TYPE
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.TRUE,
                    CSharpToken.FALSE,
                    CSharpToken.NULL -> {
                        val tokenType = TokenType.LANG_CONST
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.PREPROCESSOR -> {
                        val tokenType = TokenType.PREPROCESSOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.DOUBLE_QUOTED_STRING,
                    CSharpToken.SINGLE_QUOTED_STRING -> {
                        val tokenType = TokenType.STRING
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.LINE_COMMENT,
                    CSharpToken.BLOCK_COMMENT -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    CSharpToken.IDENTIFIER,
                    CSharpToken.WHITESPACE,
                    CSharpToken.BAD_CHARACTER -> {
                        continue
                    }
                    CSharpToken.EOF -> {
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