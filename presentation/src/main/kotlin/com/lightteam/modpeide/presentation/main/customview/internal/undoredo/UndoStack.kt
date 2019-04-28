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

package com.lightteam.modpeide.presentation.main.customview.internal.undoredo

import java.io.Serializable

class UndoStack : Serializable {

    companion object {
        const val MAX_SIZE = Integer.MAX_VALUE
    }

    data class TextChange(
        var newText: String = "",
        var oldText: String = "",
        var start: Int = 0
    ) : Serializable

    private val stack = mutableListOf<TextChange>()
    private var currentSize = 0

    fun pop(): TextChange? {
        val size = stack.size
        if(size <= 0) {
            return null
        }
        val item = stack[size - 1]
        stack.removeAt(size - 1)
        currentSize -= item.newText.length + item.oldText.length
        return item
    }

    fun push(item: TextChange) {
        val delta = item.newText.length + item.oldText.length
        if(delta < MAX_SIZE) {
            if(stack.size > 0) {
                val previous = stack[stack.size - 1]
                val toCharArray: CharArray
                val length: Int
                var allWhitespace: Boolean
                var allLettersDigits: Boolean
                var i = 0
                if (item.oldText.isEmpty() && item.newText.length == 1 && previous.oldText.isEmpty()) {
                    if (previous.start + previous.newText.length != item.start) {
                        stack.add(item)
                    } else if (Character.isWhitespace(item.newText[0])) {
                        allWhitespace = true
                        toCharArray = previous.newText.toCharArray()
                        length = toCharArray.size
                        while (i < length) {
                            if (!Character.isWhitespace(toCharArray[i])) {
                                allWhitespace = false
                            }
                            i++
                        }
                        if (allWhitespace) {
                            previous.newText += item.newText
                        } else {
                            stack.add(item)
                        }
                    } else if (Character.isLetterOrDigit(item.newText[0])) {
                        allLettersDigits = true
                        toCharArray = previous.newText.toCharArray()
                        length = toCharArray.size
                        while (i < length) {
                            if (!Character.isLetterOrDigit(toCharArray[i])) {
                                allLettersDigits = false
                            }
                            i++
                        }
                        if (allLettersDigits) {
                            previous.newText += item.newText
                        } else {
                            stack.add(item)
                        }
                    } else {
                        stack.add(item)
                    }
                } else if (item.oldText.length != 1 || item.newText.isNotEmpty() || previous.newText.isNotEmpty()) {
                    stack.add(item)
                } else if (previous.start - 1 != item.start) {
                    stack.add(item)
                } else if (Character.isWhitespace(item.oldText[0])) {
                    allWhitespace = true
                    toCharArray = previous.oldText.toCharArray()
                    length = toCharArray.size
                    while (i < length) {
                        if (!Character.isWhitespace(toCharArray[i])) {
                            allWhitespace = false
                        }
                        i++
                    }
                    if (allWhitespace) {
                        previous.oldText = item.oldText + previous.oldText
                        previous.start -= item.oldText.length
                    } else {
                        stack.add(item)
                    }
                } else if(Character.isLetterOrDigit(item.oldText[0])) {
                    allLettersDigits = true
                    toCharArray = previous.oldText.toCharArray()
                    length = toCharArray.size
                    while (i < length) {
                        if (!Character.isLetterOrDigit(toCharArray[i])) {
                            allLettersDigits = false
                        }
                        i++
                    }
                    if (allLettersDigits) {
                        previous.oldText = item.oldText + previous.oldText
                        previous.start -= item.oldText.length
                    } else {
                        stack.add(item)
                    }
                } else {
                    stack.add(item)
                }
            } else {
                stack.add(item)
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

    fun removeAll() {
        stack.removeAll(stack)
        currentSize = 0
    }

    fun mergeTop(): Boolean {
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

    fun canUndo(): Boolean = stack.size > 0
    fun getItemAt(index: Int): TextChange = stack[index]
    fun clear() = stack.clear()

    private fun count(): Int = stack.size

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