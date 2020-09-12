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

package com.lightteam.language.javascript.styler

import com.lightteam.language.base.scheme.SyntaxScheme
import com.lightteam.language.base.styler.LanguageStyler
import com.lightteam.language.base.styler.span.StyleSpan
import com.lightteam.language.base.styler.span.SyntaxHighlightSpan
import com.lightteam.language.base.styler.task.StylingTask
import com.lightteam.language.base.styler.utils.Region
import com.lightteam.language.base.styler.utils.Styleable
import com.lightteam.language.base.styler.utils.inRegion
import java.util.regex.Pattern

class JavaScriptStyler : LanguageStyler {

    companion object {
        private val NUMBER = Pattern.compile("\\b((0([xX])[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?))\\b")
        private val OPERATOR = Pattern.compile("[!$%&*\\-+=<>?:^`/|(){}\\[\\]]")
        private val KEYWORD = Pattern.compile(
            "\\b(function|prototype|debugger|super|this|export|extends|final" +
                    "|implements|native|private|protected|public|static|synchronized|throws" +
                    "|transient|volatile|delete|new|in|instanceof|typeof|of|with|break|case|catch" +
                    "|continue|default|do|else|finally|for|goto|if|import|package|return|switch" +
                    "|throw|try|while)\\b")
        private val TYPE = Pattern.compile("\\b(boolean|byte|char|class|double|enum|float|int" +
                "|interface|long|short|void|const|var|let)\\b")
        private val LANG_CONST = Pattern.compile("\\b(true|false|null|NaN|Infinity|undefined)\\b")
        private val METHOD = Pattern.compile("(?<=(function) )(\\w+)")
        private val STRING = Pattern.compile("\"(.*?)\"|'(.*?)'")
        private val COMMENT = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
    }

    private lateinit var sourceCode: String
    private lateinit var syntaxScheme: SyntaxScheme

    private var task: StylingTask? = null
    private var parseStart = 0
    private var parseEnd = 0

    override fun executeTask(sourceCode: String, syntaxScheme: SyntaxScheme, styleable: Styleable) {
        this.sourceCode = sourceCode
        this.syntaxScheme = syntaxScheme

        parseStart = 0
        parseEnd = sourceCode.length

        task?.cancelTask()
        task = StylingTask(
            doAsync = ::parse,
            onSuccess = styleable
        )
        task?.executeTask()
    }

    override fun cancelTask() {
        task?.cancelTask()
        task = null
    }

    override fun parse(): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans: MutableList<SyntaxHighlightSpan> = mutableListOf()
        val stringRegions: MutableList<Region> = mutableListOf()

        var matcher = NUMBER.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val styleSpan = StyleSpan(syntaxScheme.numberColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }
        matcher = OPERATOR.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val styleSpan = StyleSpan(syntaxScheme.operatorColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }
        matcher = KEYWORD.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val styleSpan = StyleSpan(syntaxScheme.keywordColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }
        matcher = TYPE.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val styleSpan = StyleSpan(syntaxScheme.typeColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }
        matcher = LANG_CONST.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val styleSpan = StyleSpan(syntaxScheme.langConstColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }
        matcher = METHOD.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val styleSpan = StyleSpan(syntaxScheme.methodColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, matcher.start(), matcher.end())
            syntaxHighlightSpans.add(syntaxHighlightSpan)
        }
        matcher = STRING.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val styleSpan = StyleSpan(syntaxScheme.stringColor)
            val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, start, end)
            syntaxHighlightSpans.add(syntaxHighlightSpan)
            stringRegions.add(start to end)
        }
        matcher = COMMENT.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val styleSpan = StyleSpan(syntaxScheme.commentColor, italic = true)
            if (!stringRegions.inRegion(start, end)) {
                val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, start, end)
                syntaxHighlightSpans.add(syntaxHighlightSpan)
            }
        }

        return syntaxHighlightSpans
    }
}