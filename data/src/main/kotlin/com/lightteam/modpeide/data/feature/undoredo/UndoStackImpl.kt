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

package com.lightteam.modpeide.data.feature.undoredo

import com.lightteam.modpeide.domain.feature.undoredo.UndoStack
import com.lightteam.modpeide.domain.model.editor.TextChange

class UndoStackImpl : UndoStack {

    companion object {
        const val MAX_SIZE = Integer.MAX_VALUE
    }

    private val stack = mutableListOf<TextChange>()
    private var currentSize = 0

    override fun pop(): TextChange {
        val size = stack.size
        val item = stack[size - 1]
        stack.removeAt(size - 1)
        currentSize -= item.newText.length + item.oldText.length
        return item
    }

    override fun push(textChange: TextChange) {
        val delta = textChange.newText.length + textChange.oldText.length
        if (delta < MAX_SIZE) {
            if (stack.size > 0) {
                val previous = stack[stack.size - 1]
                val toCharArray: CharArray
                val length: Int
                var allWhitespace: Boolean
                var allLettersDigits: Boolean
                var i = 0
                if (textChange.oldText.isEmpty()
                    && textChange.newText.length == 1
                    && previous.oldText.isEmpty()) {
                    if (previous.start + previous.newText.length != textChange.start) {
                        stack.add(textChange)
                    } else if (textChange.newText[0].isWhitespace()) {
                        allWhitespace = true
                        toCharArray = previous.newText.toCharArray()
                        length = toCharArray.size
                        while (i < length) {
                            if (!toCharArray[i].isWhitespace()) {
                                allWhitespace = false
                            }
                            i++
                        }
                        if (allWhitespace) {
                            previous.newText += textChange.newText
                        } else {
                            stack.add(textChange)
                        }
                    } else if (textChange.newText[0].isLetterOrDigit()) {
                        allLettersDigits = true
                        toCharArray = previous.newText.toCharArray()
                        length = toCharArray.size
                        while (i < length) {
                            if (!toCharArray[i].isLetterOrDigit()) {
                                allLettersDigits = false
                            }
                            i++
                        }
                        if (allLettersDigits) {
                            previous.newText += textChange.newText
                        } else {
                            stack.add(textChange)
                        }
                    } else {
                        stack.add(textChange)
                    }
                } else if (textChange.oldText.length != 1
                    || textChange.newText.isNotEmpty()
                    || previous.newText.isNotEmpty()) {
                    stack.add(textChange)
                } else if (previous.start - 1 != textChange.start) {
                    stack.add(textChange)
                } else if (textChange.oldText[0].isWhitespace()) {
                    allWhitespace = true
                    toCharArray = previous.oldText.toCharArray()
                    length = toCharArray.size
                    while (i < length) {
                        if (!toCharArray[i].isWhitespace()) {
                            allWhitespace = false
                        }
                        i++
                    }
                    if (allWhitespace) {
                        previous.oldText = textChange.oldText + previous.oldText
                        previous.start -= textChange.oldText.length
                    } else {
                        stack.add(textChange)
                    }
                } else if (textChange.oldText[0].isLetterOrDigit()) {
                    allLettersDigits = true
                    toCharArray = previous.oldText.toCharArray()
                    length = toCharArray.size
                    while (i < length) {
                        if (!toCharArray[i].isLetterOrDigit()) {
                            allLettersDigits = false
                        }
                        i++
                    }
                    if (allLettersDigits) {
                        previous.oldText = textChange.oldText + previous.oldText
                        previous.start -= textChange.oldText.length
                    } else {
                        stack.add(textChange)
                    }
                } else {
                    stack.add(textChange)
                }
            } else {
                stack.add(textChange)
            }
            currentSize += delta
            while (currentSize > MAX_SIZE) {
                if (!removeLast()) {
                    return
                }
            }
            return
        }
        removeAll()
    }

    override fun getItemAt(index: Int): TextChange {
        return stack[index]
    }

    override fun removeAll(): Boolean {
        currentSize = 0
        return stack.removeAll(stack)
    }

    override fun mergeTop(): Boolean {
        if (stack.size >= 2) {
            val newer = stack[stack.size - 1]
            val previous = stack[stack.size - 2]
            if (previous.start + previous.newText.length == newer.start) {
                previous.newText += newer.newText
                previous.oldText += newer.oldText
                stack.remove(newer)
                return true
            }
        }
        return false
    }

    override fun canUndo(): Boolean {
        return stack.size > 0
    }

    override fun count(): Int {
        return stack.size
    }

    override fun clear() {
        return stack.clear()
    }

    private fun removeLast(): Boolean {
        if (stack.size <= 0) {
            return false
        }
        val item = stack[0]
        stack.removeAt(0)
        currentSize -= item.newText.length + item.oldText.length
        return true
    }
}