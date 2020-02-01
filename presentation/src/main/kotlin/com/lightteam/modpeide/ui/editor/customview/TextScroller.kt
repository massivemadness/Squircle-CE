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

package com.lightteam.modpeide.ui.editor.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lightteam.modpeide.R
import com.lightteam.modpeide.ui.editor.customview.internal.textscroller.OnScrollChangedListener
import com.lightteam.modpeide.utils.extensions.getDrawableCompat
import kotlin.math.roundToInt

class TextScroller(context: Context, attrs: AttributeSet) : View(context, attrs), OnScrollChangedListener {

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

    private lateinit var textProcessor: TextProcessor

    private lateinit var draggingBitmap: Bitmap
    private lateinit var normalBitmap: Bitmap

    private val thumbDragging: Drawable =
        context.getDrawableCompat(R.drawable.fastscroll_thumb_pressed)
    private val thumbNormal: Drawable =
        context.getDrawableCompat(R.drawable.fastscroll_thumb_default)

    private val hideHandler: Handler
    private val hideCallback: Runnable

    private val thumbPaint: Paint = Paint()
    private val thumbHeight: Int = thumbNormal.intrinsicHeight

    private var scrollMax: Float = 0f
    private var scrollY: Float = 0f
    private var thumbTop: Float = 0f

    init {
        val typedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.TextScroller, 0, 0)
        val color = typedArray.getColor(R.styleable.TextScroller_thumbTint, Color.WHITE)

        hideHandler = Handler()
        hideCallback = Runnable { state = STATE_EXITING }

        @Suppress("DEPRECATION")
        thumbNormal.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
        @Suppress("DEPRECATION")
        thumbDragging.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)

        thumbPaint.isAntiAlias = true
        thumbPaint.isDither = false
        thumbPaint.alpha = 225

        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (::textProcessor.isInitialized && state != STATE_HIDDEN) {
            if (!::normalBitmap.isInitialized) {
                thumbNormal.bounds = Rect(0, 0, width, thumbHeight)
                normalBitmap = Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
                thumbNormal.draw(Canvas(normalBitmap))
            }
            if (!::draggingBitmap.isInitialized) {
                thumbDragging.bounds = Rect(0, 0, width, thumbHeight)
                draggingBitmap = Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
                thumbDragging.draw(Canvas(draggingBitmap))
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
        if (!::textProcessor.isInitialized || state == STATE_HIDDEN) {
            return false
        }
        getMeasurements()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isPointInThumb(event.x, event.y)) {
                    return false
                }
                textProcessor.abortFling()
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
                textProcessor.abortFling()
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

    fun link(editor: TextProcessor?) {
        if (editor != null) {
            textProcessor = editor
            textProcessor.addOnScrollChangedListener(this)
        }
    }

    private fun scrollView() {
        val scrollToAsFraction = thumbTop / (height - thumbHeight)
        textProcessor.scrollTo(textProcessor.scrollX, (scrollMax * scrollToAsFraction).toInt() -
            (scrollToAsFraction * (textProcessor.height - textProcessor.lineHeight)).toInt()
        )
    }

    private fun isPointInThumb(x: Float, y: Float): Boolean {
        return x >= 0f && x <= width && y >= thumbTop && y <= thumbTop + thumbHeight
    }

    private fun getMeasurements() {
        if (::textProcessor.isInitialized && textProcessor.layout != null) {
            scrollMax = textProcessor.layout.height.toFloat()
            scrollY = textProcessor.scrollY.toFloat()
            thumbTop = getThumbTop().toFloat()
        }
    }

    private fun getThumbTop(): Int {
        val absoluteThumbTop = ((height - thumbHeight) * (scrollY /
                (scrollMax - textProcessor.height + textProcessor.lineHeight))).roundToInt()
        return if (absoluteThumbTop > height - thumbHeight) {
            height - thumbHeight
        } else {
            absoluteThumbTop
        }
    }

    private fun isShowScrollerJustified(): Boolean {
        return scrollMax / textProcessor.height >= 1.5
    }
}