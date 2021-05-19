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

package com.blacksquircle.ui.editorkit.internal

import android.content.Context
import android.util.AttributeSet
import com.blacksquircle.ui.editorkit.R

abstract class AutoIndentEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : SyntaxHighlightEditText(context, attrs, defStyleAttr) {

    private var newText = ""
    private var isAutoIndenting = false

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        newText = text?.subSequence(start, start + count).toString()
        completeIndentation(start, count)
        super.doOnTextChanged(text, start, before, count)
        newText = ""
    }

    fun tab(): String {
        return if (editorConfig.useSpacesInsteadOfTabs) {
            " ".repeat(editorConfig.tabWidth)
        } else "\t"
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
                result[2] ?: ""
            } else {
                return
            }
            val newCursorPosition = if (result[3] != null) {
                result[3]!!.toInt()
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
        if (newText == "\n" && editorConfig.autoIndentation) {
            val prevLineIndentation = getIndentationForOffset(start)
            val indentation = StringBuilder(prevLineIndentation)
            var newCursorPosition = indentation.length + start + 1
            if (start > 0 && text[start - 1] == '{') {
                indentation.append(tab())
                newCursorPosition = indentation.length + start + 1
            }
            if (start + 1 < text.length && text[start + 1] == '}') {
                indentation.append("\n").append(prevLineIndentation)
            }
            strArr = arrayOfNulls(4)
            strArr[1] = indentation.toString()
            strArr[3] = newCursorPosition.toString()
            return strArr
        } else if (newText == "\"" && editorConfig.autoCloseQuotes) {
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
        } else if (newText == "'" && editorConfig.autoCloseQuotes) {
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
        } else if (newText == "{" && editorConfig.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "}"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "}" && editorConfig.autoCloseBrackets) {
            if (start + 1 < text.length && text[start + 1] == '}') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "(" && editorConfig.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = ")"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == ")" && editorConfig.autoCloseBrackets) {
            if (start + 1 < text.length && text[start + 1] == ')') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "[" && editorConfig.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "]"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "]" && editorConfig.autoCloseBrackets &&
            start + 1 < text.length && text[start + 1] == ']') {
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