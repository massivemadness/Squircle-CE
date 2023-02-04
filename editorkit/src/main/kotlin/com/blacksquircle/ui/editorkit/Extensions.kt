/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.editorkit

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.EditText
import androidx.core.content.getSystemService
import com.blacksquircle.ui.editorkit.exception.LineException
import com.blacksquircle.ui.editorkit.widget.TextProcessor

private const val LABEL_CUT = "CUT"
private const val LABEL_COPY = "COPY"

val TextProcessor.selectedText: CharSequence
    get() = text.subSequence(selectionStart, selectionEnd)

fun TextProcessor.insert(delta: CharSequence) {
    text.replace(selectionStart, selectionEnd, delta)
}

fun TextProcessor.cut() {
    val clipboardManager = context.getSystemService<ClipboardManager>()
    val clipData = ClipData.newPlainText(LABEL_CUT, selectedText)
    clipboardManager?.setPrimaryClip(clipData)
    text.replace(selectionStart, selectionEnd, "")
}

fun TextProcessor.copy() {
    val clipboardManager = context.getSystemService<ClipboardManager>()
    val clipData = ClipData.newPlainText(LABEL_COPY, selectedText)
    clipboardManager?.setPrimaryClip(clipData)
}

fun TextProcessor.paste() {
    val clipboardManager = context.getSystemService<ClipboardManager>()
    val clipData = clipboardManager?.primaryClip?.getItemAt(0)
    val clipText = clipData?.coerceToText(context)
    text.replace(selectionStart, selectionEnd, clipText)
}

fun TextProcessor.selectLine() {
    val currentLine = lines.getLineForIndex(selectionStart)
    val lineStart = lines.getIndexForStartOfLine(currentLine)
    val lineEnd = lines.getIndexForEndOfLine(currentLine)
    setSelectionRange(lineStart, lineEnd)
}

fun TextProcessor.deleteLine() {
    val currentLine = lines.getLineForIndex(selectionStart)
    val lineStart = lines.getIndexForStartOfLine(currentLine)
    val lineEnd = lines.getIndexForEndOfLine(currentLine)
    text.delete(lineStart, lineEnd)
}

fun TextProcessor.duplicateLine() {
    val currentLine = lines.getLineForIndex(selectionStart)
    val lineStart = lines.getIndexForStartOfLine(currentLine)
    val lineEnd = lines.getIndexForEndOfLine(currentLine)
    val lineText = text.subSequence(lineStart, lineEnd)
    text.insert(lineEnd, "\n" + lineText)
}

fun TextProcessor.moveCaretToStartOfLine(): Boolean {
    val currentLine = lines.getLineForIndex(selectionStart)
    val lineStart = lines.getIndexForStartOfLine(currentLine)
    setSelectionIndex(lineStart)
    return true
}

fun TextProcessor.moveCaretToEndOfLine(): Boolean {
    val currentLine = lines.getLineForIndex(selectionEnd)
    val lineEnd = lines.getIndexForEndOfLine(currentLine)
    setSelectionIndex(lineEnd)
    return true
}

fun TextProcessor.moveCaretToPrevWord(): Boolean {
    if (selectionStart > 0) {
        val currentChar = text[selectionStart - 1]
        val isLetterDigitOrUnderscore = currentChar.isLetterOrDigit() || currentChar == '_'
        if (isLetterDigitOrUnderscore) {
            for (i in selectionStart downTo 0) {
                val char = text[i - 1]
                if (!char.isLetterOrDigit() && char != '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        } else {
            for (i in selectionStart downTo 0) {
                val char = text[i - 1]
                if (char.isLetterOrDigit() || char == '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        }
    }
    return true
}

fun TextProcessor.moveCaretToNextWord(): Boolean {
    if (selectionStart < text.length) {
        val currentChar = text[selectionStart]
        val isLetterDigitOrUnderscore = currentChar.isLetterOrDigit() || currentChar == '_'
        if (isLetterDigitOrUnderscore) {
            for (i in selectionStart until text.length) {
                val char = text[i]
                if (!char.isLetterOrDigit() && char != '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        } else {
            for (i in selectionStart until text.length) {
                val char = text[i]
                if (char.isLetterOrDigit() || char == '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        }
    }
    return true
}

fun EditText.setSelectionRange(start: Int, end: Int) {
    setSelection(
        if (start > text.length) text.length else start,
        if (end > text.length) text.length else end,
    )
}

fun EditText.setSelectionIndex(index: Int) {
    setSelection(
        if (index > text.length) text.length else index,
    )
}

fun TextProcessor.gotoLine(lineNumber: Int) {
    val line = lineNumber - 1
    if (line < 0 || line >= lines.lineCount - 1) {
        throw LineException(lineNumber)
    }
    setSelectionIndex(lines.getIndexForLine(line))
}

fun TextProcessor.hasPrimaryClip(): Boolean {
    val clipboardManager = context.getSystemService<ClipboardManager>()
    return clipboardManager?.hasPrimaryClip() ?: false
}