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

package com.lightteam.editorkit.feature.suggestions

import com.lightteam.editorkit.feature.linenumbers.Line
import java.util.*
import kotlin.collections.HashMap

class WordsList {

    val words: SortedArrayList<Word> = SortedArrayList()

    private val lineMap: HashMap<Line, SortedArrayList<Word>> = HashMap()

    fun addWord(word: CharSequence, line: Line) {
        var findWord = findWord(word)
        if (findWord == null) {
            findWord = Word(word, line)
            words.add(findWord)
        } else {
            findWord.addLine(line)
        }
        putLineMap(findWord, line)
    }

    fun findWord(word: CharSequence): Word? {
        val binarySearch = Collections.binarySearch(words, Word(word, null))
        return if (binarySearch >= 0) {
            words[binarySearch]
        } else {
            null
        }
    }

    fun clearLine(line: Line) {
        val wordsList = lineMap[line]
        if (wordsList != null) {
            val iterator = wordsList.iterator()
            while (iterator.hasNext()) {
                val word = iterator.next()
                if (word.removeLine(line)) {
                    words.remove(word)
                }
            }
            wordsList.clear()
        }
    }

    fun deleteLine(line: Line) {
        clearLine(line)
        lineMap.remove(line)
    }

    private fun putLineMap(word: Word, line: Line) {
        if (lineMap.containsKey(line)) {
            lineMap[line]?.add(word)
            return
        }
        val wordsList = SortedArrayList<Word>()
        wordsList.add(word)
        lineMap[line] = wordsList
    }

    class Word(val value: CharSequence, line: Line?) : Comparable<Word>, CharSequence {

        override val length: Int = value.length

        private val lines: SortedArrayList<Line> = SortedArrayList()

        init {
            addLine(line)
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return value.subSequence(startIndex, endIndex)
        }

        override operator fun get(index: Int): Char {
            return value[index]
        }

        override operator fun compareTo(other: Word): Int {
            val compareToIgnoreCase = compareToIgnoreCase(other.value)
            return if (compareToIgnoreCase == 0) {
                compareCharSequenceTo(other.value)
            } else {
                compareToIgnoreCase
            }
        }

        fun addLine(line: Line?) {
            if (line != null) {
                lines.add(line)
            }
        }

        fun removeLine(line: Line): Boolean {
            lines.remove(line)
            return lines.isEmpty()
        }

        private fun foldCase(char: Char): Char {
            return if (char < 128.toChar()) {
                if ('A' > char || char > 'Z') char else (char + ' '.toInt())
            } else {
                char.toUpperCase().toLowerCase()
            }
        }

        private fun compareCharSequenceTo(word: CharSequence): Int {
            val first = value.length
            val second = word.length
            val length = if (first < second) first else second
            for (index in 0 until length) {
                val char = value[index] - word[index]
                if (char != 0) {
                    return char
                }
            }
            return first - second
        }

        private fun compareToIgnoreCase(word: CharSequence): Int {
            val first = value.length
            val second = word.length
            val length = if (first < second) first else second
            for (index in 0 until length) {
                val firstChar = value[index]
                val secondChar = word[index]
                if (firstChar != secondChar) {
                    val foldCase = foldCase(firstChar) - foldCase(secondChar)
                    if (foldCase != 0) {
                        return foldCase
                    }
                }
            }
            return first - second
        }
    }
}