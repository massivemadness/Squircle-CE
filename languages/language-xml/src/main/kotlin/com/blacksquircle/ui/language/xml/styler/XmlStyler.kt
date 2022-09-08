/*
 * Copyright 2022 Squircle CE contributors.
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
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
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

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = XmlLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    XmlToken.XML_CHAR_ENTITY_REF,
                    XmlToken.XML_ENTITY_REF_TOKEN -> {
                        val styleSpan = StyleSpan(scheme.entityRefColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_TAG_NAME -> {
                        val styleSpan = StyleSpan(scheme.tagNameColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_ATTR_NAME -> {
                        val styleSpan = StyleSpan(scheme.attrNameColor)
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
                        val styleSpan = StyleSpan(scheme.tagColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    XmlToken.XML_ATTRIBUTE_VALUE_TOKEN,
                    XmlToken.XML_ATTRIBUTE_VALUE_START_DELIMITER,
                    XmlToken.XML_ATTRIBUTE_VALUE_END_DELIMITER -> {
                        val styleSpan = StyleSpan(scheme.attrValueColor)
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
                        val styleSpan = StyleSpan(scheme.commentColor)
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
}