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

package com.blacksquircle.ui.language.markdown.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.StylingResult
import com.blacksquircle.ui.language.base.utils.StylingTask
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

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = MarkdownLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    MarkdownToken.HEADER -> {
                        val styleSpan = StyleSpan(syntaxScheme.tagNameColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    MarkdownToken.UNORDERED_LIST_ITEM,
                    MarkdownToken.ORDERED_LIST_ITEM -> {
                        val styleSpan = StyleSpan(syntaxScheme.attrValueColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    MarkdownToken.BOLDITALIC1,
                    MarkdownToken.BOLDITALIC2 -> {
                        val styleSpan = StyleSpan(
                            color = syntaxScheme.attrNameColor,
                            bold = true,
                            italic = true
                        )
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    MarkdownToken.BOLD1,
                    MarkdownToken.BOLD2 -> {
                        val styleSpan = StyleSpan(
                            color = syntaxScheme.attrNameColor,
                            bold = true
                        )
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    MarkdownToken.ITALIC1,
                    MarkdownToken.ITALIC2 -> {
                        val styleSpan = StyleSpan(
                            color = syntaxScheme.attrNameColor,
                            italic = true
                        )
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    MarkdownToken.STRIKETHROUGH -> {
                        val styleSpan = StyleSpan(
                            color = syntaxScheme.attrNameColor,
                            strikethrough = true
                        )
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    MarkdownToken.CODE,
                    MarkdownToken.CODE_BLOCK -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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
                        val styleSpan = StyleSpan(syntaxScheme.tagColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    MarkdownToken.URL -> {
                        val styleSpan = StyleSpan(
                            color = syntaxScheme.attrValueColor,
                            underline = true
                        )
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
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