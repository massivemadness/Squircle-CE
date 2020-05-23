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

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.Scroller
import com.lightteam.editorkit.R
import kotlin.math.abs

open class ScrollableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : ConfigurableEditText(context, attrs, defStyleAttr) {

    private val textScroller = Scroller(context)

    private var scrollListeners = arrayOf<OnScrollChangedListener>()
    private var maximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity * 100f
    private var velocityTracker: VelocityTracker? = null

    override fun configure() {
        super.configure()
        setHorizontallyScrolling(!config.wordWrap)
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
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                } else {
                    velocityTracker?.clear()
                }
                super.onTouchEvent(event)
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.computeCurrentVelocity(1000, maximumVelocity)
                val velocityX = if (config.wordWrap) {
                    0
                } else {
                    velocityTracker?.xVelocity?.toInt() ?: 0
                }
                val velocityY = velocityTracker?.yVelocity?.toInt() ?: 0
                if (abs(velocityY) < 0 || abs(velocityX) < 0) {
                    if (velocityTracker != null) {
                        velocityTracker?.recycle()
                        velocityTracker = null
                    }
                    super.onTouchEvent(event)
                }
                if (layout == null) {
                    return super.onTouchEvent(event)
                }
                textScroller.fling(
                    scrollX, scrollY,
                    -velocityX, -velocityY,
                    0, layout.width - width + paddingLeft + paddingRight,
                    0, layout.height - height + paddingTop + paddingBottom
                )
                super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                super.onTouchEvent(event)
            }
            else -> super.onTouchEvent(event)
        }
        return true
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
            }
        }
    }

    fun addOnScrollChangedListener(listener: OnScrollChangedListener) {
        val newListener = arrayOfNulls<OnScrollChangedListener>(scrollListeners.size + 1)
        val length = scrollListeners.size
        System.arraycopy(scrollListeners, 0, newListener, 0, length)
        newListener[newListener.size - 1] = listener
        scrollListeners = newListener.requireNoNulls()
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