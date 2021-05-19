/*
 * Copyright 2021 Squircle IDE contributors.
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
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
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

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = HtmlLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    HtmlToken.XML_CHAR_ENTITY_REF,
                    HtmlToken.XML_ENTITY_REF_TOKEN -> {
                        val styleSpan = StyleSpan(syntaxScheme.entityRefColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    HtmlToken.XML_TAG_NAME -> {
                        val styleSpan = StyleSpan(syntaxScheme.tagNameColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    HtmlToken.XML_ATTR_NAME -> {
                        val styleSpan = StyleSpan(syntaxScheme.attrNameColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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
                        val styleSpan = StyleSpan(syntaxScheme.tagColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN,
                    HtmlToken.XML_ATTRIBUTE_VALUE_START_DELIMITER,
                    HtmlToken.XML_ATTRIBUTE_VALUE_END_DELIMITER -> {
                        val styleSpan = StyleSpan(syntaxScheme.attrValueColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    HtmlToken.XML_COMMENT_START,
                    HtmlToken.XML_COMMENT_END,
                    HtmlToken.XML_CONDITIONAL_COMMENT_START,
                    HtmlToken.XML_CONDITIONAL_COMMENT_START_END,
                    HtmlToken.XML_CONDITIONAL_COMMENT_END,
                    HtmlToken.XML_CONDITIONAL_COMMENT_END_START,
                    HtmlToken.XML_COMMENT_CHARACTERS -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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
        return syntaxHighlightSpans
    }

    override fun enqueue(sourceCode: String, syntaxScheme: SyntaxScheme, stylingResult: StylingResult) {
        task?.cancelTask()
        task = StylingTask(
            doAsync = { execute(sourceCode, syntaxScheme) },
            onSuccess = stylingResult
        )
        task?.executeTask()
    }

    override fun cancel() {
        task?.cancelTask()
        task = null
    }
}