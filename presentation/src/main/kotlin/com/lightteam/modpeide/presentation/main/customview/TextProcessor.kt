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

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import com.lightteam.modpeide.presentation.main.customview.internal.linenumbers.LinesCollection
import com.lightteam.modpeide.presentation.main.customview.internal.syntaxhighlight.StyleSpan
import com.lightteam.modpeide.presentation.main.customview.internal.syntaxhighlight.SyntaxHighlightSpan
import com.lightteam.modpeide.presentation.main.customview.internal.syntaxhighlight.language.JavaScript
import com.lightteam.modpeide.presentation.main.customview.internal.syntaxhighlight.language.Language
import com.lightteam.modpeide.presentation.main.customview.internal.textscroller.OnScrollChangedListener
import com.lightteam.modpeide.utils.extensions.toPx

class TextProcessor(context: Context, attrs: AttributeSet) : AppCompatMultiAutoCompleteTextView(context, attrs) {

    data class Configuration(
        var fontSize: Float = 14f,
        var fontType: Typeface = Typeface.MONOSPACE,

        var wordWrap: Boolean = true,
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

    private val textScroller = Scroller(context)

    private val clipboardManager
            = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private val lines = LinesCollection()
    private val editableFactory = Editable.Factory.getInstance()

    private val selectedLinePaint = Paint()
    private val gutterPaint = Paint()
    private val gutterLinePaint = Paint()
    private val gutterTextPaint = Paint()

    private val language: Language = JavaScript()

    private val numbersSpan = StyleSpan(color = theme.numbersColor)
    private val symbolsSpan = StyleSpan(color = theme.symbolsColor)
    private val bracketsSpan = StyleSpan(color = theme.bracketsColor)
    private val keywordsSpan = StyleSpan(color = theme.keywordsColor)
    private val methodsSpan = StyleSpan(color = theme.methodsColor)
    private val stringsSpan = StyleSpan(color = theme.stringsColor)
    private val commentsSpan = StyleSpan(color = theme.commentsColor, italic = true)

    private var scrollListeners = arrayOf<OnScrollChangedListener>()
    private var maximumVelocity = 0f

    private var gutterWidth = 0
    private var gutterDigitCount = 0
    private var gutterMargin = 4.toPx() //4 dp to pixels

    private var textChangeStart = 0
    private var textChangeEnd = 0
    private var textChangedNewText = ""

    private var topDirtyLine = 0
    private var bottomDirtyLine = 0

    private var facadeText = editableFactory.newEditable("")

    private var velocityTracker: VelocityTracker? = null

    // region INIT

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                textChangeStart = start
                textChangeEnd = start + count
                abortFling()
            }
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                textChangedNewText = text?.subSequence(start, start + count).toString()
                replaceText(textChangeStart, textChangeEnd, textChangedNewText)
            }
            override fun afterTextChanged(s: Editable?) {
                clearSpans()
                syntaxHighlight()
            }
        })
        maximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity * 100f
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

        setHorizontallyScrolling(!configuration.wordWrap)

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

            numbersSpan.color = theme.numbersColor
            symbolsSpan.color = theme.symbolsColor
            bracketsSpan.color = theme.bracketsColor
            keywordsSpan.color = theme.keywordsColor
            methodsSpan.color = theme.methodsColor
            stringsSpan.color = theme.stringsColor
            commentsSpan.color = theme.commentsColor
        }
    }

    // endregion INIT

    // region CORE

    override fun onDraw(canvas: Canvas) {
        if(layout != null) {
            var top: Int
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
            super.onDraw(canvas)
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

    // region SYNTAX_HIGHLIGHT

    private fun syntaxHighlight() {
        if(layout != null) {
            var topLine = scrollY / lineHeight - 10
            var bottomLine = (scrollY + height) / lineHeight + 11
            if (topLine < 0) {
                topLine = 0
            }
            if (bottomLine > layout.lineCount) {
                bottomLine = layout.lineCount
            }
            if (topLine > layout.lineCount) {
                topLine = layout.lineCount
            }
            if (bottomLine >= 0 && topLine >= 0) {
                topDirtyLine = topLine
                bottomDirtyLine = bottomLine

                val topLineOffset = layout.getLineStart(topLine)
                val bottomLineOffset = if (bottomLine < lineCount) {
                    layout.getLineStart(bottomLine)
                } else {
                    layout.getLineStart(lineCount)
                }

                // region HIGHLIGHTING

                var matcher = language.getPatternOfNumbers().matcher( //Numbers
                    text.subSequence(topLineOffset, bottomLineOffset)
                )
                while (matcher.find()) {
                    text.setSpan(
                        SyntaxHighlightSpan(numbersSpan, topLineOffset, bottomLineOffset),
                        matcher.start() + topLineOffset, matcher.end() + topLineOffset,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                matcher = language.getPatternOfSymbols().matcher( //Symbols
                    text.subSequence(topLineOffset, bottomLineOffset)
                )
                while (matcher.find()) {
                    text.setSpan(
                        SyntaxHighlightSpan(symbolsSpan, topLineOffset, bottomLineOffset),
                        matcher.start() + topLineOffset, matcher.end() + topLineOffset,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                matcher = language.getPatternOfBrackets().matcher( //Brackets
                    text.subSequence(topLineOffset, bottomLineOffset)
                )
                while (matcher.find()) {
                    text.setSpan(
                        SyntaxHighlightSpan(bracketsSpan, topLineOffset, bottomLineOffset),
                        matcher.start() + topLineOffset, matcher.end() + topLineOffset,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                matcher = language.getPatternOfKeywords().matcher( //Keywords
                    text.subSequence(topLineOffset, bottomLineOffset)
                )
                while (matcher.find()) {
                    text.setSpan(
                        SyntaxHighlightSpan(keywordsSpan, topLineOffset, bottomLineOffset),
                        matcher.start() + topLineOffset, matcher.end() + topLineOffset,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                matcher = language.getPatternOfMethods().matcher( //Methods
                    text.subSequence(topLineOffset, bottomLineOffset)
                )
                while (matcher.find()) {
                    text.setSpan(
                        SyntaxHighlightSpan(methodsSpan, topLineOffset, bottomLineOffset),
                        matcher.start() + topLineOffset, matcher.end() + topLineOffset,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                matcher = language.getPatternOfStrings().matcher( //Strings
                    text.subSequence(topLineOffset, bottomLineOffset)
                )
                while (matcher.find()) {
                    for (span in text.getSpans(
                        matcher.start() + topLineOffset,
                        matcher.end() + topLineOffset,
                        ForegroundColorSpan::class.java)) {
                        text.removeSpan(span)
                    }
                    text.setSpan(
                        SyntaxHighlightSpan(stringsSpan, topLineOffset, bottomLineOffset),
                        matcher.start() + topLineOffset, matcher.end() + topLineOffset,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                matcher = language.getPatternOfComments().matcher( //Comments
                    text.subSequence(topLineOffset, bottomLineOffset)
                )
                while (matcher.find()) {
                    var skip = false
                    for (span in text.getSpans(
                        topLineOffset,
                        matcher.end() + topLineOffset,
                        ForegroundColorSpan::class.java)) {
                        val spanStart = text.getSpanStart(span)
                        val spanEnd = text.getSpanEnd(span)
                        if (matcher.start() + topLineOffset in spanStart..spanEnd &&
                            matcher.end() + topLineOffset > spanEnd ||
                            matcher.start() + topLineOffset >= topLineOffset + spanEnd &&
                            matcher.start() + topLineOffset <= spanEnd) {
                            skip = true
                            break
                        }

                    }
                    if (!skip) {
                        for (span in text.getSpans(
                            matcher.start() + topLineOffset,
                            matcher.end() + topLineOffset,
                            ForegroundColorSpan::class.java)) {
                            text.removeSpan(span)
                        }
                        text.setSpan(
                            SyntaxHighlightSpan(commentsSpan, topLineOffset, bottomLineOffset),
                            matcher.start() + topLineOffset, matcher.end() + topLineOffset,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                // endregion HIGHLIGHTING

                post(::invalidateVisibleArea)
            }
        }
    }

    private fun clearSpans() {
        val spans = text.getSpans(0, text.length, SyntaxHighlightSpan::class.java)
        for (span in spans) {
            text.removeSpan(span)
        }
    }

    private fun invalidateVisibleArea() {
        invalidate(
            paddingLeft,
            scrollY + paddingTop,
            width,
            scrollY + paddingTop + height
        )
    }

    // endregion SYNTAX_HIGHLIGHT

    // region SCROLLER

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        syntaxHighlight()
        for (listener in scrollListeners) {
            listener.onScrollChanged(scrollX, scrollY, scrollX, scrollY)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                abortFling()
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                } else {
                    velocityTracker?.clear()
                }
                super.onTouchEvent(event)
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.computeCurrentVelocity(1000, maximumVelocity)
                val velocityX: Int? = if(configuration.wordWrap) {
                    0
                } else {
                    velocityTracker?.xVelocity?.toInt()
                }
                val velocityY: Int? = velocityTracker?.yVelocity?.toInt()
                if(velocityX != null && velocityY != null) {
                    if (Math.abs(velocityY) >= 0 || Math.abs(velocityX) >= 0) {
                        if (layout == null) {
                            return super.onTouchEvent(event)
                        }
                        textScroller.fling(
                            scrollX, scrollY,
                            -velocityX, -velocityY,
                            0, layout.width - width + paddingLeft + paddingRight,
                            0, layout.height - height + paddingTop + paddingBottom
                        )
                    } else if (velocityTracker != null) {
                        velocityTracker?.recycle()
                        velocityTracker = null
                    }
                }
                super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                super.onTouchEvent(event)
            }
            else -> super.onTouchEvent(event)
        }
        return true
    }

    override fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        for (listener in scrollListeners) {
            listener.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        }
        if (topDirtyLine > getTopVisibleLine() || bottomDirtyLine < getBottomVisibleLine()) {
            clearSpans()
            syntaxHighlight()
        }
    }

    override fun computeScroll() {
        if (textScroller.computeScrollOffset()) {
            scrollTo(textScroller.currX, textScroller.currY)
        }
    }

    fun addOnScrollChangedListener(listener: OnScrollChangedListener) {
        val newListener = arrayOfNulls<OnScrollChangedListener>(scrollListeners.size + 1)
        val length = scrollListeners.size
        System.arraycopy(scrollListeners, 0, newListener, 0, length)
        newListener[newListener.size - 1] = listener
        scrollListeners = newListener.requireNoNulls()
    }

    fun abortFling() {
        if (!textScroller.isFinished) {
            textScroller.abortAnimation()
        }
    }

    // endregion SCROLLER

    // region INTERNAL

    private fun selectedText(): Editable {
        return text.subSequence(selectionStart, selectionEnd) as Editable
    }

    // endregion INTERNAL
}