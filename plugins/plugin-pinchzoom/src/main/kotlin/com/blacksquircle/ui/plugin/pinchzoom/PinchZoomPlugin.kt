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

package com.blacksquircle.ui.plugin.pinchzoom

import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import com.blacksquircle.ui.plugin.base.EditorPlugin
import kotlin.math.sqrt

class PinchZoomPlugin : EditorPlugin(PINCHZOOM_ID) {

    private var isDoingPinchZoom = false
    private var pinchFactor = 1f

    override fun onAttached(editText: EditText) {
        super.onAttached(editText)
        Log.d(TAG, "PinchZoom plugin loaded successfully!")
    }

    override fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> isDoingPinchZoom = false
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    val distance = getDistanceBetweenTouches(event)
                    if (!isDoingPinchZoom) {
                        val scaledDensity = requireContext()
                            .resources.displayMetrics.scaledDensity
                        val textSize = editText?.textSize ?: MIN_SIZE

                        pinchFactor = textSize / scaledDensity / distance
                        isDoingPinchZoom = true
                    } else {
                        updateTextSize(pinchFactor * distance)
                    }
                }
            }
        }
    }

    private fun getDistanceBetweenTouches(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun updateTextSize(size: Float) {
        editText?.textSize = when {
            size < MIN_SIZE -> MIN_SIZE
            size > MAX_SIZE -> MAX_SIZE
            else -> size
        }
    }

    companion object {

        private const val TAG = "PinchZoomPlugin"

        private const val MIN_SIZE = 10f
        private const val MAX_SIZE = 20f
    }
}