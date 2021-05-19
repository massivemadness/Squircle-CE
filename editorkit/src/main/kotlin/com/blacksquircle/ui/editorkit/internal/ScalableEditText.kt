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
import android.util.AttributeSet
import android.view.MotionEvent
import com.blacksquircle.ui.editorkit.R
import com.blacksquircle.ui.editorkit.utils.scaledDensity
import kotlin.math.sqrt

abstract class ScalableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : ScrollableEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val MIN_SIZE = 10f
        private const val MAX_SIZE = 20f
    }

    private var pinchFactor = 1f
    private var isDoingPinchZoom = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> isDoingPinchZoom = false
            MotionEvent.ACTION_MOVE -> {
                if (editorConfig.pinchZoom && event.pointerCount == 2) {
                    val distance = getDistanceBetweenTouches(event)
                    if (!isDoingPinchZoom) {
                        pinchFactor = textSize / context.scaledDensity / distance
                        isDoingPinchZoom = true
                    }
                    return validateTextSize(pinchFactor * distance)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getDistanceBetweenTouches(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun validateTextSize(size: Float): Boolean {
        textSize = when {
            size < MIN_SIZE -> MIN_SIZE
            size > MAX_SIZE -> MAX_SIZE
            else -> size
        }
        return true
    }
}