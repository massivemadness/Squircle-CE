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

package com.blacksquircle.ui.editorkit.plugin.linenumbers

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Editable
import android.util.Log
import com.blacksquircle.ui.editorkit.plugin.base.EditorPlugin
import com.blacksquircle.ui.editorkit.utils.bottomVisibleLine
import com.blacksquircle.ui.editorkit.utils.topVisibleLine
import com.blacksquircle.ui.editorkit.widget.TextProcessor
import com.blacksquircle.ui.language.base.model.ColorScheme

class LineNumbersPlugin : EditorPlugin(PLUGIN_ID) {

    var lineNumbers = true
    var highlightCurrentLine = true

    private val selectedLinePaint = Paint()
    private val gutterPaint = Paint()
    private val gutterDividerPaint = Paint()
    private val gutterCurrentLineNumberPaint = Paint()
    private val gutterTextPaint = Paint()

    private val gutterMargin = 4.dp
    private var gutterWidth = 0
    private var gutterDigitCount = 0

    override fun onAttached(editText: TextProcessor) {
        super.onAttached(editText)
        Log.d(PLUGIN_ID, "LineNumbers plugin loaded successfully!")
    }

    override fun onColorSchemeChanged(colorScheme: ColorScheme) {
        super.onColorSchemeChanged(colorScheme)
        selectedLinePaint.color = colorScheme.selectedLineColor
        selectedLinePaint.isAntiAlias = false
        selectedLinePaint.isDither = false

        gutterPaint.color = colorScheme.gutterColor
        gutterPaint.isAntiAlias = false
        gutterPaint.isDither = false

        gutterDividerPaint.color = colorScheme.gutterDividerColor
        gutterDividerPaint.isAntiAlias = false
        gutterDividerPaint.isDither = false
        gutterDividerPaint.style = Paint.Style.STROKE
        gutterDividerPaint.strokeWidth = 2.6f

        gutterCurrentLineNumberPaint.textSize = requireContext()
            .resources.displayMetrics.scaledDensity * editText.textSize
        gutterCurrentLineNumberPaint.color = colorScheme.gutterCurrentLineNumberColor
        gutterCurrentLineNumberPaint.isAntiAlias = true
        gutterCurrentLineNumberPaint.isDither = false
        gutterCurrentLineNumberPaint.textAlign = Paint.Align.RIGHT

        gutterTextPaint.textSize = requireContext()
            .resources.displayMetrics.scaledDensity * editText.textSize
        gutterTextPaint.color = colorScheme.gutterTextColor
        gutterTextPaint.isAntiAlias = true
        gutterTextPaint.isDither = false
        gutterTextPaint.textAlign = Paint.Align.RIGHT
    }

    override fun beforeDraw(canvas: Canvas?) {
        super.beforeDraw(canvas)
        if (highlightCurrentLine) {
            val currentLineStart = structure.getLineForIndex(editText.selectionStart)
            if (currentLineStart == structure.getLineForIndex(editText.selectionEnd)) {
                if (editText.layout == null) return

                val selectedLineStartIndex = structure.getIndexForStartOfLine(currentLineStart)
                val selectedLineEndIndex = structure.getIndexForEndOfLine(currentLineStart)
                val topVisualLine = editText.layout.getLineForOffset(selectedLineStartIndex)
                val bottomVisualLine = editText.layout.getLineForOffset(selectedLineEndIndex)

                val lineTop = editText.layout.getLineTop(topVisualLine) + editText.paddingTop
                val lineBottom = editText.layout.getLineBottom(bottomVisualLine) + editText.paddingTop
                val width = editText.layout.width + editText.paddingLeft + editText.paddingRight

                canvas?.drawRect(
                    gutterWidth.toFloat(),
                    lineTop.toFloat(),
                    width.toFloat(),
                    lineBottom.toFloat(),
                    selectedLinePaint,
                )
            }
        }
        updateGutter()
    }

    override fun afterDraw(canvas: Canvas?) {
        super.afterDraw(canvas)
        if (lineNumbers) {
            val currentLineStart = structure.getLineForIndex(editText.selectionStart)
            canvas?.drawRect(
                editText.scrollX.toFloat(),
                editText.scrollY.toFloat(),
                (gutterWidth + editText.scrollX).toFloat(),
                (editText.scrollY + editText.height).toFloat(),
                gutterPaint,
            )
            var topVisibleLine = editText.topVisibleLine
            if (topVisibleLine >= 2) {
                topVisibleLine -= 2
            } else {
                topVisibleLine = 0
            }
            var prevLineNumber = -1
            val textRight = (gutterWidth - gutterMargin / 2) + editText.scrollX
            while (topVisibleLine <= editText.bottomVisibleLine) {
                if (editText.layout == null) return
                val number = structure.getLineForIndex(editText.layout.getLineStart(topVisibleLine))
                if (number != prevLineNumber) {
                    canvas?.drawText(
                        (number + 1).toString(),
                        textRight.toFloat(),
                        editText.layout.getLineBaseline(topVisibleLine) + editText.paddingTop.toFloat(),
                        if (number == currentLineStart && highlightCurrentLine) {
                            gutterCurrentLineNumberPaint
                        } else {
                            gutterTextPaint
                        },
                    )
                }
                prevLineNumber = number
                topVisibleLine++
            }
            canvas?.drawLine(
                (gutterWidth + editText.scrollX).toFloat(),
                editText.scrollY.toFloat(),
                (gutterWidth + editText.scrollX).toFloat(),
                (editText.scrollY + editText.height).toFloat(),
                gutterDividerPaint,
            )
        }
    }

    override fun afterTextChanged(text: Editable?) {
        super.afterTextChanged(text)
        updateGutter()
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        gutterCurrentLineNumberPaint.textSize = editText.textSize
        gutterTextPaint.textSize = editText.textSize
    }

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        gutterCurrentLineNumberPaint.typeface = tf
        gutterTextPaint.typeface = tf
    }

    private fun updateGutter() {
        if (lineNumbers) {
            var count = 3
            var widestNumber = 0
            var widestWidth = 0f

            gutterDigitCount = structure.lineCount.toString().length
            for (i in 0..9) {
                val width = editText.paint.measureText(i.toString())
                if (width > widestWidth) {
                    widestNumber = i
                    widestWidth = width
                }
            }
            if (gutterDigitCount >= count) {
                count = gutterDigitCount
            }
            val builder = StringBuilder()
            for (i in 0 until count) {
                builder.append(widestNumber.toString())
            }
            gutterWidth = editText.paint.measureText(builder.toString()).toInt()
            gutterWidth += gutterMargin
        }
        if (editText.paddingStart != gutterWidth + gutterMargin) {
            editText.setPadding(
                gutterWidth + gutterMargin,
                gutterMargin,
                editText.paddingEnd,
                editText.paddingBottom,
            )
        }
    }

    companion object {

        const val PLUGIN_ID = "line-numbers-1141"

        private val Int.dp: Int
            get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}