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

import android.graphics.Color
import com.lightteam.language.internal.StringStream
import com.lightteam.language.scheme.ColorScheme
import com.lightteam.language.styler.LanguageStyler
import com.lightteam.language.styler.span.StyleSpan
import com.lightteam.language.styler.span.SyntaxHighlightSpan
import io.reactivex.Single
import java.util.regex.Pattern

class JavaScriptStyler : LanguageStyler() {

    companion object {
        //private const val TAG = "JavaScriptStyler"

//        private val KEYWORDS_OLD = Pattern.compile(
//            "(?<=\\b)((break)|(continue)|(else)|(for)|(function)|(if)|(in)|(new)" +
//                    "|(this)|(var)|(while)|(return)|(case)|(catch)|(of)|(typeof)" +
//                    "|(const)|(default)|(do)|(switch)|(try)|(null)|(true)" +
//                    "|(false)|(eval)|(let))(?=\\b)")
//        private val NUMBERS_OLD = Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)")
//        private val SYMBOLS_OLD = Pattern.compile("([!+\\-*<>=?|:%&])")
//        private val BRACKETS_OLD = Pattern.compile("([(){}\\[\\]])")
//        private val METHODS_OLD = Pattern.compile("(?<=(function) )(\\w+)", Pattern.CASE_INSENSITIVE)
//        private val STRINGS_OLD = Pattern.compile("\"(.*?)\"|'(.*?)'")
//        private val COMMENTS_OLD = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")

        private val KEYWORD = Pattern.compile(
            "\\b(function|prototype|debugger|super|this|const|export|extends|final" +
                    "|implements|native|private|protected|public|static|synchronized|throws" +
                    "|transient|volatile|delete|in|instanceof|new|typeof|with|break|case|catch" +
                    "|continue|default|do|else|finally|for|goto|if|import|package|return|switch" +
                    "|throw|try|while)\\b")
        private val TYPE = Pattern.compile(
            "\\b(boolean|byte|char|class|double|enum|float|int" +
                    "|interface|long|short|var|void)\\b")
        private val OPERATOR = Pattern.compile(
            "!|\\$|%|&|\\*|\\-|\\+|~|=|<|>|\\?|\\:|\\^|\\b(delete" +
                    "|in|instanceof|new|typeof|void|with)\\b")
        private val NUMBER = Pattern.compile("\\b((0([xX])[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?))\\b")
        private val LANG_CONST = Pattern.compile("\\b(true|false|null|NaN|Infinity|undefined)\\b")
        private val METHOD = Pattern.compile("(?<=(function) )(\\w+)", Pattern.CASE_INSENSITIVE)
        private val STRING = Pattern.compile("\"(.*?)\"|'(.*?)'")
        private val COMMENT = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
    }

    //private var syntaxHighlightSpans: MutableList<SyntaxHighlightSpan> = mutableListOf()
    //private var cancelled = false
    //private var hasParsed = false
    //private var parseEnd = 0

    override fun parse() {
        var matcher = KEYWORD.matcher(stringStream.source)
        matcher.region(stringStream.position, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.keywordsColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = TYPE.matcher(stringStream.source)
        matcher.region(stringStream.position, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.keywordsColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = OPERATOR.matcher(stringStream.source)
        matcher.region(stringStream.position, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.keywordsColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = NUMBER.matcher(stringStream.source)
        matcher.region(stringStream.position, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.numbersColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = LANG_CONST.matcher(stringStream.source)
        matcher.region(stringStream.position, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.keywordsColor))
            addSpan(span, matcher.start(), matcher.end())
        }
    }

    /*private fun addSpan(styleSpan: StyleSpan, start: Int, end: Int) {
        if (end > start && end >= 0 && start >= 0
            && start <= stringStream.eof() && end <= stringStream.eof()) {
            val span = SyntaxHighlightSpan(styleSpan, start, end)
            syntaxHighlightSpans.add(span)
        }
    }*/

    /*private fun addSpans(spans: List<SyntaxHighlightSpan>) {
        syntaxHighlightSpans.addAll(spans)
    }*/
}