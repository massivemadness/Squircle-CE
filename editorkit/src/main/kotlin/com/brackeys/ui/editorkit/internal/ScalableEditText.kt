/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.editorkit.internal

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.brackeys.ui.editorkit.R
import com.brackeys.ui.editorkit.utils.scaledDensity
import kotlin.math.sqrt

abstract class ScalableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : ScrollableEditText(context, attrs, defStyleAttr) {

    private var pinchFactor = 1f
    private var isDoingPinchZoom = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> isDoingPinchZoom = false
            MotionEvent.ACTION_MOVE -> {
                if (config.pinchZoom && event.pointerCount == 2) {
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
            size < 10 -> 10f // minimum
            size > 20 -> 20f // maximum
            else -> size
        }
        return true
    }
}