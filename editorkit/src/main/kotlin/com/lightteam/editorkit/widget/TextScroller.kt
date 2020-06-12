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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.getDrawableOrThrow
import com.lightteam.editorkit.R
import com.lightteam.editorkit.internal.ScrollableEditText
import kotlin.math.roundToInt

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
                    return
                }
                STATE_EXITING -> {
                    hideHandler.removeCallbacks(hideCallback)
                    field = STATE_EXITING
                    invalidate()
                }
            }
        }

    private lateinit var scrollableEditText: ScrollableEditText

    private lateinit var normalBitmap: Bitmap
    private lateinit var draggingBitmap: Bitmap

    private val thumbHeight: Int
    private val thumbNormal: Drawable
    private val thumbDragging: Drawable

    private val hideHandler: Handler = Handler()
    private val hideCallback: Runnable = Runnable { state = STATE_EXITING }

    private val thumbPaint: Paint = Paint()
    private var thumbTop: Float = 0F

    private var textScrollMax: Float = 0F
    private var textScrollY: Float = 0F

    private var isInitialized = false

    init {
        val typedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.TextScroller, 0, 0)

        thumbNormal = typedArray.getDrawableOrThrow(R.styleable.TextScroller_thumbNormal)
        thumbDragging = typedArray.getDrawableOrThrow(R.styleable.TextScroller_thumbDragging)

        val thumbTint = typedArray.getColor(R.styleable.TextScroller_thumbTint, Color.WHITE)
        thumbNormal.setTint(thumbTint)
        thumbDragging.setTint(thumbTint)

        thumbHeight = thumbNormal.intrinsicHeight

        thumbPaint.isAntiAlias = true
        thumbPaint.isDither = false
        thumbPaint.alpha = 225

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        if (::scrollableEditText.isInitialized && state != STATE_HIDDEN) {
            if (!isInitialized) {
                normalBitmap = Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
                thumbNormal.bounds = Rect(0, 0, width, thumbHeight)
                thumbNormal.draw(Canvas(normalBitmap))

                draggingBitmap = Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
                thumbDragging.bounds = Rect(0, 0, width, thumbHeight)
                thumbDragging.draw(Canvas(draggingBitmap))

                isInitialized = true
            }
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
        if (!::scrollableEditText.isInitialized || state == STATE_HIDDEN) {
            return false
        }
        getMeasurements()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isPointInThumb(event.x, event.y)) {
                    return false
                }
                scrollableEditText.abortFling()
                state = STATE_DRAGGING
                isPressed = true
                return true
            }
            MotionEvent.ACTION_UP -> {
                state = STATE_VISIBLE
                isPressed = false
                hideHandler.postDelayed(hideCallback, TIME_EXITING)
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                if (state != STATE_DRAGGING) {
                    return false
                }
                isPressed = true
                scrollableEditText.abortFling()
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
        return false
    }

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        if (state != STATE_DRAGGING) {
            getMeasurements()
            state = STATE_VISIBLE
            hideHandler.postDelayed(hideCallback, TIME_EXITING)
        }
    }

    fun link(scrollableEditText: ScrollableEditText) {
        this.scrollableEditText = scrollableEditText
        this.scrollableEditText.addOnScrollChangedListener(this)
    }

    private fun scrollView() {
        val scrollToAsFraction = thumbTop / (height - thumbHeight)
        scrollableEditText.scrollTo(
            scrollableEditText.scrollX,
            (textScrollMax * scrollToAsFraction).toInt() - (scrollToAsFraction * (scrollableEditText.height - scrollableEditText.lineHeight)).toInt()
        )
    }

    private fun isPointInThumb(x: Float, y: Float): Boolean {
        return x >= 0f && x <= width && y >= thumbTop && y <= thumbTop + thumbHeight
    }

    private fun getMeasurements() {
        if (::scrollableEditText.isInitialized && scrollableEditText.layout != null) {
            textScrollMax = scrollableEditText.layout.height.toFloat()
            textScrollY = scrollableEditText.scrollY.toFloat()
            thumbTop = getThumbTop().toFloat()
        }
    }

    private fun getThumbTop(): Int {
        val calculatedThumbTop = ((height - thumbHeight) * (textScrollY /
                (textScrollMax - scrollableEditText.height + scrollableEditText.lineHeight)))
        val absoluteThumbTop = if (!calculatedThumbTop.isNaN()) {
            calculatedThumbTop.roundToInt()
        } else {
            0
        }
        return if (absoluteThumbTop > height - thumbHeight) {
            height - thumbHeight
        } else {
            absoluteThumbTop
        }
    }

    private fun isShowScrollerJustified(): Boolean {
        return textScrollMax / scrollableEditText.height >= 1.5
    }
}