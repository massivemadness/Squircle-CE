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

package com.blacksquircle.ui.language.base.utils

import com.blacksquircle.ui.language.base.model.Suggestion
import java.util.regex.Pattern

class WordsManager {

    companion object {
        private const val WORDS_REGEX = "\\w((\\w|-)*(\\w))?"
    }

    private val wordsPattern = Pattern.compile(WORDS_REGEX)
    private val lineMap = hashMapOf<Int, MutableList<Suggestion>>()

    fun getWords(): Set<Suggestion> {
        val wordsSet = hashSetOf<Suggestion>()
        for (line in lineMap.values) {
            for (word in line) {
                wordsSet.add(word)
            }
        }
        return wordsSet
    }

    fun processLine(lineNumber: Int, text: String) {
        lineMap[lineNumber]?.clear()
        val matcher = wordsPattern.matcher(text)
        while (matcher.find()) {
            val word = Suggestion(
                type = Suggestion.Type.WORD,
                text = text.substring(matcher.start(), matcher.end()),
                returnType = ""
            )
            if (lineMap.containsKey(lineNumber)) {
                lineMap[lineNumber]?.add(word)
            } else {
                lineMap[lineNumber] = mutableListOf(word)
            }
        }
    }

    fun deleteLine(lineNumber: Int) {
        lineMap.remove(lineNumber)
    }

    fun clearLines() {
        lineMap.clear()
    }
}