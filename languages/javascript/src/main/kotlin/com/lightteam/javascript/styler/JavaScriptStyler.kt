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

package com.lightteam.javascript.styler

import android.os.AsyncTask
import android.util.Log
import com.lightteam.language.scheme.SyntaxScheme
import com.lightteam.language.styler.LanguageStyler
import com.lightteam.language.styler.Styleable
import com.lightteam.language.styler.span.StyleSpan
import com.lightteam.language.styler.span.SyntaxHighlightSpan
import java.util.regex.Pattern

class JavaScriptStyler : LanguageStyler {

    companion object {
        private const val TAG = "JavaScriptStyler"

        private val NUMBER = Pattern.compile("\\b((0([xX])[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?))\\b")
        private val OPERATOR = Pattern.compile("([!$%&*\\-+=<>?:^`/|(){}\\[\\]])")
        private val KEYWORD = Pattern.compile(
            "\\b(function|prototype|debugger|super|this|export|extends|final" +
                    "|implements|native|private|protected|public|static|synchronized|throws" +
                    "|transient|volatile|delete|new|in|instanceof|typeof|of|with|break|case|catch" +
                    "|continue|default|do|else|finally|for|goto|if|import|package|return|switch" +
                    "|throw|try|while)\\b")
        private val TYPE = Pattern.compile(
            "\\b(boolean|byte|char|class|double|enum|float|int|interface|long|short|void|const|var|let)\\b")
        private val LANG_CONST = Pattern.compile("\\b(true|false|null|NaN|Infinity|undefined)\\b")
        private val METHOD = Pattern.compile("(?<=(function) )(\\w+)")
        private val STRING = Pattern.compile("\"(.*?)\"|'(.*?)'")
        private val COMMENT = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
    }

    private lateinit var syntaxHighlightSpans: MutableList<SyntaxHighlightSpan>

    private lateinit var styleable: Styleable
    private lateinit var sourceCode: String
    private lateinit var syntaxScheme: SyntaxScheme

    private var task: StylingTask? = null
    private var cancelled = false

    private var parseStart = 0
    private var parseEnd = 0

    override fun runTask(styleable: Styleable, sourceCode: String, syntaxScheme: SyntaxScheme) {
        task?.cancelTask()
        task = StylingTask()

        this.styleable = styleable
        this.sourceCode = sourceCode
        this.syntaxScheme = syntaxScheme

        parseStart = 0
        parseEnd = sourceCode.length
        try {
            task?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    override fun cancelTask() {
        cancelled = true
        task?.cancelTask()
        task = null
    }

    private fun parse() {
        var matcher = NUMBER.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.numberColor)
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = OPERATOR.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.operatorColor)
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = KEYWORD.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.keywordColor)
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = TYPE.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.typeColor)
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = LANG_CONST.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.langConstColor)
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = METHOD.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.methodColor)
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = STRING.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.stringColor)
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = COMMENT.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(syntaxScheme.commentColor, italic = true)
            addSpan(span, matcher.start(), matcher.end())
        }
    }

    private fun addSpan(styleSpan: StyleSpan, start: Int, end: Int) {
        if (end > start && end >= 0 && start >= 0 &&
            start <= sourceCode.length && end <= sourceCode.length) {
            syntaxHighlightSpans.add(SyntaxHighlightSpan(styleSpan, start, end))
        }
    }

    private inner class StylingTask : AsyncTask<Void, Void, Void>() {

        private var isError = false

        override fun doInBackground(vararg p0: Void?): Void? {
            syntaxHighlightSpans = mutableListOf()
            try {
                parse()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                isError = true
            }
            return null
        }

        override fun onPostExecute(voidR: Void?) {
            if (!isError && !isCancelled && !cancelled) {
                styleable.setSpans(syntaxHighlightSpans)
            }
        }

        fun cancelTask(): Boolean {
            return cancel(true)
        }
    }
}