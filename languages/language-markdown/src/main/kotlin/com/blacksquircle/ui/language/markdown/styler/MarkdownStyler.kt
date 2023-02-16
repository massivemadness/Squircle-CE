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

package com.blacksquircle.ui.language.markdown.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.markdown.lexer.MarkdownLexer
import com.blacksquircle.ui.language.markdown.lexer.MarkdownToken
import java.io.IOException
import java.io.StringReader

class MarkdownStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "MarkdownStyler"

        private var markdownStyler: MarkdownStyler? = null

        fun getInstance(): MarkdownStyler {
            return markdownStyler ?: MarkdownStyler().also {
                markdownStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = MarkdownLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    MarkdownToken.HEADER -> {
                        val tokenType = TokenType.TAG_NAME
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.UNORDERED_LIST_ITEM,
                    MarkdownToken.ORDERED_LIST_ITEM -> {
                        val tokenType = TokenType.ATTR_VALUE
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.BOLDITALIC1,
                    MarkdownToken.BOLDITALIC2 -> {
                        val tokenType = TokenType.ATTR_NAME
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.BOLD1,
                    MarkdownToken.BOLD2 -> {
                        val tokenType = TokenType.ATTR_NAME
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.ITALIC1,
                    MarkdownToken.ITALIC2 -> {
                        val tokenType = TokenType.ATTR_NAME
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.STRIKETHROUGH -> {
                        val tokenType = TokenType.ATTR_NAME
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.CODE,
                    MarkdownToken.CODE_BLOCK -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.LT,
                    MarkdownToken.GT,
                    MarkdownToken.EQ,
                    MarkdownToken.NOT,
                    MarkdownToken.DIV,
                    MarkdownToken.MINUS,
                    MarkdownToken.LPAREN,
                    MarkdownToken.RPAREN,
                    MarkdownToken.LBRACE,
                    MarkdownToken.RBRACE,
                    MarkdownToken.LBRACK,
                    MarkdownToken.RBRACK -> {
                        val tokenType = TokenType.TAG
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.URL -> {
                        val tokenType = TokenType.ATTR_VALUE
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    MarkdownToken.IDENTIFIER,
                    MarkdownToken.WHITESPACE,
                    MarkdownToken.BAD_CHARACTER -> {
                        continue
                    }
                    MarkdownToken.EOF -> {
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