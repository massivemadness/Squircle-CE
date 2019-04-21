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

package com.lightteam.modpeide.presentation.main.customview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import com.lightteam.modpeide.presentation.main.customview.internal.LinesCollection
import com.lightteam.modpeide.utils.extensions.toPx

class TextProcessor(context: Context, attrs: AttributeSet) : AppCompatMultiAutoCompleteTextView(context, attrs) {

    data class Configuration(
        var fontSize: Float = 14f,
        var fontType: Typeface = Typeface.MONOSPACE,

        var highlightCurrentLine: Boolean = true,

        var softKeyboard: Boolean = false,
        var imeKeyboard: Boolean = false
    )

    data class Theme(
        var textColor: Int = Color.WHITE,
        var backgroundColor: Int = Color.DKGRAY,
        var gutterColor: Int = Color.GRAY,
        var gutterTextColor: Int = Color.WHITE,
        var selectedLineColor: Int = Color.GRAY,
        var selectionColor: Int = Color.LTGRAY,

        var searchSpanColor: Int = Color.GREEN,
        var bracketSpanColor: Int = Color.GREEN,

        //Syntax Highlighting
        var numbersColor: Int = Color.WHITE,
        var symbolsColor: Int = Color.WHITE,
        var bracketsColor: Int = Color.WHITE,
        var keywordsColor: Int = Color.WHITE,
        var methodsColor: Int = Color.WHITE,
        var stringsColor: Int = Color.WHITE,
        var commentsColor: Int = Color.WHITE
    )

    private val clipboardManager
            = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private val lines = LinesCollection()
    private val editableFactory = Editable.Factory.getInstance()

    private val selectedLinePaint = Paint()
    private val gutterPaint = Paint()
    private val gutterLinePaint = Paint()
    private val gutterTextPaint = Paint()

    private var gutterWidth = 0
    private var gutterDigitCount = 0
    private var gutterMargin = 4.toPx()

    private var textChangeStart: Int = 0
    private var textChangeEnd: Int = 0
    private var textChangedNewText: String = ""

    private var facadeText = editableFactory.newEditable("")

    // region INIT

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                textChangeStart = start
                textChangeEnd = start + count
            }
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                textChangedNewText = text?.subSequence(start, start + count).toString()
                replaceText(textChangeStart, textChangeEnd, textChangedNewText)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        colorize()
    }

    var configuration: Configuration = Configuration()
        set(value) {
            field = value
            configure()
        }

    var theme: Theme = Theme()
        set(value) {
            field = value
            colorize()
        }

    private fun configure() {
        imeOptions = if(configuration.softKeyboard) {
            0 //Normal
        } else {
            EditorInfo.IME_FLAG_NO_EXTRACT_UI
        }
        inputType = if (configuration.imeKeyboard) {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
        } else {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        textSize = configuration.fontSize
        typeface = configuration.fontType

        gutterTextPaint.textSize = textSize
        gutterTextPaint.typeface = typeface
    }

    private fun colorize() {
        post {
            setTextColor(theme.textColor)
            setBackgroundColor(theme.backgroundColor)
            highlightColor = theme.selectionColor

            selectedLinePaint.color = theme.selectedLineColor
            selectedLinePaint.isAntiAlias = false
            selectedLinePaint.isDither = false

            gutterPaint.color = theme.gutterColor
            gutterPaint.isAntiAlias = false
            gutterPaint.isDither = false

            gutterLinePaint.color = theme.gutterTextColor
            gutterLinePaint.isAntiAlias = false
            gutterLinePaint.isDither = false
            gutterLinePaint.style = Paint.Style.STROKE

            gutterTextPaint.color = theme.gutterTextColor
            gutterTextPaint.isAntiAlias = true
            gutterTextPaint.isDither = false
            gutterTextPaint.textAlign = Paint.Align.RIGHT
        }
    }

    // endregion INIT

    // region CORE

    fun getFacadeText(): Editable {
        return facadeText
    }

    fun setFacadeText(newText: String) {
        var line = 0
        var lineStart = 0
        newText.lines().forEach {
            lines.add(line, lineStart)
            lineStart += it.length + 1
            line++
        }
        newText.removeSuffix("\n") //important
        setText(newText)
        facadeText.clear()
        replaceText(0, facadeText.length, newText)
    }

    override fun onDraw(canvas: Canvas) {
        var top: Int
        if(layout != null) {
            if (configuration.highlightCurrentLine) {
                val currentLineStart = lines.getLineForIndex(selectionStart)
                if (currentLineStart == lines.getLineForIndex(selectionEnd)) {
                    val selectedLineStartIndex = getIndexForStartOfLine(currentLineStart)
                    val selectedLineEndIndex = getIndexForEndOfLine(currentLineStart)

                    val topVisualLine = layout.getLineForOffset(selectedLineStartIndex)
                    val bottomVisualLine = layout.getLineForOffset(selectedLineEndIndex)

                    top = layout.getLineTop(topVisualLine) + paddingTop
                    val right = layout.width + paddingLeft + paddingRight
                    val bottom = layout.getLineBottom(bottomVisualLine) + paddingTop
                    canvas.drawRect(
                        gutterWidth.toFloat(),
                        top.toFloat(),
                        right.toFloat(),
                        bottom.toFloat(),
                        selectedLinePaint
                    )
                }
            }
        }
        super.onDraw(canvas)
        if(layout != null) {
            canvas.drawRect(
                scrollX.toFloat(),
                scrollY.toFloat(),
                (gutterWidth + scrollX).toFloat(),
                (scrollY + height).toFloat(),
                gutterPaint
            )
            var i = getTopVisibleLine()
            if (i >= 2) {
                i -= 2
            } else {
                i = 0
            }
            var prevLineNumber = -1
            val textRight = (gutterWidth - gutterMargin / 2) + scrollX
            while (i <= getBottomVisibleLine()) {
                val number = lines.getLineForIndex(layout.getLineStart(i))
                if (number != prevLineNumber) {
                    canvas.drawText(
                        Integer.toString(number + 1),
                        textRight.toFloat(),
                        (layout.getLineBaseline(i) + paddingTop).toFloat(),
                        gutterTextPaint
                    )
                }
                prevLineNumber = number
                i++
            }
            top = scrollY
            canvas.drawLine(
                (gutterWidth + scrollX).toFloat(),
                top.toFloat(),
                (gutterWidth + scrollX).toFloat(),
                (top + height).toFloat(),
                gutterLinePaint
            )
            updateGutter()
        }
    }

    // endregion CORE

    // region METHODS

    fun hasPrimaryClip(): Boolean = clipboardManager.hasPrimaryClip()

    fun clearText() {
        setFacadeText("")
    }

    fun insert(delta: CharSequence) {
        var selectionStart = Math.max(0, selectionStart)
        var selectionEnd = Math.max(0, selectionEnd)

        selectionStart = Math.min(selectionStart, selectionEnd)
        selectionEnd = Math.max(selectionStart, selectionEnd)

        editableText.delete(selectionStart, selectionEnd)
        editableText.insert(selectionStart, delta)
        //text.replace(selectionStart, selectionEnd, delta)
    }

    fun cut() {
        clipboardManager.primaryClip = ClipData.newPlainText("CUT", selectedText())
        editableText.replace(selectionStart, selectionEnd, "")
    }

    fun copy() {
        clipboardManager.primaryClip = ClipData.newPlainText("COPY", selectedText())
    }

    fun paste() {
        val clip = clipboardManager.primaryClip?.getItemAt(0)?.coerceToText(context)
        editableText.replace(selectionStart, selectionEnd, clip)
    }

    fun selectLine() {
        var start = Math.min(selectionStart, selectionEnd)
        var end = Math.max(selectionStart, selectionEnd)
        if (end > start) {
            end--
        }
        while (end < text.length && text[end] != '\n') {
            end++
        }
        while (start > 0 && text[start - 1] != '\n') {
            start--
        }
        setSelection(start, end)
    }

    fun deleteLine() {
        var start = Math.min(selectionStart, selectionEnd)
        var end = Math.max(selectionStart, selectionEnd)
        if (end > start) {
            end--
        }
        while (end < text.length && text[end] != '\n') {
            end++
        }
        while (start > 0 && text[start - 1] != '\n') {
            start--
        }
        editableText.delete(start, end)
    }

    fun duplicateLine() {
        var start = Math.min(selectionStart, selectionEnd)
        var end = Math.max(selectionStart, selectionEnd)
        if (end > start) {
            end--
        }
        while (end < text.length && text[end] != '\n') {
            end++
        }
        while (start > 0 && text[start - 1] != '\n') {
            start--
        }
        editableText.insert(end, "\n" + text.subSequence(start, end))
    }

    // endregion METHODS

    // region LINE_NUMBERS

    private fun updateGutter() {
        var max = 3
        val paint = layout.paint
        if(paint != null) {
            var widestNumber = 0
            var widestWidth = 0f

            gutterDigitCount = Integer.toString(lines.lineCount).length
            for (i in 0..9) {
                val width = paint.measureText(Integer.toString(i))
                if (width > widestWidth) {
                    widestNumber = i
                    widestWidth = width
                }
            }
            if (gutterDigitCount >= max) {
                max = gutterDigitCount
            }
            val builder = StringBuilder()
            for (i in 0 until max) {
                builder.append(Integer.toString(widestNumber))
            }
            gutterWidth = paint.measureText(builder.toString()).toInt()
            gutterWidth += gutterMargin
            if (paddingLeft != gutterWidth + gutterMargin) {
                setPadding(gutterWidth + gutterMargin, gutterMargin, paddingRight, 0)
            }
        }
    }

    private fun getTopVisibleLine(): Int {
        if (lineHeight == 0) {
            return 0
        }
        val line = scrollY / lineHeight
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else {
            line
        }
    }

    private fun getBottomVisibleLine(): Int {
        if (lineHeight == 0) {
            return 0
        }
        val line = Math.abs((scrollY + height) / lineHeight) + 1
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else {
            line
        }
    }

    private fun getIndexForStartOfLine(lineNumber: Int): Int {
        return lines.getIndexForLine(lineNumber)
    }

    private fun getIndexForEndOfLine(lineNumber: Int): Int {
        return if (lineNumber == lineCount - 1) {
            facadeText.length
        } else {
            lines.getIndexForLine(lineNumber + 1) - 1
        }
    }

    private fun replaceText(newStart: Int, newEnd: Int, newText: CharSequence) {
        var start = if (newStart < 0) 0 else newStart
        var end = if (newEnd >= facadeText.length) facadeText.length else newEnd

        val newCharCount = newText.length - (end - start)
        val startLine = lines.getLineForIndex(start)

        var i = start
        while (i < end) {
            if (facadeText[i] == '\n') {
                lines.remove(startLine + 1)
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
        if (start > facadeText.length) {
            start = facadeText.length
        }
        if (end > facadeText.length) {
            end = facadeText.length
        }
        if (start < 0) {
            start = 0
        }
        if (end < 0) {
            end = 0
        }
        facadeText.replace(start, end, newText)
    }

    // endregion LINE_NUMBERS

    // region INTERNAL

    /*private fun invalidateVisibleArea() {
        invalidate(
            paddingLeft,
            scrollY + paddingTop,
            width,
            scrollY + paddingTop + height
        )
    }*/

    private fun selectedText(): Editable {
        return text.subSequence(selectionStart, selectionEnd) as Editable
    }

    // endregion INTERNAL
}