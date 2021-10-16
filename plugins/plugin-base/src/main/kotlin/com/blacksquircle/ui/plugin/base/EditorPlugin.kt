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

package com.blacksquircle.ui.plugin.base

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.EditText

abstract class EditorPlugin(val pluginId: String) {

    protected var editText: EditText? = null

    open fun onAttached(editText: EditText) {
        this.editText = editText
    }

    open fun onDetached(editText: EditText) {
        this.editText = null
    }

    open fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = Unit
    open fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
    open fun onDraw(canvas: Canvas) = Unit

    open fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) = Unit
    open fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) = Unit
    open fun onSelectionChanged(selStart: Int, selEnd: Int) = Unit
    open fun onTouchEvent(event: MotionEvent) = Unit
    open fun onKeyUp(keyCode: Int, event: KeyEvent?) = Unit
    open fun onKeyDown(keyCode: Int, event: KeyEvent?) = Unit

    open fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) = Unit
    open fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) = Unit
    open fun doAfterTextChanged(text: Editable?) = Unit

    open fun doOnTextReceived(text: CharSequence) = Unit
    open fun doOnTextCleared() = Unit

    protected fun requireContext(): Context {
        if (editText?.context == null) {
            throw IllegalStateException("EditorPlugin $this not attached to a context.")
        }
        return editText!!.context
    }
}