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

package com.blacksquircle.ui.editorkit.plugin.base

import android.graphics.Canvas
import android.graphics.Typeface
import android.text.Editable
import android.view.KeyEvent
import android.view.MotionEvent
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.editorkit.widget.TextProcessor
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.model.TextStructure

abstract class EditorPlugin(val pluginId: String) {

    private var _editText: TextProcessor? = null
    protected val editText: TextProcessor
        get() = _editText!!

    protected val isAttached: Boolean
        get() = _editText != null

    protected val language: Language?
        get() = editText.language
    protected val colorScheme: ColorScheme
        get() = editText.colorScheme
    protected val structure: TextStructure
        get() = editText.structure
    protected val undoStack: UndoStack
        get() = editText.undoStack
    protected val redoStack: UndoStack
        get() = editText.redoStack

    open fun onAttached(editText: TextProcessor) {
        this._editText = editText
        onColorSchemeChanged(colorScheme)
        onLanguageChanged(language)
    }

    open fun onDetached(editText: TextProcessor) {
        this._editText = null
    }

    open fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = Unit
    open fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
    open fun drawBehind(canvas: Canvas?) = Unit
    open fun onDraw(canvas: Canvas?) = Unit

    open fun onColorSchemeChanged(colorScheme: ColorScheme) = Unit
    open fun onLanguageChanged(language: Language?) = Unit

    open fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) = Unit
    open fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) = Unit
    open fun onSelectionChanged(selStart: Int, selEnd: Int) = Unit
    open fun onTouchEvent(event: MotionEvent): Boolean = false
    open fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean = false
    open fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean = false

    open fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) = Unit
    open fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) = Unit
    open fun afterTextChanged(text: Editable?) = Unit

    open fun processLine(lineNumber: Int, lineStart: Int, lineEnd: Int) = Unit
    open fun addLine(lineNumber: Int, lineStart: Int) = Unit
    open fun removeLine(lineNumber: Int) = Unit

    open fun setTextContent(text: CharSequence) = Unit
    open fun setTextSize(size: Float) = Unit
    open fun setTypeface(tf: Typeface?) = Unit

    open fun showDropDown() = Unit

    protected fun requireContext() = _editText?.context
        ?: throw IllegalStateException("EditorPlugin $this not attached to a context.")
}