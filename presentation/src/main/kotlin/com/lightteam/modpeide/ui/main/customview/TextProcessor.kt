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

package com.lightteam.modpeide.ui.main.customview

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.*
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import androidx.core.text.getSpans
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.patterns.completion.ModPECompletion
import com.lightteam.modpeide.data.patterns.completion.UnknownCompletion
import com.lightteam.modpeide.data.patterns.language.JavaScriptLanguage
import com.lightteam.modpeide.ui.main.adapters.CodeCompletionAdapter
import com.lightteam.modpeide.ui.main.customview.internal.codecompletion.SymbolsTokenizer
import com.lightteam.modpeide.data.storage.collection.LinesCollection
import com.lightteam.modpeide.ui.main.customview.internal.syntaxhighlight.StyleSpan
import com.lightteam.modpeide.ui.main.customview.internal.syntaxhighlight.SyntaxHighlightSpan
import com.lightteam.modpeide.domain.patterns.language.Language
import com.lightteam.modpeide.ui.main.customview.internal.textscroller.OnScrollChangedListener
import com.lightteam.modpeide.data.storage.collection.UndoStack
import com.lightteam.modpeide.domain.patterns.completion.CodeCompletion
import com.lightteam.modpeide.utils.extensions.getScaledDensity
import com.lightteam.modpeide.utils.extensions.toPx
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.sqrt

class TextProcessor(context: Context, attrs: AttributeSet) : AppCompatMultiAutoCompleteTextView(context, attrs) {

    data class Configuration(
        //Font
        var fontSize: Float = 14f,
        var fontType: Typeface = Typeface.MONOSPACE,

        //Editor
        var wordWrap: Boolean = true,
        var codeCompletion: Boolean = true,
        var pinchZoom: Boolean = true,
        var highlightCurrentLine: Boolean = true,
        var highlightDelimiters: Boolean = true,

        //Keyboard
        var softKeyboard: Boolean = false,
        var imeKeyboard: Boolean = false,

        //Code Style
        var autoIndentation: Boolean = true,
        var autoCloseBrackets: Boolean = true,
        var autoCloseQuotes: Boolean = false
    )

    data class Theme(
        var textColor: Int = Color.WHITE,
        var backgroundColor: Int = Color.DKGRAY,
        var gutterColor: Int = Color.GRAY,
        var gutterTextColor: Int = Color.WHITE,
        var gutterDividerColor: Int = Color.WHITE,
        var gutterCurrentLineNumberColor: Int = Color.GRAY,
        var selectedLineColor: Int = Color.GRAY,
        var selectionColor: Int = Color.LTGRAY,
        var filterableColor: Int = Color.DKGRAY,

        var searchBgColor: Int = Color.GREEN,
        var bracketsBgColor: Int = Color.GREEN,

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

    var undoStack = UndoStack()
    var redoStack = UndoStack()

    var language: Language = JavaScriptLanguage() //= UnknownLanguage()
    var completion: CodeCompletion = UnknownCompletion()

    private val textScroller = Scroller(context)
    private val completionAdapter = CodeCompletionAdapter(context, R.layout.item_completion)

    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val scaledDensity = context.getScaledDensity()
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            textChangeStart = start
            textChangeEnd = start + count
            if (!isDoingUndoRedo) {
                if (count < UndoStack.MAX_SIZE) {
                    textLastChange = UndoStack.TextChange()
                    textLastChange?.oldText = s?.subSequence(start, start + count).toString()
                    textLastChange?.start = start
                    return
                }
                undoStack.removeAll()
                redoStack.removeAll()
                textLastChange = null
            }
            abortFling()
        }
        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            newText = text?.subSequence(start, start + count).toString()
            completeIndentation(start, count)
            textChangedNewText = text?.subSequence(start, start + count).toString()
            replaceText(textChangeStart, textChangeEnd, textChangedNewText)
            if (!isDoingUndoRedo && textLastChange != null) {
                if (count < UndoStack.MAX_SIZE) {
                    textLastChange?.newText = text?.subSequence(start, start + count).toString()
                    if (start == textLastChange?.start &&
                        (textLastChange?.oldText?.isNotEmpty()!! || textLastChange?.newText?.isNotEmpty()!!) &&
                        !textLastChange?.oldText?.equals(textLastChange?.newText)!!) {
                        undoStack.push(textLastChange!!)
                        redoStack.removeAll()
                    }
                } else {
                    undoStack.removeAll()
                    redoStack.removeAll()
                }
                textLastChange = null
            }
            newText = ""
            if(configuration.codeCompletion) {
                onPopupChangePosition()
            }
        }
        override fun afterTextChanged(s: Editable?) {
            clearSearchSpans()
            clearSyntaxSpans()
            syntaxHighlight()
        }
    }

    private val lines = LinesCollection()
    private val editableFactory = Editable.Factory.getInstance()

    private val selectedLinePaint = Paint()
    private val gutterPaint = Paint()
    private val gutterDividerPaint = Paint()
    private val gutterCurrentLineNumberPaint = Paint()
    private val gutterTextPaint = Paint()

    private val numbersSpan = StyleSpan(color = theme.numbersColor)
    private val symbolsSpan = StyleSpan(color = theme.symbolsColor)
    private val bracketsSpan = StyleSpan(color = theme.bracketsColor)
    private val keywordsSpan = StyleSpan(color = theme.keywordsColor)
    private val methodsSpan = StyleSpan(color = theme.methodsColor)
    private val stringsSpan = StyleSpan(color = theme.stringsColor)
    private val commentsSpan = StyleSpan(color = theme.commentsColor, italic = true)

    private val tabString = "    " // 4 spaces
    private val bracketTypes = charArrayOf('{', '[', '(', '}', ']', ')')

    private var openBracketSpan = BackgroundColorSpan(theme.bracketsBgColor)
    private var closedBracketSpan = BackgroundColorSpan(theme.bracketsBgColor)

    private var isDoingUndoRedo = false
    private var isAutoIndenting = false
    private var isFindSpansVisible = false

    private var scrollListeners = arrayOf<OnScrollChangedListener>()
    private var maximumVelocity = 0f

    private var zoomPinch = false
    private var zoomFactor = 1f

    private var gutterWidth = 0
    private var gutterDigitCount = 0
    private var gutterMargin = 4.toPx() // 4 dp to pixels

    private var textLastChange: UndoStack.TextChange? = null
    private var textChangeStart = 0
    private var textChangeEnd = 0
    private var textChangedNewText = ""

    private var newText = ""

    private var topDirtyLine = 0
    private var bottomDirtyLine = 0

    private var facadeText = editableFactory.newEditable("")

    private var velocityTracker: VelocityTracker? = null

    // region INIT

    init {
        //language = JavaScriptLanguage()
        completion = ModPECompletion()
        completionAdapter.dataSet = completion.getAll().toMutableList()
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

        if(configuration.codeCompletion) {
            setAdapter(completionAdapter)
            setTokenizer(SymbolsTokenizer())
        } else {
            setTokenizer(null)
        }
        if(configuration.pinchZoom) {
            setOnTouchListener { _, event ->
                pinchZoom(event)
            }
        } else {
            setOnTouchListener { _, event ->
                onTouchEvent(event)
            }
        }
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

            gutterDividerPaint.color = theme.gutterDividerColor
            gutterDividerPaint.isAntiAlias = false
            gutterDividerPaint.isDither = false
            gutterDividerPaint.style = Paint.Style.STROKE
            gutterDividerPaint.strokeWidth = 2.6f

            gutterCurrentLineNumberPaint.color = theme.gutterCurrentLineNumberColor
            gutterCurrentLineNumberPaint.isAntiAlias = true
            gutterCurrentLineNumberPaint.isDither = false
            gutterCurrentLineNumberPaint.textAlign = Paint.Align.RIGHT

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

            completionAdapter.color = theme.filterableColor

            openBracketSpan = BackgroundColorSpan(theme.bracketsBgColor)
            closedBracketSpan = BackgroundColorSpan(theme.bracketsBgColor)
        }
    }

    // endregion INIT

    // region CORE

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
            gutterCurrentLineNumberPaint.typeface = typeface
            gutterTextPaint.typeface = typeface
        }
    }

    override fun onDraw(canvas: Canvas) {
        if(layout != null) {
            val currentLineStart = lines.getLineForIndex(selectionStart)
            var top: Int
            if (configuration.highlightCurrentLine) {
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
            updateGutter()
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
                        (number + 1).toString(),
                        textRight.toFloat(),
                        (layout.getLineBaseline(i) + paddingTop).toFloat(),
                        if(number == currentLineStart) {
                            gutterCurrentLineNumberPaint
                        } else {
                            gutterTextPaint
                        }
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
                gutterDividerPaint
            )
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (selStart == selEnd) {
            checkMatchingBracket(selStart)
        }
        //invalidate()
    }

    fun getFacadeText(): String {
        return facadeText.toString()
    }

    fun setFacadeText(newText: String) {
        newText.removeSuffix("\n")

        disableUndoRedo()

        setText(newText)
        undoStack.clear()
        redoStack.clear()
        facadeText.clear()
        replaceText(0, facadeText.length, newText)
        lines.clear()
        var line = 0
        var lineStart = 0
        newText.lines().forEach {
            lines.add(line, lineStart)
            lineStart += it.length + 1
            line++
        }
        lines.add(line, lineStart) //because the last \n was removed

        enableUndoRedo()

        syntaxHighlight()
    }

    fun clearText() {
        setFacadeText("")
    }

    private fun enableUndoRedo() {
        addTextChangedListener(textWatcher)
    }

    private fun disableUndoRedo() {
        removeTextChangedListener(textWatcher)
    }

    // endregion CORE

    // region METHODS

    fun hasPrimaryClip(): Boolean = clipboardManager.hasPrimaryClip()

    fun insert(delta: CharSequence) {
        var selectionStart = 0.coerceAtLeast(selectionStart)
        var selectionEnd = 0.coerceAtLeast(selectionEnd)

        selectionStart = selectionStart.coerceAtMost(selectionEnd)
        selectionEnd = selectionStart.coerceAtLeast(selectionEnd)

        editableText.delete(selectionStart, selectionEnd)
        editableText.insert(selectionStart, delta)
        //text.replace(selectionStart, selectionEnd, delta)
    }

    fun cut() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("CUT", selectedText()))
        editableText.replace(selectionStart, selectionEnd, "")
    }

    fun copy() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("COPY", selectedText()))
    }

    fun paste() {
        val clip = clipboardManager.primaryClip?.getItemAt(0)?.coerceToText(context)
        editableText.replace(selectionStart, selectionEnd, clip)
    }

    fun selectLine() {
        var start = selectionStart.coerceAtMost(selectionEnd)
        var end = selectionStart.coerceAtLeast(selectionEnd)
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
        var start = selectionStart.coerceAtMost(selectionEnd)
        var end = selectionStart.coerceAtLeast(selectionEnd)
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
        var start = selectionStart.coerceAtMost(selectionEnd)
        var end = selectionStart.coerceAtLeast(selectionEnd)
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

    fun canUndo(): Boolean = undoStack.canUndo()
    fun canRedo(): Boolean = redoStack.canUndo()

    fun undo() {
        val textChange = undoStack.pop() as UndoStack.TextChange
        when {
            textChange.start >= 0 -> {
                isDoingUndoRedo = true
                if (textChange.start > text.length) {
                    textChange.start = text.length
                }
                var end = textChange.start + textChange.newText.length
                if (end < 0) {
                    end = 0
                }
                if (end > text.length) {
                    end = text.length
                }
                text.replace(textChange.start, end, textChange.oldText)
                Selection.setSelection(text, textChange.start + textChange.oldText.length)
                redoStack.push(textChange)
                isDoingUndoRedo = false
            }
            else -> undoStack.clear()
        }
    }

    fun redo() {
        val textChange = redoStack.pop() as UndoStack.TextChange
        when {
            textChange.start >= 0 -> {
                isDoingUndoRedo = true
                text.replace(
                    textChange.start,
                    textChange.start + textChange.oldText.length, textChange.newText
                )
                Selection.setSelection(text, textChange.start + textChange.newText.length)
                undoStack.push(textChange)
                isDoingUndoRedo = false
            }
            else -> undoStack.clear()
        }
    }

    fun find(what: String, matchCase: Boolean, regExp: Boolean, wordsOnly: Boolean) {
        val pattern: Pattern = if (regExp) {
            if (matchCase) {
                Pattern.compile(what)
            } else {
                Pattern.compile(what, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
            }
        } else {
            if (wordsOnly) {
                if (matchCase) {
                    Pattern.compile("\\s$what\\s")
                } else {
                    Pattern.compile("\\s" + Pattern.quote(what) + "\\s",
                        Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
                }
            } else {
                if (matchCase) {
                    Pattern.compile(Pattern.quote(what))
                } else {
                    Pattern.compile(Pattern.quote(what), Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
                }
            }
        }
        clearSearchSpans()
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            text.setSpan(
                BackgroundColorSpan(theme.searchBgColor),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        isFindSpansVisible = true
    }

    fun replaceAll(replaceWhat: String, replaceWith: String) {
        setText(text.toString().replace(replaceWhat, replaceWith))
    }

    fun gotoLine(lineNumber: Int) {
        setSelection(lines.getIndexForLine(lineNumber))
    }

    private fun selectedText(): Editable {
        return text.subSequence(selectionStart, selectionEnd) as Editable
    }

    // endregion METHODS

    // region LINE_NUMBERS

    fun getArrayLineCount(): Int {
        return lines.lineCount - 1
    }

    private fun updateGutter() {
        var max = 3
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
        if (gutterDigitCount >= max) {
            max = gutterDigitCount
        }
        val builder = StringBuilder()
        for (i in 0 until max) {
            builder.append(widestNumber.toString())
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
        val line = abs((scrollY + height) / lineHeight) + 1
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

    // region INDENTATION

    private fun completeIndentation(start: Int, count: Int) {
        if(!isDoingUndoRedo && !isAutoIndenting) {
            val result = executeIndentation(start)
            val replacementValue = if (result[0] != null || result[1] != null) {
                val preText = result[0] ?: ""
                val postText = result[1] ?: ""
                if (preText != "" || postText != "") {
                    preText + newText + postText
                } else {
                    return
                }
            } else if (result[2] != null) {
                result[2]!!
            } else {
                return
            }
            val newCursorPosition: Int
            newCursorPosition = if (result[3] != null) {
                Integer.parseInt(result[3]!!)
            } else {
                start + replacementValue.length
            }
            post {
                isAutoIndenting = true
                text.replace(start, start + count, replacementValue)
                undoStack.pop()
                val change = undoStack.pop()
                if (replacementValue != "") {
                    if(change != null) {
                        change.newText = replacementValue
                        undoStack.push(change)
                    }
                }
                Selection.setSelection(text, newCursorPosition)
                isAutoIndenting = false
            }
        }
    }

    private fun executeIndentation(start: Int): Array<String?> {
        val strArr: Array<String?>
        if (newText == "\n" && configuration.autoIndentation) {
            val prevLineIndentation = getIndentationForOffset(start)
            val indentation = StringBuilder(prevLineIndentation)
            var newCursorPosition = indentation.length + start + 1
            if (start > 0 && text[start - 1] == '{') {
                indentation.append(tabString)
                newCursorPosition = indentation.length + start + 1
            }
            if (start + 1 < text.length && text[start + 1] == '}') {
                indentation.append("\n").append(prevLineIndentation)
            }
            strArr = arrayOfNulls(4)
            strArr[1] = indentation.toString()
            strArr[3] = newCursorPosition.toString()
            return strArr
        } else if (newText == "\"" && configuration.autoCloseQuotes) {
            if (start + 1 >= text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "\""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (text[start + 1] == '\"' && text[start - 1] != '\\') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (!(text[start + 1] == '\"' && text[start - 1] == '\\')) {
                strArr = arrayOfNulls(4)
                strArr[1] = "\""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "'" && configuration.autoCloseQuotes) {
            if (start + 1 >= text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (start + 1 >= text.length) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (text[start + 1] == '\'' && start > 0 && text[start - 1] != '\\') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            } else if (!(text[start + 1] == '\'' && start > 0 && text[start - 1] == '\\')) {
                strArr = arrayOfNulls(4)
                strArr[1] = "'"
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "{" && configuration.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "}"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "}" && configuration.autoCloseBrackets) {
            if (start + 1 < text.length && text[start + 1] == '}') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "(" && configuration.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = ")"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == ")" && configuration.autoCloseBrackets) {
            if (start + 1 < text.length && text[start + 1] == ')') {
                strArr = arrayOfNulls(4)
                strArr[2] = ""
                strArr[3] = (start + 1).toString()
                return strArr
            }
        } else if (newText == "[" && configuration.autoCloseBrackets) {
            strArr = arrayOfNulls(4)
            strArr[1] = "]"
            strArr[3] = (start + 1).toString()
            return strArr
        } else if (newText == "]" && configuration.autoCloseBrackets && start + 1 < text.length && text[start + 1] == ']') {
            strArr = arrayOfNulls(4)
            strArr[2] = ""
            strArr[3] = (start + 1).toString()
            return strArr
        }
        return arrayOfNulls(4)
    }

    private fun getIndentationForOffset(offset: Int): String {
        return getIndentationForLine(lines.getLineForIndex(offset))
    }

    private fun getIndentationForLine(line: Int): String {
        val realLine = lines.getLine(line) ?: return ""
        val start = realLine.start
        var i = start
        while (i < text.length) {
            val char = text[i]
            if (!Character.isWhitespace(char) || char == '\n') {
                break
            }
            i++
        }
        return text.subSequence(start, i).toString()
    }

    // endregion INDENTATION

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

                //post(::invalidateVisibleArea)
            }
        }
    }

    private fun clearSearchSpans() {
        if(isFindSpansVisible) {
            val spans = text.getSpans<BackgroundColorSpan>(0, text.length)
            for (span in spans) {
                text.removeSpan(span)
            }
            isFindSpansVisible = false
        }
    }

    private fun clearSyntaxSpans() {
        val spans = text.getSpans<SyntaxHighlightSpan>(0, text.length)
        for (span in spans) {
            text.removeSpan(span)
        }
    }

    /*private fun invalidateVisibleArea() {
        invalidate(
            paddingLeft,
            scrollY + paddingTop,
            width,
            scrollY + paddingTop + height
        )
    }*/

    private fun checkMatchingBracket(pos: Int) {
        if(layout != null) {
            text.removeSpan(openBracketSpan)
            text.removeSpan(closedBracketSpan)
            if (configuration.highlightDelimiters) {
                if (pos > 0 && pos <= text.length) {
                    val c1 = text[pos - 1]
                    for (i in bracketTypes.indices) {
                        if (bracketTypes[i] == c1) {
                            val open = i <= 2
                            val c2 = bracketTypes[(i + 3) % 6]
                            var k = pos
                            if (open) {
                                var nob = 1
                                while (k < text.length) {
                                    if (text[k] == c2) {
                                        nob--
                                    }
                                    if (text[k] == c1) {
                                        nob++
                                    }
                                    if (nob == 0) {
                                        showBracket(pos - 1, k)
                                        break
                                    }
                                    k++
                                }
                            } else {
                                var ncb = 1
                                k -= 2
                                while (k >= 0) {
                                    if (text[k] == c2) {
                                        ncb--
                                    }
                                    if (text[k] == c1) {
                                        ncb++
                                    }
                                    if (ncb == 0) {
                                        showBracket(k, pos - 1)
                                        break
                                    }
                                    k--
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showBracket(i: Int, j: Int) {
        text.setSpan(openBracketSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(closedBracketSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // endregion SYNTAX_HIGHLIGHT

    // region SCROLLER

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        syntaxHighlight()
        for (listener in scrollListeners) {
            listener.onScrollChanged(scrollX, scrollY, scrollX, scrollY)
        }
        if(configuration.codeCompletion) {
            onDropDownSizeChange(w, h)
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
                    if (abs(velocityY) >= 0 || abs(velocityX) >= 0) {
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
            clearSyntaxSpans()
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

    // region PINCH_ZOOM

    private fun pinchZoom(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP ->
                zoomPinch = false
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    val distance = getDistanceBetweenTouches(event)
                    if (!zoomPinch) {
                        zoomFactor = textSize / scaledDensity / distance
                        zoomPinch = true
                    }
                    validateTextSize(zoomFactor * distance)
                }
            }
        }
        return zoomPinch
    }

    private fun getDistanceBetweenTouches(event: MotionEvent): Float {
        val xx = event.getX(1) - event.getX(0)
        val yy = event.getY(1) - event.getY(0)
        return sqrt((xx * xx + yy * yy).toDouble()).toFloat()
    }

    private fun validateTextSize(size: Float) {
        textSize = when {
            size < 10 -> //minimum
                10f //minimum
            size > 20 -> //maximum
                20f //maximum
            else ->
                size
        }
    }

    // endregion PINCH_ZOOM

    // region CODE_COMPLETION

    override fun showDropDown() {
        if(!isPopupShowing) {
            if(hasFocus()) {
                super.showDropDown()
            }
        }
    }

    private fun onDropDownSizeChange(width: Int, height: Int) {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)

        dropDownWidth = width * 1/2 // 1/2 width of screen
        dropDownHeight = height * 1/2 // 1/2 height of screen

        onPopupChangePosition() // change position
    }

    private fun onPopupChangePosition() {
        if(layout != null) {
            val charHeight = paint.measureText("M").toInt()
            val line = layout.getLineForOffset(selectionStart)
            val baseline = layout.getLineBaseline(line)
            val ascent = layout.getLineAscent(line)

            val x = layout.getPrimaryHorizontal(selectionStart)
            val y = baseline + ascent

            val offsetHorizontal = x + gutterWidth
            dropDownHorizontalOffset = offsetHorizontal.toInt()

            val offsetVertical = y + charHeight - scrollY

            var tmp = offsetVertical + dropDownHeight + charHeight
            if (tmp < getVisibleHeight()) {
                tmp = offsetVertical + charHeight / 2
                dropDownVerticalOffset = tmp
            } else {
                tmp = offsetVertical - dropDownHeight - charHeight
                dropDownVerticalOffset = tmp
            }
        }
    }

    private fun getVisibleHeight(): Int {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)
        return rect.bottom - rect.top
    }

    // endregion CODE_COMPLETION
}