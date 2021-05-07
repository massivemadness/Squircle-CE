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

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.widget.Scroller
import com.blacksquircle.ui.editorkit.R
import kotlin.math.abs

abstract class ScrollableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : ConfigurableEditText(context, attrs, defStyleAttr) {

    private val textScroller = Scroller(context)
    private val scrollListeners = mutableListOf<OnScrollChangedListener>()
    private val maximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity * 100f

    private var velocityTracker: VelocityTracker? = null

    override fun configure() {
        imeOptions = if (editorConfig.softKeyboard) {
            EditorInfo.IME_ACTION_UNSPECIFIED
        } else {
            EditorInfo.IME_FLAG_NO_EXTRACT_UI
        }

        inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        textSize = editorConfig.fontSize
        typeface = editorConfig.fontType

        setHorizontallyScrolling(!editorConfig.wordWrap)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        for (listener in scrollListeners) {
            listener.onScrollChanged(scrollX, scrollY, scrollX, scrollY)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                abortFling()
                velocityTracker?.clear()
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                }
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.computeCurrentVelocity(1000, maximumVelocity)
                val velocityX = if (!editorConfig.wordWrap) {
                    velocityTracker?.xVelocity?.toInt() ?: 0
                } else 0
                val velocityY = velocityTracker?.yVelocity?.toInt() ?: 0
                if (abs(velocityY) < 0 || abs(velocityX) < 0) {
                    velocityTracker?.recycle()
                    velocityTracker = null
                }
                if (layout != null) {
                    textScroller.fling(
                        scrollX, scrollY,
                        -velocityX, -velocityY,
                        0, layout.width - width + paddingStart + paddingEnd,
                        0, layout.height - height + paddingTop + paddingBottom
                    )
                }
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        for (listener in scrollListeners) {
            listener.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        }
    }

    override fun computeScroll() {
        if (!isInEditMode) {
            if (textScroller.computeScrollOffset()) {
                scrollTo(textScroller.currX, textScroller.currY)
                postInvalidate()
            }
        }
    }

    fun addOnScrollChangedListener(listener: OnScrollChangedListener) {
        scrollListeners.add(listener)
    }

    fun abortFling() {
        if (!textScroller.isFinished) {
            textScroller.abortAnimation()
        }
    }

    interface OnScrollChangedListener {
        fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int)
    }
}