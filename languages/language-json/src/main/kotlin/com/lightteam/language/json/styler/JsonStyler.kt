/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.language.json.styler

import android.util.Log
import com.lightteam.language.base.model.SyntaxScheme
import com.lightteam.language.base.styler.LanguageStyler
import com.lightteam.language.base.styler.span.StyleSpan
import com.lightteam.language.base.styler.span.SyntaxHighlightSpan
import com.lightteam.language.base.styler.task.StylingTask
import com.lightteam.language.base.styler.utils.StylingResult
import com.lightteam.language.json.styler.lexer.JsonLexer
import com.lightteam.language.json.styler.lexer.JsonToken
import java.io.IOException
import java.io.StringReader

class JsonStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "JsonStyler"

        private var jsonStyler: JsonStyler? = null

        fun getInstance(): JsonStyler {
            return jsonStyler ?: JsonStyler().also {
                jsonStyler = it
            }
        }
    }

    private var task: StylingTask? = null

    override fun execute(sourceCode: String, syntaxScheme: SyntaxScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(sourceCode)
        val lexer = JsonLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    JsonToken.NUMBER -> {
                        val styleSpan = StyleSpan(syntaxScheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JsonToken.LBRACE,
                    JsonToken.RBRACE,
                    JsonToken.LBRACK,
                    JsonToken.RBRACK,
                    JsonToken.COMMA,
                    JsonToken.COLON -> {
                        val styleSpan = StyleSpan(syntaxScheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JsonToken.TRUE,
                    JsonToken.FALSE,
                    JsonToken.NULL -> {
                        val styleSpan = StyleSpan(syntaxScheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JsonToken.DOUBLE_QUOTED_STRING,
                    JsonToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(syntaxScheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JsonToken.BLOCK_COMMENT,
                    JsonToken.LINE_COMMENT -> {
                        val styleSpan = StyleSpan(syntaxScheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    JsonToken.IDENTIFIER,
                    JsonToken.WHITESPACE,
                    JsonToken.BAD_CHARACTER -> {
                        continue
                    }
                    JsonToken.EOF -> {
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