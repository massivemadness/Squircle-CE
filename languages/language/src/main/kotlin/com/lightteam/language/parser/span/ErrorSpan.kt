package com.lightteam.language.parser.span

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