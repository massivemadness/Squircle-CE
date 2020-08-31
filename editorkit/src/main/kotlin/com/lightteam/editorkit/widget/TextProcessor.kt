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
import android.view.KeyEvent
import androidx.core.content.getSystemService
import com.lightteam.editorkit.R
import com.lightteam.editorkit.feature.gotoline.LineException
import com.lightteam.editorkit.internal.CodeSuggestsEditText
import kotlin.jvm.Throws

class TextProcessor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : CodeSuggestsEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val LABEL_CUT = "CUT"
        private const val LABEL_COPY = "COPY"
    }

    var onKeyDownListener: OnKeyDownListener? = null

    private val clipboardManager = context.getSystemService<ClipboardManager>()!!

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) {
            return super.onKeyDown(keyCode, event)
        }
        return onKeyDownListener?.onKeyDown(keyCode, event) ?: super.onKeyDown(keyCode, event)
    }

    fun insert(delta: CharSequence) {
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
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        val lineEnd = getIndexForEndOfLine(currentLine)
        setSelection(lineStart, lineEnd)
    }

    fun deleteLine() {
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        val lineEnd = getIndexForEndOfLine(currentLine)
        text.delete(lineStart, lineEnd)
    }

    fun duplicateLine() {
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        val lineEnd = getIndexForEndOfLine(currentLine)
        val lineText = text.subSequence(lineStart, lineEnd)
        text.insert(lineEnd, "\n" + lineText)
    }

    fun moveCaretToStartOfLine(): Boolean {
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        setSelection(lineStart)
        return true
    }

    fun moveCaretToEndOfLine(): Boolean {
        val currentLine = lines.getLineForIndex(selectionEnd)
        val lineEnd = getIndexForEndOfLine(currentLine)
        setSelection(lineEnd)
        return true
    }

    @Throws(LineException::class)
    fun gotoLine(lineNumber: Int) {
        val line = lineNumber - 1
        if (line < 0 || line >= lines.lineCount - 1) {
            throw LineException(lineNumber)
        }
        setSelection(lines.getIndexForLine(line))
    }

    fun hasPrimaryClip(): Boolean {
        return clipboardManager.hasPrimaryClip()
    }

    private fun selectedText(): CharSequence {
        return text.subSequence(selectionStart, selectionEnd)
    }

    interface OnKeyDownListener {
        fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean?
    }
}