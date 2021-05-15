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

package com.blacksquircle.ui.editorkit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.getDrawableOrThrow
import com.blacksquircle.ui.editorkit.R
import com.blacksquircle.ui.editorkit.internal.ScrollableEditText

class TextScroller @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ScrollableEditText.OnScrollChangedListener {

    companion object {
        const val STATE_HIDDEN = 0
        const val STATE_VISIBLE = 1
        const val STATE_DRAGGING = 2
        const val STATE_EXITING = 3

        private const val TIME_EXITING = 2000L
    }

    var state = STATE_HIDDEN
        set(state) {
            when (state) {
                STATE_HIDDEN -> {
                    hideHandler.removeCallbacks(hideCallback)
                    field = STATE_HIDDEN
                    invalidate()
                }
                STATE_VISIBLE -> {
                    if (isShowScrollerJustified()) {
                        hideHandler.removeCallbacks(hideCallback)
                        field = STATE_VISIBLE
                        invalidate()
                    }
                }
                STATE_DRAGGING -> {
                    hideHandler.removeCallbacks(hideCallback)
                    field = STATE_DRAGGING
                    invalidate()
                }
                STATE_EXITING -> {
                    hideHandler.removeCallbacks(hideCallback)
                    field = STATE_EXITING
                    invalidate()
                }
            }
        }

    private var scrollableEditText: ScrollableEditText? = null

    private val normalBitmap by lazy {
        Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
    }
    private val draggingBitmap by lazy {
        Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
    }

    private val thumbHeight: Int
    private val thumbNormal: Drawable
    private val thumbDragging: Drawable

    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideCallback = Runnable { state = STATE_EXITING }

    private val thumbPaint = Paint()
    private var thumbTop = 0f

    private var textScrollMax = 0f
    private var textScrollY = 0f

    private var isInitialized = false

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TextScroller, 0, 0)

        val hasThumbNormal = typedArray.hasValue(R.styleable.TextScroller_thumbNormal)
        val hasThumbDragging = typedArray.hasValue(R.styleable.TextScroller_thumbDragging)
        val hasThumbTint = typedArray.hasValue(R.styleable.TextScroller_thumbTint)

        thumbNormal = if (hasThumbNormal) {
            typedArray.getDrawableOrThrow(R.styleable.TextScroller_thumbNormal)
        } else ContextCompat.getDrawable(context, R.drawable.fastscroll_default)!!

        thumbDragging = if (hasThumbDragging) {
            typedArray.getDrawableOrThrow(R.styleable.TextScroller_thumbDragging)
        } else ContextCompat.getDrawable(context, R.drawable.fastscroll_pressed)!!

        if (hasThumbTint) {
            val thumbTint = typedArray.getColor(R.styleable.TextScroller_thumbTint, Color.BLUE)
            thumbNormal.setTint(thumbTint)
            thumbDragging.setTint(thumbTint)
        }

        thumbHeight = thumbNormal.intrinsicHeight

        thumbPaint.isAntiAlias = true
        thumbPaint.isDither = false
        thumbPaint.alpha = 225

        typedArray.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!isInitialized) {
            thumbNormal.bounds = Rect(0, 0, width, thumbHeight)
            thumbNormal.draw(Canvas(normalBitmap))

            thumbDragging.bounds = Rect(0, 0, width, thumbHeight)
            thumbDragging.draw(Canvas(draggingBitmap))

            isInitialized = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (state != STATE_HIDDEN) {
            super.onDraw(canvas)
            if (state == STATE_VISIBLE || state == STATE_DRAGGING) {
                thumbPaint.alpha = 225
                if (state == STATE_VISIBLE) {
                    canvas.drawBitmap(normalBitmap, 0f, thumbTop, thumbPaint)
                } else {
                    canvas.drawBitmap(draggingBitmap, 0f, thumbTop, thumbPaint)
                }
            } else if (state == STATE_EXITING) {
                if (thumbPaint.alpha > 25) {
                    thumbPaint.alpha = thumbPaint.alpha - 25
                    canvas.drawBitmap(normalBitmap, 0f, thumbTop, thumbPaint)
                    handler.postDelayed(hideCallback, 17)
                    return
                }
                thumbPaint.alpha = 0
                state = STATE_HIDDEN
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (scrollableEditText == null || state == STATE_HIDDEN) {
            return false
        }
        getMeasurements()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isPointInThumb(event.x, event.y)) {
                    scrollableEditText?.abortFling()
                    state = STATE_DRAGGING
                    isPressed = true
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                state = STATE_VISIBLE
                isPressed = false
                hideHandler.postDelayed(hideCallback, TIME_EXITING)
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == STATE_DRAGGING) {
                    isPressed = true
                    scrollableEditText?.abortFling()
                    var newThumbTop = event.y.toInt() - thumbHeight / 2
                    if (newThumbTop < 0) {
                        newThumbTop = 0
                    } else if (thumbHeight + newThumbTop > height) {
                        newThumbTop = height - thumbHeight
                    }
                    thumbTop = newThumbTop.toFloat()
                    scrollView()
                    invalidate()
                    return true
                }
            }
        }
        return false
    }

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        if (state != STATE_DRAGGING) {
            getMeasurements()
            state = STATE_VISIBLE
            hideHandler.postDelayed(hideCallback, TIME_EXITING)
        }
    }

    fun attachTo(scrollableEditText: ScrollableEditText) {
        this.scrollableEditText = scrollableEditText
        this.scrollableEditText?.addOnScrollChangedListener(this)
    }

    private fun scrollView() {
        if (scrollableEditText != null) {
            val scrollToAsFraction = thumbTop / (height - thumbHeight)
            val lineHeight = scrollableEditText!!.lineHeight
            val textAreaHeight = scrollableEditText!!.height
            scrollableEditText?.scrollTo(
                scrollableEditText!!.scrollX,
                ((textScrollMax * scrollToAsFraction) - (scrollToAsFraction * (textAreaHeight - lineHeight))).toInt()
            )
        }
    }

    private fun getMeasurements() {
        if (scrollableEditText?.layout != null) {
            textScrollMax = scrollableEditText!!.layout.height.toFloat()
            textScrollY = scrollableEditText!!.scrollY.toFloat()
            thumbTop = getThumbTop()
        }
    }

    private fun getThumbTop(): Float {
        if (scrollableEditText != null) {
            val lineHeight = scrollableEditText!!.lineHeight
            val textAreaHeight = scrollableEditText!!.height
            val calculatedThumbTop =
                (height - thumbHeight) * (textScrollY / (textScrollMax - textAreaHeight + lineHeight))

            val absoluteThumbTop = if (!calculatedThumbTop.isNaN()) {
                calculatedThumbTop
            } else 0f

            return if (absoluteThumbTop > height - thumbHeight) {
                (height - thumbHeight).toFloat()
            } else absoluteThumbTop
        }
        return 0f
    }

    private fun isPointInThumb(x: Float, y: Float): Boolean {
        return x >= 0f && x <= width && y >= thumbTop && y <= thumbTop + thumbHeight
    }

    private fun isShowScrollerJustified(): Boolean {
        return textScrollMax / scrollableEditText!!.height >= 1.5
    }
}