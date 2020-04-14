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

package com.lightteam.language.internal

class StringStream(val source: String) {

    var caseSensitive = true
    var position = 0
        get() {
            if (field < eof()) {
                return field
            }
            return eof()
        }

    fun reset() {
        position = 0
    }

    fun advance(): Char {
        position++
        if (position < eof()) {
            return source[position]
        }
        return Char.MAX_VALUE
    }

    fun advance(index: Int): Char {
        position += index
        if (position < eof()) {
            return source[position]
        }
        return Char.MAX_VALUE
    }

    fun atEndOfStream(): Boolean {
        return position == eof()
    }

    fun eof(): Int {
        return source.length
    }

    fun findNext(char: Char): Int {
        if (caseSensitive) {
            while (position < eof() && source[position] != char) {
                position++
            }
        } else {
            while (position < eof() && !caseInsensitiveEquals(source[position], char)) {
                position++
            }
        }
        return if (position >= eof()) eof() else position
    }

    fun findNext(str: String): Int {
        val charArray = str.toCharArray()
        val length = charArray.size
        val eof = eof() - length
        while (position <= eof) {
            var newIndex = 0
            while (true) {
                if (newIndex >= length) {
                    break
                }
                if (position + newIndex >= 0) {
                    val charAt = source[position + newIndex]
                    if (caseSensitive && charAt != charArray[newIndex]) {
                        break
                    }
                    if (!caseSensitive) {
                        if (!caseInsensitiveEquals(charAt, charArray[newIndex])) {
                            break
                        }
                    }
                    if (newIndex == length - 1) {
                        position += length
                        return position
                    }
                }
                newIndex++
            }
            position++
        }
        position = eof()
        return eof()
    }

    fun goBack(): Char {
        if (position > 0) {
            position--
        }
        return source[position]
    }

    fun goBack(index: Int): Char {
        if (index > position) {
            position = 0
            return source[position]
        }
        position -= index
        if (position < eof()) {
            source[position]
        }
        return Char.MAX_VALUE
    }

    fun gotoEnd() {
        position = eof() - 1
    }

    fun gotoStart() {
        position = 0
    }

    fun match(str: String): Boolean {
        return match(str.toCharArray())
    }

    fun match(charArray: CharArray): Boolean {
        val length = charArray.size
        if (position + length > eof()) {
            return false
        }
        for (index in 0 until length) {
            if (position + index >= 0) {
                val charAt = source[position + index]
                if (caseSensitive) {
                    if (charAt != charArray[index]) {
                        return false
                    }
                }
                if (caseSensitive) {
                    continue
                } else if (!caseInsensitiveEquals(charAt, charArray[index])) {
                    return false
                }
            }
        }
        position += charArray.size
        return true
    }

    fun match(char: Char): Boolean {
        if (position >= source.length) {
            return false
        }
        val charAt = source[position]
        return if (caseSensitive && charAt == char) {
            position++
            true
        } else if (caseSensitive || !caseInsensitiveEquals(charAt, char)) {
            false
        } else {
            position++
            true
        }
    }

    fun peek(): Char {
        if (position >= eof() || position < 0) {
            return Char.MAX_VALUE
        }
        return source[position]
    }

    fun peek(index: Int): Char {
        if (position + index >= eof() || position + index < 0) {
            return Char.MAX_VALUE
        }
        return source[position + index]
    }

    fun skipWhiteSpace() {
        while (source[position].isWhitespace()) {
            position++
        }
    }

    private fun caseInsensitiveEquals(first: Char, second: Char): Boolean {
        return first.toLowerCase() == second.toLowerCase()
    }
}