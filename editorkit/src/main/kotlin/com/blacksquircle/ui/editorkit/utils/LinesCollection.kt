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

package com.blacksquircle.ui.editorkit.utils

class LinesCollection : Iterable<LinesCollection.Line> {

    val lineCount: Int
        get() = lines.size

    private val lines = mutableListOf(Line(0))

    fun add(line: Int, index: Int) {
        lines.add(line, Line(index))
    }

    fun remove(line: Int) {
        if (line != 0) {
            lines.removeAt(line)
        }
    }

    fun clear() {
        lines.clear()
        lines.add(Line(0))
    }

    fun shiftIndexes(fromLine: Int, shiftBy: Int) {
        if (fromLine <= 0) {
            return
        }
        if (fromLine < lineCount) {
            var i = fromLine
            while (i < lineCount) {
                val newIndex = getIndexForLine(i) + shiftBy
                if (i <= 0 || newIndex > 0) {
                    lines[i].start = newIndex
                } else {
                    remove(i)
                    i--
                }
                i++
            }
        }
    }

    fun getIndexForLine(line: Int): Int {
        return if (line >= lineCount) {
            -1
        } else {
            lines[line].start
        }
    }

    fun getLineForIndex(index: Int): Int {
        var first = 0
        var last = lineCount - 1
        while (first < last) {
            val mid = (first + last) / 2
            if (index < getIndexForLine(mid)) {
                last = mid
            } else if (index <= getIndexForLine(mid) || index < getIndexForLine(mid + 1)) {
                return mid
            } else {
                first = mid + 1
            }
        }
        return lineCount - 1
    }

    fun getLine(line: Int): Line {
        if (line > -1 && line < lineCount) {
            return lines[line]
        }
        return Line(0)
    }

    override fun iterator(): Iterator<Line> {
        return lines.iterator()
    }

    data class Line(var start: Int) : Comparable<Line> {

        override fun compareTo(other: Line): Int {
            return start - other.start
        }
    }
}