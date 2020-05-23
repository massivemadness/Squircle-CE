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

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.lightteam.editorkit.R
import kotlin.math.sqrt

open class ScalableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : ScrollableEditText(context, attrs, defStyleAttr) {

    var isDoingPinchZoom = false

    private val scaledDensity = context.resources.displayMetrics.scaledDensity

    private var pinchFactor = 1f

    override fun configure() {
        super.configure()
        // TODO Move to onTouchEvent()
        setOnTouchListener { _, event ->
            if (config.pinchZoom) {
                return@setOnTouchListener pinchZoom(event)
            }
            return@setOnTouchListener false
        }
    }

    private fun pinchZoom(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> isDoingPinchZoom = false
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    val distance = getDistanceBetweenTouches(event)
                    if (!isDoingPinchZoom) {
                        pinchFactor = textSize / scaledDensity / distance
                        isDoingPinchZoom = true
                    }
                    validateTextSize(pinchFactor * distance)
                }
            }
        }
        return isDoingPinchZoom
    }

    private fun getDistanceBetweenTouches(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun validateTextSize(size: Float) {
        textSize = when {
            size < 10 -> 10f // minimum
            size > 20 -> 20f // maximum
            else -> size
        }
    }
}