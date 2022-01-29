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

package com.blacksquircle.ui.language.lua.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
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

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = LuaLexer(sourceReader)

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
                    LuaToken.LONG_LITERAL,
                    LuaToken.INTEGER_LITERAL,
                    LuaToken.FLOAT_LITERAL,
                    LuaToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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
                        val styleSpan = StyleSpan(scheme.methodColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    LuaToken.TRUE,
                    LuaToken.FALSE,
                    LuaToken.NULL -> {
                        val styleSpan = StyleSpan(scheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    LuaToken.DOUBLE_QUOTED_STRING,
                    LuaToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(scheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    LuaToken.LINE_COMMENT,
                    LuaToken.BLOCK_COMMENT -> {
                        val styleSpan = StyleSpan(scheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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
        return syntaxHighlightSpans
    }
}