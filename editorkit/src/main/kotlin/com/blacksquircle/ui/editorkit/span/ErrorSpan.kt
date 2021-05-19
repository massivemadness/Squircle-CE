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

package com.blacksquircle.ui.editorkit.span

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.LineBackgroundSpan

class ErrorSpan(
    private val lineWidth: Float = 1 * Resources.getSystem().displayMetrics.density + 0.5f,
    private val waveSize: Float = 3 * Resources.getSystem().displayMetrics.density + 0.5f,
    private val color: Int = Color.RED
) : LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val width = paint.measureText(text, start, end)
        val linePaint = Paint(paint)
        linePaint.color = color
        linePaint.strokeWidth = lineWidth
        val doubleWaveSize = waveSize * 2
        var i = left.toFloat()
        while (i < left + width) {
            canvas.drawLine(i, bottom.toFloat(), i + waveSize, bottom - waveSize, linePaint)
            canvas.drawLine(i + waveSize, bottom - waveSize, i + doubleWaveSize, bottom.toFloat(), linePaint)
            i += doubleWaveSize
        }
    }
}