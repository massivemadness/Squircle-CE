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

package com.blacksquircle.ui.editorkit.widget.internal

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.MultiAutoCompleteTextView
import android.widget.OverScroller
import kotlin.math.abs

abstract class ScrollableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.autoCompleteTextViewStyle,
) : MultiAutoCompleteTextView(context, attrs, defStyleAttr) {

    private val textScroller = OverScroller(context)
    private val scrollListeners = mutableListOf<OnScrollChangedListener>()
    private val maximumVelocity = ViewConfiguration.get(context)
        .scaledMaximumFlingVelocity.toFloat()

    private var velocityTracker: VelocityTracker? = null
    private var horizontallyScrollable = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        for (listener in scrollListeners) {
            listener.onScrollChanged(scrollX, scrollY, scrollX, scrollY)
        }
    }

    override fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        for (listener in scrollListeners) {
            listener.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        initVelocityTrackerIfNotExists()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> abortFling()
            MotionEvent.ACTION_UP -> {
                velocityTracker?.computeCurrentVelocity(1000, maximumVelocity)
                val velocityX = if (isHorizontallyScrollableCompat()) {
                    velocityTracker?.xVelocity?.toInt() ?: 0
                } else {
                    0
                }
                val velocityY = velocityTracker?.yVelocity?.toInt() ?: 0
                if (abs(velocityY) < 0 || abs(velocityX) < 0) {
                    recycleVelocityTracker()
                } else if (velocityX != 0 || velocityY != 0) {
                    textScroller.fling(
                        scrollX,
                        scrollY,
                        -velocityX,
                        -velocityY,
                        0,
                        (layout?.width ?: width) - width + paddingStart + paddingEnd,
                        0,
                        (layout?.height ?: width) - height + paddingTop + paddingBottom,
                    )
                }
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (textScroller.computeScrollOffset()) {
            val currX = textScroller.currX
            val currY = textScroller.currY
            if (currY < 0) {
                val isValidPosition = scrollY - abs(currY) > 0
                if (isValidPosition) {
                    scrollTo(currX, currY)
                }
            } else {
                scrollTo(currX, currY)
            }
            postInvalidate()
        }
    }

    override fun setHorizontallyScrolling(whether: Boolean) {
        super.setHorizontallyScrolling(whether)
        horizontallyScrollable = whether
    }

    fun isHorizontallyScrollableCompat(): Boolean {
        return horizontallyScrollable
    }

    fun addOnScrollChangedListener(listener: OnScrollChangedListener) {
        scrollListeners.add(listener)
    }

    fun removeOnScrollChangedListener(listener: OnScrollChangedListener) {
        scrollListeners.remove(listener)
    }

    fun abortFling() {
        if (!textScroller.isFinished) {
            textScroller.abortAnimation()
        }
        velocityTracker?.clear()
    }

    private fun initVelocityTrackerIfNotExists() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        velocityTracker?.recycle()
        velocityTracker = null
    }

    interface OnScrollChangedListener {
        fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int)
    }
}