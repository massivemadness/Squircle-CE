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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) {
            return super.onKeyDown(keyCode, event)
        }

        val ctrl = event.isCtrlPressed
        val shift = event.isShiftPressed
        val alt = event.isAltPressed

        return when {
            ctrl && (shift || alt) && keyCode == KeyEvent.KEYCODE_A -> selectLine()
            ctrl && shift && keyCode == KeyEvent.KEYCODE_Z -> if (canRedo()) redo() else true
            ctrl && keyCode == KeyEvent.KEYCODE_X -> cut()
            ctrl && keyCode == KeyEvent.KEYCODE_C -> copy()
            ctrl && keyCode == KeyEvent.KEYCODE_V -> paste()
            ctrl && keyCode == KeyEvent.KEYCODE_A -> { selectAll(); true }
            ctrl && keyCode == KeyEvent.KEYCODE_DEL -> deleteLine()
            ctrl && keyCode == KeyEvent.KEYCODE_D -> duplicateLine()
            ctrl && keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> moveCaretToStartOfLine()
            ctrl && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> moveCaretToEndOfLine()
            ctrl && keyCode == KeyEvent.KEYCODE_Z -> if (canUndo()) undo() else true
            ctrl && keyCode == KeyEvent.KEYCODE_Y -> if (canRedo()) redo() else true
            // ctrl && keyCode == KeyEvent.KEYCODE_S -> // TODO Save
            // ctrl && keyCode == KeyEvent.KEYCODE_W -> // TODO Close
            // ctrl && keyCode == KeyEvent.KEYCODE_F -> // TODO Find
            // ctrl && keyCode == KeyEvent.KEYCODE_R -> // TODO Find & Replace
            // ctrl && keyCode == KeyEvent.KEYCODE_G -> // TODO Go to Line
            // alt && keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> moveCaretToPrevWord() // TODO
            // alt && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> moveCaretToNextWord() // TODO
            keyCode == KeyEvent.KEYCODE_TAB -> insert(tab())
            else -> super.onKeyDown(keyCode, event)
        }
    }

    fun insert(delta: CharSequence): Boolean {
        text.replace(selectionStart, selectionEnd, delta)
        return true
    }

    fun cut(): Boolean {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(LABEL_CUT, selectedText()))
        text.replace(selectionStart, selectionEnd, "")
        return true
    }

    fun copy(): Boolean {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(LABEL_COPY, selectedText()))
        return true
    }

    fun paste(): Boolean {
        val clip = clipboardManager.primaryClip?.getItemAt(0)?.coerceToText(context)
        text.replace(selectionStart, selectionEnd, clip)
        return true
    }

    fun selectLine(): Boolean {
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        val lineEnd = getIndexForEndOfLine(currentLine)
        setSelection(lineStart, lineEnd)
        return true
    }

    fun deleteLine(): Boolean {
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        val lineEnd = getIndexForEndOfLine(currentLine)
        text.delete(lineStart, lineEnd)
        return true
    }

    fun duplicateLine(): Boolean {
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        val lineEnd = getIndexForEndOfLine(currentLine)
        val lineText = text.subSequence(lineStart, lineEnd)
        text.insert(lineEnd, "\n" + lineText)
        return true
    }

    @SuppressWarnings("WeakerAccess")
    fun moveCaretToStartOfLine(): Boolean {
        val currentLine = lines.getLineForIndex(selectionStart)
        val lineStart = getIndexForStartOfLine(currentLine)
        setSelection(lineStart, lineStart)
        return true
    }

    @SuppressWarnings("WeakerAccess")
    fun moveCaretToEndOfLine(): Boolean {
        val currentLine = lines.getLineForIndex(selectionEnd)
        val lineEnd = getIndexForEndOfLine(currentLine)
        setSelection(lineEnd, lineEnd)
        return true
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