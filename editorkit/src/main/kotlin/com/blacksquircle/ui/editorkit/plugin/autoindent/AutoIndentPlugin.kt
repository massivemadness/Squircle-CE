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

package com.blacksquircle.ui.editorkit.plugin.autoindent

import android.util.Log
import android.widget.EditText
import com.blacksquircle.ui.editorkit.plugin.base.EditorPlugin

class AutoIndentPlugin : EditorPlugin(PLUGIN_ID) {

    var autoIndentLines = true
    var autoCloseBrackets = true
    var autoCloseQuotes = true

    private val editor: EditText
        get() = editText!!

    private var newText = ""
    private var isAutoIndenting = false

    override fun onAttached(editText: EditText) {
        super.onAttached(editText)
        Log.d(PLUGIN_ID, "AutoIndent plugin loaded successfully!")
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(text, start, before, count)
        newText = text?.subSequence(start, start + count).toString()
        completeIndentation(start, count)
        newText = ""
    }

    private fun completeIndentation(start: Int, count: Int) {
        if (!isAutoIndenting) {
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
            editor.post {
                isAutoIndenting = true
                editor.text.replace(start, start + count, replacementValue)
                undoStack.pop()
                val change = undoStack.pop()
                if (replacementValue != "") {
                    change.newText = replacementValue
                    undoStack.push(change)
                }
                editor.setSelection(newCursorPosition)
                isAutoIndenting = false
            }
        }
    }

    private fun executeIndentation(start: Int): Array<String?> {
        val strArr: Array<String?>
        if (newText == "\n" && autoIndentLines) {
            val prevLineIndentation = getIndentationForOffset(start)
            val indentation = StringBuilder(prevLineIndentation)
            var newCursorPosition = indentation.length + start + 1
            if (start > 0 && editor.text[start - 1] == '{') {
                indentation.append("    "/*editor.tab()*/)
                newCursorPosition = indentation.length + start + 1
            }
            if (start + 1 < editor.text.length && editor.text[start + 1] == '}') {
                indentation.append("\n").append(prevLineIndentation)
            }
            strArr = arrayOfNulls(4)
            strArr[1] = indentation.toString()
            strArr[3] = newCursorPosition.toString()
            return strArr
        } else if (newText == "\"" && autoCloseQuotes) {
            if (start + 1 >= editor.text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "\""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (editor.text[start + 1] == '\"' && editor.text[start - 1] != '\\') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (!(editor.text[start + 1] == '\"' && editor.text[start - 1] == '\\')) {
                strArr = arrayOfNulls(4)
                strArr[1] = "\""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "'" && autoCloseQuotes) {
            if (start + 1 >= editor.text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (start + 1 >= editor.text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (editor.text[start + 1] == '\'' && start > 0 && editor.text[start - 1] != '\\') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (!(editor.text[start + 1] == '\'' && start > 0 && editor.text[start - 1] == '\\')) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "{" && autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "}"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "}" && autoCloseBrackets) {
            if (start + 1 < editor.text.length && editor.text[start + 1] == '}') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "(" && autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = ")"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == ")" && autoCloseBrackets) {
            if (start + 1 < editor.text.length && editor.text[start + 1] == ')') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "[" && autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "]"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "]" && autoCloseBrackets &&
            start + 1 < editor.text.length && editor.text[start + 1] == ']') {
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
        while (i < editor.text.length) {
            val char = editor.text[i]
            if (!char.isWhitespace() || char == '\n') {
                break
            }
            i++
        }
        return editor.text.subSequence(start, i).toString()
    }

    companion object {
        const val PLUGIN_ID = "autoindent-7401"
    }
}