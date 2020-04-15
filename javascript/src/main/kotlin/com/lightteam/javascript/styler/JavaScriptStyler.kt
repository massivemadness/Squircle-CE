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
import com.lightteam.language.styler.LanguageStyler
import com.lightteam.language.styler.span.StyleSpan
import java.util.regex.Pattern

class JavaScriptStyler : LanguageStyler() {

    companion object {
        private val NUMBER = Pattern.compile("\\b((0([xX])[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?))\\b")
        private val OPERATOR = Pattern.compile("!|\\$|%|&|\\*|-|\\+|~|=|<|>|\\?|:|\\^|\\||\\b")
        private val BRACKET = Pattern.compile("([(){}\\[\\]])")
        private val KEYWORD = Pattern.compile(
            "\\b(function|prototype|debugger|super|this|const|var|let|export|extends|final" +
                    "|implements|native|private|protected|public|static|synchronized|throws" +
                    "|transient|volatile|delete|new|in|instanceof|typeof|of|with|break|case|catch" +
                    "|continue|default|do|else|finally|for|goto|if|import|package|return|switch" +
                    "|throw|try|while)\\b")
        private val TYPE = Pattern.compile(
            "\\b(boolean|byte|char|class|double|enum|float|int|interface|long|short|void)\\b")
        private val LANG_CONST = Pattern.compile("\\b(true|false|null|NaN|Infinity|undefined)\\b")
        private val METHOD = Pattern.compile("(?<=(function) )(\\w+)")
        private val STRING = Pattern.compile("\"(.*?)\"|'(.*?)'")
        private val COMMENT = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
    }

    override fun parse() {
        var matcher = NUMBER.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.numberColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = OPERATOR.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.operatorColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = BRACKET.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.bracketColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = KEYWORD.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.keywordColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = TYPE.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.keywordColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = LANG_CONST.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.keywordColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = METHOD.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.methodColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = STRING.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.stringColor))
            addSpan(span, matcher.start(), matcher.end())
        }
        matcher = COMMENT.matcher(sourceCode)
        matcher.region(parseStart, parseEnd)
        while (matcher.find()) {
            val span = StyleSpan(Color.parseColor(colorScheme.commentColor), italic = true)
            addSpan(span, matcher.start(), matcher.end())
        }
    }
}