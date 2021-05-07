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

package com.blacksquircle.ui.language.xml.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
import com.blacksquircle.ui.language.xml.lexer.XmlLexer
import com.blacksquircle.ui.language.xml.lexer.XmlToken
import java.io.IOException
import java.io.StringReader

class XmlStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "XmlStyler"

        private var xmlStyler: XmlStyler? = null

        fun getInstance(): XmlStyler {
            return xmlStyler ?: XmlStyler().also {
                xmlStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = XmlLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    XmlToken.XML_CHAR_ENTITY_REF,
                    XmlToken.XML_ENTITY_REF_TOKEN -> {
                        val styleSpan = StyleSpan(syntaxScheme.entityRefColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_TAG_NAME -> {
                        val styleSpan = StyleSpan(syntaxScheme.tagNameColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_ATTR_NAME -> {
                        val styleSpan = StyleSpan(syntaxScheme.attrNameColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_DOCTYPE_PUBLIC,
                    XmlToken.XML_DOCTYPE_SYSTEM,
                    XmlToken.XML_DOCTYPE_START,
                    XmlToken.XML_DOCTYPE_END,
                    XmlToken.XML_PI_START,
                    XmlToken.XML_PI_END,
                    XmlToken.XML_PI_TARGET,
                    XmlToken.XML_EMPTY_ELEMENT_END,
                    XmlToken.XML_TAG_END,
                    XmlToken.XML_CDATA_START,
                    XmlToken.XML_CDATA_END,
                    XmlToken.XML_START_TAG_START,
                    XmlToken.XML_END_TAG_START -> {
                        val styleSpan = StyleSpan(syntaxScheme.tagColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_ATTRIBUTE_VALUE_TOKEN,
                    XmlToken.XML_ATTRIBUTE_VALUE_START_DELIMITER,
                    XmlToken.XML_ATTRIBUTE_VALUE_END_DELIMITER -> {
                        val styleSpan = StyleSpan(syntaxScheme.attrValueColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_COMMENT_START,
                    XmlToken.XML_COMMENT_END,
                    XmlToken.XML_CONDITIONAL_COMMENT_START,
                    XmlToken.XML_CONDITIONAL_COMMENT_START_END,
                    XmlToken.XML_CONDITIONAL_COMMENT_END,
                    XmlToken.XML_CONDITIONAL_COMMENT_END_START,
                    XmlToken.XML_COMMENT_CHARACTERS -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_DATA_CHARACTERS,
                    XmlToken.XML_TAG_CHARACTERS,
                    XmlToken.WHITESPACE,
                    XmlToken.BAD_CHARACTER -> {
                        continue
                    }
                    XmlToken.EOF -> {
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