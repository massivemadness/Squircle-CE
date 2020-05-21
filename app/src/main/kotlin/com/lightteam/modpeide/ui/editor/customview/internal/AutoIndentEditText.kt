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

package com.lightteam.modpeide.ui.editor.customview.internal

import android.content.Context
import android.util.AttributeSet
import com.lightteam.modpeide.R

open class AutoIndentEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : SyntaxHighlightEditText(context, attrs, defStyleAttr) {

    var isAutoIndenting = false

    private var newText = ""

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        newText = text?.subSequence(start, start + count).toString()
        completeIndentation(start, count)
        super.doOnTextChanged(text, start, before, count)
        newText = ""
    }

    private fun completeIndentation(start: Int, count: Int) {
        if (!isDoingUndoRedo && !isAutoIndenting) {
            val result = executeIndentation(start)
            val replacementValue = if (result[0] != null || result[1] != null) {
                val preText = result[0] ?: ""
                val postText = result[1] ?: ""
                if (preText != "" || postText != "") {
                    preText + newText + postText
                } else {
                    return
                }
            } else if (result[2] != null) {
                result[2]!!
            } else {
                return
            }
            val newCursorPosition = if (result[3] != null) {
                Integer.parseInt(result[3]!!)
            } else {
                start + replacementValue.length
            }
            post {
                isAutoIndenting = true
                text.replace(start, start + count, replacementValue)
                undoStack.pop()
                val change = undoStack.pop()
                if (replacementValue != "") {
                    change.newText = replacementValue
                    undoStack.push(change)
                }
                setSelection(newCursorPosition)
                onUndoRedoChangedListener?.onUndoRedoChanged()
                isAutoIndenting = false
            }
        }
    }

    private fun executeIndentation(start: Int): Array<String?> {
        val strArr: Array<String?>
        if (newText == "\n" && config.autoIndentation) {
            val prevLineIndentation = getIndentationForOffset(start)
            val indentation = StringBuilder(prevLineIndentation)
            var newCursorPosition = indentation.length + start + 1
            if (start > 0 && text[start - 1] == '{') {
                indentation.append("    ") // 4 spaces
                newCursorPosition = indentation.length + start + 1
            }
            if (start + 1 < text.length && text[start + 1] == '}') {
                indentation.append("\n").append(prevLineIndentation)
            }
            strArr = arrayOfNulls(4)
            strArr[1] = indentation.toString()
            strArr[3] = newCursorPosition.toString()
            return strArr
        } else if (newText == "\"" && config.autoCloseQuotes) {
            if (start + 1 >= text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "\""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (text[start + 1] == '\"' && text[start - 1] != '\\') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (!(text[start + 1] == '\"' && text[start - 1] == '\\')) {
                strArr = arrayOfNulls(4)
                strArr[1] = "\""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "'" && config.autoCloseQuotes) {
            if (start + 1 >= text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (start + 1 >= text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (text[start + 1] == '\'' && start > 0 && text[start - 1] != '\\') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (!(text[start + 1] == '\'' && start > 0 && text[start - 1] == '\\')) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "{" && config.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "}"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "}" && config.autoCloseBrackets) {
            if (start + 1 < text.length && text[start + 1] == '}') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "(" && config.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = ")"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == ")" && config.autoCloseBrackets) {
            if (start + 1 < text.length && text[start + 1] == ')') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "[" && config.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "]"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "]" && config.autoCloseBrackets
            && start + 1 < text.length && text[start + 1] == ']') {
            strArr = arrayOfNulls(4)
            strArr[2] = ""
            strArr[3] = (start + 1).toString()
            return strArr
        }
        return arrayOfNulls(4)
    }

    private fun getIndentationForOffset(offset: Int): String {
        return getIndentationForLine(lines.getLineForIndex(offset))
    }

    private fun getIndentationForLine(line: Int): String {
        val realLine = lines.getLine(line)
        val start = realLine.start
        var i = start
        while (i < text.length) {
            val char = text[i]
            if (!char.isWhitespace() || char == '\n') {
                break
            }
            i++
        }
        return text.subSequence(start, i).toString()
    }
}
