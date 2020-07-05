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
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.lightteam.editorkit.R
import com.lightteam.editorkit.feature.colorscheme.ColorScheme
import com.lightteam.editorkit.feature.linenumbers.LinesCollection
import com.lightteam.editorkit.utils.TextChangeListener
import com.lightteam.editorkit.utils.dpToPx
import kotlin.math.abs

open class LineNumbersEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : ScalableEditText(context, attrs, defStyleAttr), TextChangeListener {

    val lines = LinesCollection()
    val arrayLineCount: Int
        get() = lines.lineCount - 1

    var isReadyToDraw = false
    var colorScheme: ColorScheme? = null
        set(value) {
            field = value
            colorize()
        }

    private val selectedLinePaint = Paint()
    private val gutterPaint = Paint()
    private val gutterDividerPaint = Paint()
    private val gutterCurrentLineNumberPaint = Paint()
    private val gutterTextPaint = Paint()

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            doBeforeTextChanged(s, start, count, after)
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            doOnTextChanged(s, start, before, count)
        }
        override fun afterTextChanged(s: Editable?) {
            doAfterTextChanged(s)
        }
    }

    private var textChangeStart = 0
    private var textChangeEnd = 0
    private var textChangedNewText = ""

    protected var gutterWidth = 0
    private var gutterDigitCount = 0
    private var gutterMargin = 4.dpToPx()

    protected var processedText: Editable = Editable.Factory.getInstance().newEditable("")

    override fun onDraw(canvas: Canvas?) {
        if (layout != null && isReadyToDraw) {
            val currentLineStart = lines.getLineForIndex(selectionStart)
            if (config.highlightCurrentLine) {
                if (currentLineStart == lines.getLineForIndex(selectionEnd)) {
                    val selectedLineStartIndex = getIndexForStartOfLine(currentLineStart)
                    val selectedLineEndIndex = getIndexForEndOfLine(currentLineStart)
                    val topVisualLine = layout.getLineForOffset(selectedLineStartIndex)
                    val bottomVisualLine = layout.getLineForOffset(selectedLineEndIndex)

                    val lineTop = layout.getLineTop(topVisualLine) + paddingTop
                    val width = layout.width + paddingLeft + paddingRight
                    val lineBottom = layout.getLineBottom(bottomVisualLine) + paddingTop
                    canvas?.drawRect(
                        gutterWidth.toFloat(),
                        lineTop.toFloat(),
                        width.toFloat(),
                        lineBottom.toFloat(),
                        selectedLinePaint
                    )
                }
            }
            updateGutter()
            super.onDraw(canvas)
            canvas?.drawRect(
                scrollX.toFloat(),
                scrollY.toFloat(),
                (gutterWidth + scrollX).toFloat(),
                (scrollY + height).toFloat(),
                gutterPaint
            )
            val bottomVisibleLine = getBottomVisibleLine()
            var topVisibleLine = getTopVisibleLine()
            if (topVisibleLine >= 2) {
                topVisibleLine -= 2
            } else {
                topVisibleLine = 0
            }
            var prevLineNumber = -1
            val textRight = (gutterWidth - gutterMargin / 2) + scrollX
            while (topVisibleLine <= bottomVisibleLine) {
                val number = lines.getLineForIndex(layout.getLineStart(topVisibleLine))
                if (number != prevLineNumber) {
                    canvas?.drawText(
                        (number + 1).toString(),
                        textRight.toFloat(),
                        (layout.getLineBaseline(topVisibleLine) + paddingTop).toFloat(),
                        if (number == currentLineStart) {
                            gutterCurrentLineNumberPaint
                        } else {
                            gutterTextPaint
                        }
                    )
                }
                prevLineNumber = number
                topVisibleLine++
            }
            canvas?.drawLine(
                (gutterWidth + scrollX).toFloat(),
                scrollY.toFloat(),
                (gutterWidth + scrollX).toFloat(),
                (scrollY + height).toFloat(),
                gutterDividerPaint
            )
        }
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        post {
            gutterCurrentLineNumberPaint.textSize = textSize
            gutterTextPaint.textSize = textSize
        }
    }

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        post {
            gutterCurrentLineNumberPaint.typeface = tf
            gutterTextPaint.typeface = tf
        }
    }

    override fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        textChangeStart = start
        textChangeEnd = start + count
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        textChangedNewText = text?.subSequence(start, start + count).toString()
        replaceText(textChangeStart, textChangeEnd, textChangedNewText)
    }

    override fun doAfterTextChanged(text: Editable?) {
        updateGutter()
    }

    open fun colorize() {
        colorScheme?.let {
            setTextColor(it.textColor)
            setBackgroundColor(it.backgroundColor)
            highlightColor = it.selectionColor

            selectedLinePaint.color = it.selectedLineColor
            selectedLinePaint.isAntiAlias = false
            selectedLinePaint.isDither = false

            gutterPaint.color = it.gutterColor
            gutterPaint.isAntiAlias = false
            gutterPaint.isDither = false

            gutterDividerPaint.color = it.gutterDividerColor
            gutterDividerPaint.isAntiAlias = false
            gutterDividerPaint.isDither = false
            gutterDividerPaint.style = Paint.Style.STROKE
            gutterDividerPaint.strokeWidth = 2.6f

            gutterCurrentLineNumberPaint.color = it.gutterCurrentLineNumberColor
            gutterCurrentLineNumberPaint.isAntiAlias = true
            gutterCurrentLineNumberPaint.isDither = false
            gutterCurrentLineNumberPaint.textAlign = Paint.Align.RIGHT

            gutterTextPaint.color = it.gutterTextColor
            gutterTextPaint.isAntiAlias = true
            gutterTextPaint.isDither = false
            gutterTextPaint.textAlign = Paint.Align.RIGHT

            isReadyToDraw = true
        }
    }

    open fun processText(newText: String) {
        abortFling()
        removeTextChangedListener(textWatcher)
        setText(newText)

        processedText.clear()
        replaceText(0, processedText.length, newText)
        lines.clear()

        var lineNumber = 0
        var lineStart = 0
        newText.lines().forEach {
            addLine(lineNumber, lineStart, it.length)
            lineStart += it.length + 1
            lineNumber++
        }
        lines.add(lineNumber, lineStart)
        addTextChangedListener(textWatcher)
    }

    open fun clearText() {
        processText("")
    }

    open fun replaceText(newStart: Int, newEnd: Int, newText: CharSequence) {
        var start = if (newStart < 0) 0 else newStart
        var end = if (newEnd >= processedText.length) processedText.length else newEnd

        val newCharCount = newText.length - (end - start)
        val startLine = lines.getLineForIndex(start)

        var i = start
        while (i < end) {
            if (processedText[i] == '\n') {
                removeLine(startLine + 1)
            }
            i++
        }
        lines.shiftIndexes(lines.getLineForIndex(start) + 1, newCharCount)
        i = 0
        while (i < newText.length) {
            if (newText[i] == '\n') {
                lines.add(lines.getLineForIndex(start + i) + 1, start + i + 1)
            }
            i++
        }
        if (start > end) {
            end = start
        }
        if (start > processedText.length) {
            start = processedText.length
        }
        if (end > processedText.length) {
            end = processedText.length
        }
        if (start < 0) {
            start = 0
        }
        if (end < 0) {
            end = 0
        }
        processedText.replace(start, end, newText)
    }

    open fun addLine(lineNumber: Int, lineStart: Int, lineLength: Int) {
        lines.add(lineNumber, lineStart)
    }

    open fun removeLine(line: Int) {
        lines.remove(line)
    }

    fun getProcessedText(): String {
        return processedText.toString()
    }

    fun getIndexForStartOfLine(lineNumber: Int): Int {
        return lines.getIndexForLine(lineNumber)
    }

    fun getIndexForEndOfLine(lineNumber: Int): Int {
        return if (lineNumber == lineCount - 1) {
            processedText.length
        } else {
            lines.getIndexForLine(lineNumber + 1) - 1
        }
    }

    fun getTopVisibleLine(): Int {
        if (lineHeight == 0) {
            return 0
        }
        val line = scrollY / lineHeight
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else line
    }

    fun getBottomVisibleLine(): Int {
        if (lineHeight == 0) {
            return 0
        }
        val line = abs((scrollY + height) / lineHeight) + 1
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else line
    }

    private fun updateGutter() {
        var count = 3
        var widestNumber = 0
        var widestWidth = 0f

        gutterDigitCount = lines.lineCount.toString().length
        for (i in 0..9) {
            val width = paint.measureText(i.toString())
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
        gutterWidth = paint.measureText(builder.toString()).toInt()
        gutterWidth += gutterMargin
        if (paddingLeft != gutterWidth + gutterMargin) {
            setPadding(gutterWidth + gutterMargin, gutterMargin, paddingRight, 0)
        }
    }
}