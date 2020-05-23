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

package com.lightteam.editorkit.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.getSystemService
import com.lightteam.editorkit.R
import com.lightteam.editorkit.internal.CodeSuggestsEditText

class TextProcessor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : CodeSuggestsEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val LABEL_CUT = "CUT"
        private const val LABEL_COPY = "COPY"
    }

    private val clipboardManager = context.getSystemService<ClipboardManager>()!!

    fun insert(delta: CharSequence) {
        var selectionStart = 0.coerceAtLeast(selectionStart)
        var selectionEnd = 0.coerceAtLeast(selectionEnd)

        selectionStart = selectionStart.coerceAtMost(selectionEnd)
        selectionEnd = selectionStart.coerceAtLeast(selectionEnd)

        text.replace(selectionStart, selectionEnd, delta)
    }

    fun cut() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(LABEL_CUT, selectedText()))
        text.replace(selectionStart, selectionEnd, "")
    }

    fun copy() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(LABEL_COPY, selectedText()))
    }

    fun paste() {
        val clip = clipboardManager.primaryClip?.getItemAt(0)?.coerceToText(context)
        text.replace(selectionStart, selectionEnd, clip)
    }

    fun selectLine() {
        var start = selectionStart.coerceAtMost(selectionEnd)
        var end = selectionStart.coerceAtLeast(selectionEnd)
        if (end > start) {
            end--
        }
        while (end < text.length && text[end] != '\n') {
            end++
        }
        while (start > 0 && text[start - 1] != '\n') {
            start--
        }
        setSelection(start, end)
    }

    fun deleteLine() {
        var start = selectionStart.coerceAtMost(selectionEnd)
        var end = selectionStart.coerceAtLeast(selectionEnd)
        if (end > start) {
            end--
        }
        while (end < text.length && text[end] != '\n') {
            end++
        }
        while (start > 0 && text[start - 1] != '\n') {
            start--
        }
        text.delete(start, end)
    }

    fun duplicateLine() {
        var start = selectionStart.coerceAtMost(selectionEnd)
        var end = selectionStart.coerceAtLeast(selectionEnd)
        if (end > start) {
            end--
        }
        while (end < text.length && text[end] != '\n') {
            end++
        }
        while (start > 0 && text[start - 1] != '\n') {
            start--
        }
        text.insert(end, "\n" + text.subSequence(start, end))
    }

    fun gotoLine(lineNumber: Int) {
        setSelection(lines.getIndexForLine(lineNumber))
    }

    fun hasPrimaryClip(): Boolean {
        return clipboardManager.hasPrimaryClip()
    }

    private fun selectedText(): CharSequence {
        return text.subSequence(selectionStart, selectionEnd)
    }
}