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

package com.blacksquircle.ui.language.html.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.html.lexer.HtmlLexer
import com.blacksquircle.ui.language.html.lexer.HtmlToken
import java.io.IOException
import java.io.StringReader

class HtmlStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "HtmlStyler"

        private var htmlStyler: HtmlStyler? = null

        fun getInstance(): HtmlStyler {
            return htmlStyler ?: HtmlStyler().also {
                htmlStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = HtmlLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    HtmlToken.XML_CHAR_ENTITY_REF,
                    HtmlToken.XML_ENTITY_REF_TOKEN -> {
                        val tokenType = TokenType.ENTITY_REF
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    HtmlToken.XML_TAG_NAME -> {
                        val tokenType = TokenType.TAG_NAME
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    HtmlToken.XML_ATTR_NAME -> {
                        val tokenType = TokenType.ATTR_NAME
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    HtmlToken.XML_DOCTYPE_PUBLIC,
                    HtmlToken.XML_DOCTYPE_START,
                    HtmlToken.XML_DOCTYPE_END,
                    HtmlToken.XML_PI_START,
                    HtmlToken.XML_PI_END,
                    HtmlToken.XML_PI_TARGET,
                    HtmlToken.XML_EMPTY_ELEMENT_END,
                    HtmlToken.XML_TAG_END,
                    HtmlToken.XML_START_TAG_START,
                    HtmlToken.XML_END_TAG_START -> {
                        val tokenType = TokenType.TAG
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN,
                    HtmlToken.XML_ATTRIBUTE_VALUE_START_DELIMITER,
                    HtmlToken.XML_ATTRIBUTE_VALUE_END_DELIMITER -> {
                        val tokenType = TokenType.ATTR_VALUE
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    HtmlToken.XML_COMMENT_START,
                    HtmlToken.XML_COMMENT_END,
                    HtmlToken.XML_CONDITIONAL_COMMENT_START,
                    HtmlToken.XML_CONDITIONAL_COMMENT_START_END,
                    HtmlToken.XML_CONDITIONAL_COMMENT_END,
                    HtmlToken.XML_CONDITIONAL_COMMENT_END_START,
                    HtmlToken.XML_COMMENT_CHARACTERS -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    HtmlToken.XML_DATA_CHARACTERS,
                    HtmlToken.XML_TAG_CHARACTERS,
                    HtmlToken.WHITESPACE,
                    HtmlToken.BAD_CHARACTER -> {
                        continue
                    }
                    HtmlToken.EOF -> {
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