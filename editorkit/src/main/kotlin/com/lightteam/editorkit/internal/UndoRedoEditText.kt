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

package com.lightteam.editorkit.internal

import android.content.Context
import android.util.AttributeSet
import androidx.core.text.PrecomputedTextCompat
import com.lightteam.editorkit.R
import com.lightteam.editorkit.feature.undoredo.TextChange
import com.lightteam.editorkit.feature.undoredo.UndoStack

open class UndoRedoEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : LineNumbersEditText(context, attrs, defStyleAttr) {

    var isDoingUndoRedo = false

    var undoStack: UndoStack = UndoStack()
    var redoStack: UndoStack = UndoStack()

    var onUndoRedoChangedListener: OnUndoRedoChangedListener? = null

    private var textLastChange: TextChange? = null

    override fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        super.doBeforeTextChanged(text, start, count, after)
        if (!isDoingUndoRedo) {
            if (count < UndoStack.MAX_SIZE) {
                textLastChange = TextChange(
                    newText = "",
                    oldText = text?.subSequence(start, start + count).toString(),
                    start = start
                )
                return
            }
            undoStack.removeAll()
            redoStack.removeAll()
            textLastChange = null
        }
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.doOnTextChanged(text, start, before, count)
        if (!isDoingUndoRedo && textLastChange != null) {
            if (count < UndoStack.MAX_SIZE) {
                textLastChange?.newText = text?.subSequence(start, start + count).toString()
                if (start == textLastChange?.start &&
                    (textLastChange?.oldText?.isNotEmpty()!! || textLastChange?.newText?.isNotEmpty()!!) &&
                    textLastChange?.oldText != textLastChange?.newText) {
                    undoStack.push(textLastChange!!)
                    redoStack.removeAll()
                }
            } else {
                undoStack.removeAll()
                redoStack.removeAll()
            }
            textLastChange = null
            onUndoRedoChangedListener?.onUndoRedoChanged()
        }
    }

    override fun processText(textParams: PrecomputedTextCompat?) {
        super.processText(textParams)
        onUndoRedoChangedListener?.onUndoRedoChanged()
    }

    override fun clearText() {
        undoStack.removeAll()
        redoStack.removeAll()
        onUndoRedoChangedListener?.onUndoRedoChanged()
        super.clearText()
    }

    fun canUndo(): Boolean = undoStack.canUndo()
    fun canRedo(): Boolean = redoStack.canUndo()

    fun undo(): Boolean {
        val textChange = undoStack.pop()
        if (textChange.start >= 0) {
            isDoingUndoRedo = true
            if (textChange.start > text.length) {
                textChange.start = text.length
            }
            var end = textChange.start + textChange.newText.length
            if (end < 0) {
                end = 0
            }
            if (end > text.length) {
                end = text.length
            }
            redoStack.push(textChange)
            text.replace(textChange.start, end, textChange.oldText)
            setSelection(textChange.start + textChange.oldText.length)
            isDoingUndoRedo = false
        } else {
            undoStack.removeAll()
        }
        onUndoRedoChangedListener?.onUndoRedoChanged()
        return true
    }

    fun redo(): Boolean {
        val textChange = redoStack.pop()
        if (textChange.start >= 0) {
            isDoingUndoRedo = true
            undoStack.push(textChange)
            text.replace(
                textChange.start,
                textChange.start + textChange.oldText.length,
                textChange.newText
            )
            setSelection(textChange.start + textChange.newText.length)
            isDoingUndoRedo = false
        } else {
            undoStack.removeAll()
        }
        onUndoRedoChangedListener?.onUndoRedoChanged()
        return true
    }

    interface OnUndoRedoChangedListener {
        fun onUndoRedoChanged()
    }
}