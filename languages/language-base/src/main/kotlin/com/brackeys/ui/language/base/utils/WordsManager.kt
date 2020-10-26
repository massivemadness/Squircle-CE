/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.language.base.utils

import com.brackeys.ui.language.base.model.Word
import java.util.regex.Pattern

class WordsManager {

    companion object {
        private const val WORDS_REGEX = "\\w((\\w|-)*(\\w))?"
    }

    private val wordsPattern = Pattern.compile(WORDS_REGEX)
    private val lineMap = hashMapOf<Int, MutableList<Word>>()

    fun getWords(): Set<Word> {
        val wordsSet = hashSetOf<Word>()
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
            val word = Word(text.substring(matcher.start(), matcher.end()))
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