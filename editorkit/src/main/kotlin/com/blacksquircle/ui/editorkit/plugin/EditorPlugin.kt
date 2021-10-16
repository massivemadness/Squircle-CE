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

package com.blacksquircle.ui.editorkit.plugin

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.view.KeyEvent
import android.view.MotionEvent

interface EditorPlugin {

    val pluginId: String

    fun onCreate(context: Context)

    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = Unit
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
    fun onDraw(canvas: Canvas) = Unit

    fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) = Unit
    fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) = Unit
    fun onSelectionChanged(selStart: Int, selEnd: Int) = Unit
    fun onKeyDown(keyCode: Int, event: KeyEvent?) = Unit
    fun onKeyUp(keyCode: Int, event: KeyEvent?) = Unit
    fun onTouchEvent(event: MotionEvent) = Unit

    fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) = Unit
    fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) = Unit
    fun doAfterTextChanged(text: Editable?) = Unit

    fun doOnTextReceived(text: CharSequence) = Unit
    fun doOnTextCleared() = Unit
}