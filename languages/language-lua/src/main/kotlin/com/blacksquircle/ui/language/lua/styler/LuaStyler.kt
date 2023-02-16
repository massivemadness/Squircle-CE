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

package com.blacksquircle.ui.language.lua.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.lua.lexer.LuaLexer
import com.blacksquircle.ui.language.lua.lexer.LuaToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class LuaStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "LuaStyler"

        private val METHOD = Pattern.compile("(?<=(function)) (\\w+)")

        private var luaStyler: LuaStyler? = null

        fun getInstance(): LuaStyler {
            return luaStyler ?: LuaStyler().also {
                luaStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = LuaLexer(sourceReader)

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
                    LuaToken.LONG_LITERAL,
                    LuaToken.INTEGER_LITERAL,
                    LuaToken.FLOAT_LITERAL,
                    LuaToken.DOUBLE_LITERAL -> {
                        val tokenType = TokenType.NUMBER
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LuaToken.LT,
                    LuaToken.GT,
                    LuaToken.LTEQ,
                    LuaToken.GTEQ,
                    LuaToken.EQEQ,
                    LuaToken.TILDEEQ,
                    LuaToken.CONCAT,
                    LuaToken.EQ,
                    LuaToken.NOT_OPERATOR,
                    LuaToken.TILDE,
                    LuaToken.COLON,
                    LuaToken.PLUS,
                    LuaToken.MINUS,
                    LuaToken.MULT,
                    LuaToken.DIV,
                    LuaToken.OR_OPERATOR,
                    LuaToken.XOR,
                    LuaToken.MOD,
                    LuaToken.QUEST,
                    LuaToken.LPAREN,
                    LuaToken.RPAREN,
                    LuaToken.LBRACE,
                    LuaToken.RBRACE,
                    LuaToken.LBRACK,
                    LuaToken.RBRACK -> {
                        val tokenType = TokenType.OPERATOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LuaToken.SEMICOLON,
                    LuaToken.COMMA,
                    LuaToken.DOT -> {
                        continue // skip
                    }
                    LuaToken.BREAK,
                    LuaToken.DO,
                    LuaToken.ELSE,
                    LuaToken.ELSEIF,
                    LuaToken.END,
                    LuaToken.FOR,
                    LuaToken.FUNCTION,
                    LuaToken.GOTO,
                    LuaToken.IF,
                    LuaToken.IN,
                    LuaToken.LOCAL,
                    LuaToken.NIL,
                    LuaToken.REPEAT,
                    LuaToken.RETURN,
                    LuaToken.THEN,
                    LuaToken.UNTIL,
                    LuaToken.WHILE,
                    LuaToken.AND,
                    LuaToken.OR,
                    LuaToken.NOT -> {
                        val tokenType = TokenType.KEYWORD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LuaToken._G,
                    LuaToken._VERSION,
                    LuaToken.ASSERT,
                    LuaToken.COLLECTGARBAGE,
                    LuaToken.DOFILE,
                    LuaToken.ERROR,
                    LuaToken.GETFENV,
                    LuaToken.GETMETATABLE,
                    LuaToken.IPAIRS,
                    LuaToken.LOAD,
                    LuaToken.LOADFILE,
                    LuaToken.LOADSTRING,
                    LuaToken.MODULE,
                    LuaToken.NEXT,
                    LuaToken.PAIRS,
                    LuaToken.PCALL,
                    LuaToken.PRINT,
                    LuaToken.RAWEQUAL,
                    LuaToken.RAWGET,
                    LuaToken.RAWSET,
                    LuaToken.REQUIRE,
                    LuaToken.SELECT,
                    LuaToken.SETFENV,
                    LuaToken.SETMETATABLE,
                    LuaToken.TONUMBER,
                    LuaToken.TOSTRING,
                    LuaToken.TYPE,
                    LuaToken.UNPACK,
                    LuaToken.XPCALL -> {
                        val tokenType = TokenType.METHOD
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LuaToken.TRUE,
                    LuaToken.FALSE,
                    LuaToken.NULL -> {
                        val tokenType = TokenType.LANG_CONST
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LuaToken.DOUBLE_QUOTED_STRING,
                    LuaToken.SINGLE_QUOTED_STRING -> {
                        val tokenType = TokenType.STRING
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LuaToken.LINE_COMMENT,
                    LuaToken.BLOCK_COMMENT -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LuaToken.IDENTIFIER,
                    LuaToken.WHITESPACE,
                    LuaToken.BAD_CHARACTER -> {
                        continue
                    }
                    LuaToken.EOF -> {
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