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
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDrawableOrThrow
import com.blacksquircle.ui.editorkit.R
import com.blacksquircle.ui.editorkit.widget.internal.ScrollableEditText

open class TextScroller @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), ScrollableEditText.OnScrollChangedListener {

    var state: State = State.HIDDEN
        set(value) {
            when (value) {
                State.HIDDEN -> {
                    hideHandler.removeCallbacks(hideCallback)
                    field = value
                    invalidate()
                }
                State.VISIBLE -> {
                    if (isShowScrollerJustified()) {
                        hideHandler.removeCallbacks(hideCallback)
                        field = value
                        invalidate()
                    }
                }
                State.DRAGGING -> {
                    hideHandler.removeCallbacks(hideCallback)
                    field = value
                    invalidate()
                }
                State.EXITING -> {
                    hideHandler.removeCallbacks(hideCallback)
                    field = value
                    invalidate()
                }
            }
        }

    private var scrollableEditText: ScrollableEditText? = null

    private val normalBitmap by lazy {
        val bitmap = Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
        thumbNormal.bounds = Rect(0, 0, width, thumbHeight)
        thumbNormal.draw(Canvas(bitmap))
        bitmap
    }
    private val draggingBitmap by lazy {
        val bitmap = Bitmap.createBitmap(width, thumbHeight, Bitmap.Config.ARGB_8888)
        thumbDragging.bounds = Rect(0, 0, width, thumbHeight)
        thumbDragging.draw(Canvas(bitmap))
        bitmap
    }

    private val thumbHeight: Int
    private val thumbNormal: Drawable
    private val thumbDragging: Drawable

    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideCallback = Runnable { state = State.EXITING }

    private val thumbPaint = Paint()
    private var thumbTop = 0f

    private var textScrollMax = 0f
    private var textScrollY = 0f

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TextScroller, 0, 0)

        val hasThumbNormal = typedArray.hasValue(R.styleable.TextScroller_thumbNormal)
        val hasThumbDragging = typedArray.hasValue(R.styleable.TextScroller_thumbDragging)
        val hasThumbTint = typedArray.hasValue(R.styleable.TextScroller_thumbTint)

        thumbNormal = if (hasThumbNormal) {
            typedArray.getDrawableOrThrow(R.styleable.TextScroller_thumbNormal)
        } else {
            ContextCompat.getDrawable(context, R.drawable.fastscroll_default)!!
        }

        thumbDragging = if (hasThumbDragging) {
            typedArray.getDrawableOrThrow(R.styleable.TextScroller_thumbDragging)
        } else {
            ContextCompat.getDrawable(context, R.drawable.fastscroll_pressed)!!
        }

        if (hasThumbTint) {
            val thumbTint = typedArray.getColorOrThrow(R.styleable.TextScroller_thumbTint)
            thumbNormal.setTint(thumbTint)
            thumbDragging.setTint(thumbTint)
        }

        thumbHeight = thumbNormal.intrinsicHeight
        thumbPaint.isAntiAlias = true
        thumbPaint.isDither = false
        thumbPaint.alpha = ALPHA_MAX

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (state) {
            State.HIDDEN -> return
            State.VISIBLE -> {
                thumbPaint.alpha = ALPHA_MAX
                canvas.drawBitmap(normalBitmap, 0f, thumbTop, thumbPaint)
            }
            State.DRAGGING -> {
                thumbPaint.alpha = ALPHA_MAX
                canvas.drawBitmap(draggingBitmap, 0f, thumbTop, thumbPaint)
            }
            State.EXITING -> {
                if (thumbPaint.alpha > ALPHA_STEP) {
                    thumbPaint.alpha = thumbPaint.alpha - ALPHA_STEP
                    canvas.drawBitmap(normalBitmap, 0f, thumbTop, thumbPaint)
                    handler.postDelayed(hideCallback, EXITING_DELAY)
                } else {
                    thumbPaint.alpha = ALPHA_MIN
                    state = State.HIDDEN
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (scrollableEditText == null || state == State.HIDDEN) {
            return false
        }
        getMeasurements()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isPointInThumb(event.x, event.y)) {
                    scrollableEditText?.abortFling()
                    state = State.DRAGGING
                    isPressed = true
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                state = State.VISIBLE
                isPressed = false
                hideHandler.postDelayed(hideCallback, TIME_EXITING)
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == State.DRAGGING) {
                    isPressed = true
                    scrollableEditText?.abortFling()
                    var newThumbTop = event.y.toInt() - thumbHeight / 2
                    if (newThumbTop < 0) {
                        newThumbTop = 0
                    } else if (thumbHeight + newThumbTop > height - paddingBottom) {
                        newThumbTop = height - paddingBottom - thumbHeight
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
        if (state != State.DRAGGING) {
            getMeasurements()
            state = State.VISIBLE
            hideHandler.postDelayed(hideCallback, TIME_EXITING)
        }
    }

    fun attachTo(scrollableEditText: ScrollableEditText) {
        this.scrollableEditText = scrollableEditText
        this.scrollableEditText?.addOnScrollChangedListener(this)
    }

    fun detach() {
        this.scrollableEditText?.removeOnScrollChangedListener(this)
        this.scrollableEditText = null
    }

    private fun scrollView() {
        if (scrollableEditText == null) return

        val scrollToAsFraction = thumbTop / (height - paddingBottom - thumbHeight)
        val lineHeight = scrollableEditText!!.lineHeight
        val textAreaHeight = scrollableEditText!!.height - scrollableEditText!!.paddingBottom
        scrollableEditText?.scrollTo(
            scrollableEditText!!.scrollX,
            ((textScrollMax * scrollToAsFraction) - (scrollToAsFraction * (textAreaHeight - lineHeight))).toInt(),
        )
    }

    private fun getMeasurements() {
        if (scrollableEditText == null) return

        textScrollMax = scrollableEditText?.layout?.height?.toFloat() ?: 0f
        textScrollY = scrollableEditText?.scrollY?.toFloat() ?: 0f
        thumbTop = getThumbTop()
    }

    private fun getThumbTop(): Float {
        if (scrollableEditText == null) return 0f

        val lineHeight = scrollableEditText!!.lineHeight
        val textAreaHeight = scrollableEditText!!.height - scrollableEditText!!.paddingBottom
        val calculatedThumbTop = (height - paddingBottom - thumbHeight) *
            (textScrollY / (textScrollMax - textAreaHeight + lineHeight))

        val absoluteThumbTop = if (!calculatedThumbTop.isNaN()) {
            calculatedThumbTop
        } else {
            0f
        }

        return if (absoluteThumbTop > height - paddingBottom - thumbHeight) {
            (height - paddingBottom - thumbHeight).toFloat()
        } else {
            absoluteThumbTop
        }
    }

    private fun isPointInThumb(x: Float, y: Float): Boolean {
        return x >= 0f && x <= width && y >= thumbTop && y <= thumbTop + thumbHeight
    }

    private fun isShowScrollerJustified(): Boolean {
        return textScrollMax / (scrollableEditText!!.height - scrollableEditText!!.paddingBottom) >= 1.5
    }

    enum class State {
        HIDDEN,
        VISIBLE,
        DRAGGING,
        EXITING,
    }

    companion object {

        private const val ALPHA_MAX = 225
        private const val ALPHA_STEP = 25
        private const val ALPHA_MIN = 0

        private const val EXITING_DELAY = 17L
        private const val TIME_EXITING = 2000L
    }
}