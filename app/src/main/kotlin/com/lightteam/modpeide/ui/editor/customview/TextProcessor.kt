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

package com.lightteam.modpeide.ui.editor.customview

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.*
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import androidx.core.content.getSystemService
import androidx.core.text.getSpans
import com.lightteam.language.language.Language
import com.lightteam.language.parser.span.ErrorSpan
import com.lightteam.language.styler.Styleable
import com.lightteam.language.styler.span.SyntaxHighlightSpan
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.ThemeConverter
import com.lightteam.modpeide.data.feature.LinesCollection
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.modpeide.data.feature.suggestion.WordsManager
import com.lightteam.modpeide.data.feature.undoredo.UndoStackImpl
import com.lightteam.modpeide.domain.editor.TextChange
import com.lightteam.modpeide.domain.feature.undoredo.UndoStack
import com.lightteam.modpeide.ui.editor.adapters.SuggestionAdapter
import com.lightteam.modpeide.ui.editor.customview.internal.OnScrollChangedListener
import com.lightteam.modpeide.ui.editor.customview.internal.SymbolsTokenizer
import com.lightteam.modpeide.utils.extensions.dpToPx
import com.lightteam.modpeide.utils.extensions.getScaledDensity
import com.lightteam.unknown.language.UnknownLanguage
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.sqrt

class TextProcessor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : AppCompatMultiAutoCompleteTextView(context, attrs, defStyleAttr), Styleable {

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

        //Code Style
        var autoIndentation: Boolean = true,
        var autoCloseBrackets: Boolean = true,
        var autoCloseQuotes: Boolean = false
    )

    var configuration: Configuration = Configuration()
        set(value) {
            field = value
            configure()
        }

    var theme: Theme? = null
        set(value) {
            field = value
            colorize()
        }

    var undoStack: UndoStack = UndoStackImpl()
    var redoStack: UndoStack = UndoStackImpl()

    var language: Language = UnknownLanguage()
    val arrayLineCount: Int
        get() = lines.lineCount - 1

    private val textScroller = Scroller(context)
    private val suggestionAdapter = SuggestionAdapter(context, R.layout.item_suggestion)
    private var syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
    private val wordsManager: WordsManager = WordsManager()

    private val clipboardManager = context.getSystemService<ClipboardManager>()!!
    private val scaledDensity = context.getScaledDensity()
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            cancelSyntaxHighlighting()
            addedTextCount -= count
            if (!isSyntaxHighlighting) {
                textChangeStart = start
                textChangeEnd = start + count
                if (!isDoingUndoRedo) {
                    if (count < UndoStackImpl.MAX_SIZE) {
                        textLastChange = TextChange(
                            newText = "",
                            oldText = s?.subSequence(start, start + count).toString(),
                            start = start
                        )
                        return
                    }
                    undoStack.removeAll()
                    redoStack.removeAll()
                    textLastChange = null
                }
            }
            abortFling()
        }
        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            addedTextCount += count
            newText = text?.subSequence(start, start + count).toString()
            completeIndentation(start, count)
            if (!isSyntaxHighlighting) {
                textChangedNewText = text?.subSequence(start, start + count).toString()
                replaceText(textChangeStart, textChangeEnd, textChangedNewText)
                if (!isDoingUndoRedo && textLastChange != null) {
                    if (count < UndoStackImpl.MAX_SIZE) {
                        textLastChange?.newText = text?.subSequence(start, start + count).toString()
                        if (start == textLastChange?.start
                            && (textLastChange?.oldText?.isNotEmpty()!! || textLastChange?.newText?.isNotEmpty()!!)
                            && textLastChange?.oldText != textLastChange?.newText) {
                            undoStack.push(textLastChange!!)
                            redoStack.removeAll()
                        }
                    } else {
                        undoStack.removeAll()
                        redoStack.removeAll()
                    }
                    textLastChange = null
                }
            }
            newText = ""
            if (configuration.codeCompletion) {
                onPopupChangePosition()
            }
        }
        override fun afterTextChanged(s: Editable?) {
            clearSpans()
            updateGutter()
            /*if (!isSyntaxHighlighting) {
                shiftSpans(selectionStart, addedTextCount)
            }*/
            addedTextCount = 0
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

    private val delimiters = charArrayOf('{', '[', '(', '}', ']', ')')

    private var openBracketSpan: BackgroundColorSpan? = null
    private var closedBracketSpan: BackgroundColorSpan? = null

    private var isDoingUndoRedo = false
    private var isSyntaxHighlighting = false
    private var isAutoIndenting = false
    private var isFindSpansVisible = false
    private var isErrorSpansVisible = false

    private var scrollListeners = arrayOf<OnScrollChangedListener>()
    private var maximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity * 100f

    private var zoomPinch = false
    private var zoomFactor = 1f

    private var gutterWidth = 0
    private var gutterDigitCount = 0
    private var gutterMargin = 4.dpToPx() // 4 dp to pixels

    private var textLastChange: TextChange? = null
    private var textChangeStart = 0
    private var textChangeEnd = 0
    private var textChangedNewText = ""

    private var newText = ""
    private var addedTextCount = 0

    private var topDirtyLine = 0
    private var bottomDirtyLine = 0

    private var facadeText = editableFactory.newEditable("")

    private var velocityTracker: VelocityTracker? = null

    // region INIT

    private fun configure() {
        imeOptions = if (configuration.softKeyboard) {
            EditorInfo.IME_ACTION_UNSPECIFIED
        } else {
            EditorInfo.IME_FLAG_NO_EXTRACT_UI
        }
        inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        textSize = configuration.fontSize
        typeface = configuration.fontType

        setHorizontallyScrolling(!configuration.wordWrap)

        if (configuration.codeCompletion) {
            setAdapter(suggestionAdapter)
            setTokenizer(SymbolsTokenizer())
        } else {
            setTokenizer(null)
        }
        setOnTouchListener { _, event ->
            if (configuration.pinchZoom) {
                pinchZoom(event)
            } else {
                onTouchEvent(event)
            }
        }
    }

    private fun colorize() {
        theme?.let {
            post {
                setTextColor(it.colorScheme.textColor)
                setBackgroundColor(it.colorScheme.backgroundColor)
                highlightColor = it.colorScheme.selectionColor

                selectedLinePaint.color = it.colorScheme.selectedLineColor
                selectedLinePaint.isAntiAlias = false
                selectedLinePaint.isDither = false

                gutterPaint.color = it.colorScheme.gutterColor
                gutterPaint.isAntiAlias = false
                gutterPaint.isDither = false

                gutterDividerPaint.color = it.colorScheme.gutterDividerColor
                gutterDividerPaint.isAntiAlias = false
                gutterDividerPaint.isDither = false
                gutterDividerPaint.style = Paint.Style.STROKE
                gutterDividerPaint.strokeWidth = 2.6f

                gutterCurrentLineNumberPaint.color = it.colorScheme.gutterCurrentLineNumberColor
                gutterCurrentLineNumberPaint.isAntiAlias = true
                gutterCurrentLineNumberPaint.isDither = false
                gutterCurrentLineNumberPaint.textAlign = Paint.Align.RIGHT

                gutterTextPaint.color = it.colorScheme.gutterTextColor
                gutterTextPaint.isAntiAlias = true
                gutterTextPaint.isDither = false
                gutterTextPaint.textAlign = Paint.Align.RIGHT

                suggestionAdapter.setColorScheme(it.colorScheme)

                openBracketSpan = BackgroundColorSpan(it.colorScheme.delimiterBackgroundColor)
                closedBracketSpan = BackgroundColorSpan(it.colorScheme.delimiterBackgroundColor)
            }
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
        if (layout != null) {
            val currentLineStart = lines.getLineForIndex(selectionStart)
            if (configuration.highlightCurrentLine) {
                if (currentLineStart == lines.getLineForIndex(selectionEnd)) {
                    val selectedLineStartIndex = getIndexForStartOfLine(currentLineStart)
                    val selectedLineEndIndex = getIndexForEndOfLine(currentLineStart)
                    val topVisualLine = layout.getLineForOffset(selectedLineStartIndex)
                    val bottomVisualLine = layout.getLineForOffset(selectedLineEndIndex)

                    val lineTop = layout.getLineTop(topVisualLine) + paddingTop
                    val width = layout.width + paddingLeft + paddingRight
                    val lineBottom = layout.getLineBottom(bottomVisualLine) + paddingTop
                    canvas.drawRect(
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
            canvas.drawRect(
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
                    canvas.drawText(
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
            canvas.drawLine(
                (gutterWidth + scrollX).toFloat(),
                scrollY.toFloat(),
                (gutterWidth + scrollX).toFloat(),
                (scrollY + height).toFloat(),
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

    override fun setSelection(start: Int, stop: Int) {
        if (start <= text.length && stop <= text.length) {
            super.setSelection(start, stop)
        }
    }

    fun getProcessedText(): String {
        return facadeText.toString()
    }

    fun processText(newText: String) {
        abortFling()
        removeTextChangedListener(textWatcher)
        setText(newText)

        wordsManager.clear()
        // undoStack.clear()
        // redoStack.clear()
        facadeText.clear()
        replaceText(0, facadeText.length, newText)
        lines.clear()

        var lineNumber = 0
        var lineStart = 0
        newText.lines().forEach {
            lines.add(lineNumber, lineStart)
            wordsManager.processLine(
                facadeText, lines.getLine(lineNumber),
                lineStart, lineStart + it.length
            )
            lineStart += it.length + 1
            lineNumber++
        }
        lines.add(lineNumber, lineStart)
        fillWithPredefinedSuggestions()
        addTextChangedListener(textWatcher)
        syntaxHighlight()
    }

    fun clearText() {
        undoStack.clear()
        redoStack.clear()
        processText("")
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
        val textChange = undoStack.pop()
        if (textChange.start >= 0) {
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
            redoStack.push(textChange)
            text.replace(textChange.start, end, textChange.oldText)
            setSelection(textChange.start + textChange.oldText.length)
            isDoingUndoRedo = false
        } else {
            undoStack.clear()
        }
    }

    fun redo() {
        val textChange = redoStack.pop()
        if (textChange.start >= 0) {
            isDoingUndoRedo = true
            undoStack.push(textChange)
            text.replace(
                textChange.start,
                textChange.start + textChange.oldText.length, textChange.newText
            )
            setSelection(textChange.start + textChange.newText.length)
            isDoingUndoRedo = false
        } else {
            undoStack.clear()
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
        clearSpans()
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            theme?.let {
                text.setSpan(
                    BackgroundColorSpan(it.colorScheme.findResultBackgroundColor),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
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
                wordsManager.deleteLine(lines.getLine(startLine + 1))
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
        val endLine = lines.getLineForIndex(newText.length + start)
        for (currentLine in startLine..endLine) {
            wordsManager.processLine(
                facadeText,
                lines.getLine(currentLine),
                getIndexForStartOfLine(currentLine),
                getIndexForEndOfLine(currentLine)
            )
        }
    }

    // endregion LINE_NUMBERS

    // region INDENTATION

    private fun completeIndentation(start: Int, count: Int) {
        if (!isDoingUndoRedo && !isAutoIndenting) {
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
            val newCursorPosition = if (result[3] != null) {
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
                    change.newText = replacementValue
                    undoStack.push(change)
                }
                setSelection(newCursorPosition)
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
                indentation.append("    ") // 4 spaces
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
        val realLine = lines.getLine(line)
        val start = realLine.start
        var i = start
        while (i < text.length) {
            val char = text[i]
            if (!char.isWhitespace() || char == '\n') {
                break
            }
            i++
        }
        return text.subSequence(start, i).toString()
    }

    // endregion INDENTATION

    // region SYNTAX_HIGHLIGHT

    private fun updateSyntaxHighlighting() {
        if (layout != null) {
            val textSyntaxSpans = text.getSpans<SyntaxHighlightSpan>(0, text.length)
            for (span in textSyntaxSpans) {
                text.removeSpan(span)
            }

            var topLine = scrollY / lineHeight - 30
            if (topLine >= lineCount) {
                topLine = lineCount - 1
            } else if (topLine < 0) {
                topLine = 0
            }

            var bottomLine = (scrollY + height) / lineHeight + 30
            if (bottomLine >= lineCount) {
                bottomLine = lineCount - 1
            } else if (bottomLine < 0) {
                bottomLine = 0
            }

            topDirtyLine = topLine
            bottomDirtyLine = bottomLine
            val lineStart = layout.getLineStart(topLine)
            val lineEnd = layout.getLineEnd(bottomLine)

            isSyntaxHighlighting = true
            for (span in syntaxHighlightSpans) {
                if (span.start >= 0 && span.end <= text.length && span.start <= span.end
                    && (span.start in lineStart..lineEnd || span.start <= lineEnd && span.end >= lineStart)) {
                    text.setSpan(
                        span,
                        if (span.start < lineStart) lineStart else span.start,
                        if (span.end > lineEnd) lineEnd else span.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            isSyntaxHighlighting = false
        }
    }

    override fun setSpans(spans: List<SyntaxHighlightSpan>) {
        syntaxHighlightSpans = spans as MutableList<SyntaxHighlightSpan>
        updateSyntaxHighlighting()
    }

    fun setErrorSpan(lineNumber: Int) {
        if (lineNumber == 0) {
            return
        }
        val lineStart = getIndexForStartOfLine(lineNumber - 1)
        val lineEnd = getIndexForEndOfLine(lineNumber - 1)
        if (lineStart < text.length && lineEnd < text.length && lineStart > -1 && lineEnd > -1) {
            isErrorSpansVisible = true
            text.setSpan(ErrorSpan(), lineStart, lineEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun syntaxHighlight() {
        cancelSyntaxHighlighting()
        theme?.let {
            language.runStyler(
                this,
                getProcessedText(),
                ThemeConverter.toSyntaxScheme(it)
            )
        }
    }

    private fun cancelSyntaxHighlighting() {
        language.cancelStyler()
    }

    private fun clearSpans() {
        if (isFindSpansVisible) {
            val spans = text.getSpans<BackgroundColorSpan>(0, text.length)
            for (span in spans) {
                text.removeSpan(span)
            }
            isFindSpansVisible = false
        }
        if (isErrorSpansVisible) {
            val spans = text.getSpans<ErrorSpan>(0, text.length)
            for (span in spans) {
                text.removeSpan(span)
            }
            isErrorSpansVisible = false
        }
    }

    /*private fun shiftSpans(from: Int, byHowMuch: Int) {
        for (span in syntaxHighlightSpans) {
            if (span.start >= from) {
                span.start += byHowMuch
            }
            if (span.end >= from) {
                span.end += byHowMuch
            }
            if (span.start > span.end) {
                syntaxHighlightSpans.remove(span)
            }
        }
        //clearSpans()
    }*/

    private fun checkMatchingBracket(pos: Int) {
        if (layout != null) {
            if (openBracketSpan != null && closedBracketSpan != null) {
                text.removeSpan(openBracketSpan)
                text.removeSpan(closedBracketSpan)
            }
            if (configuration.highlightDelimiters) {
                if (pos > 0 && pos <= text.length) {
                    val c1 = text[pos - 1]
                    for (i in delimiters.indices) {
                        if (delimiters[i] == c1) {
                            val open = i <= 2
                            val c2 = delimiters[(i + 3) % 6]
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
        if (openBracketSpan != null && closedBracketSpan != null) {
            text.setSpan(openBracketSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(closedBracketSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    // endregion SYNTAX_HIGHLIGHT

    // region SCROLLER

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateSyntaxHighlighting()
        for (listener in scrollListeners) {
            listener.onScrollChanged(scrollX, scrollY, scrollX, scrollY)
        }
        if (configuration.codeCompletion) {
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
                val velocityX = if (configuration.wordWrap) {
                    0
                } else {
                    velocityTracker?.xVelocity?.toInt() ?: 0
                }
                val velocityY = velocityTracker?.yVelocity?.toInt() ?: 0
                if (abs(velocityY) < 0 || abs(velocityX) < 0) {
                    if (velocityTracker != null) {
                        velocityTracker?.recycle()
                        velocityTracker = null
                    }
                    super.onTouchEvent(event)
                }
                if (layout == null) {
                    return super.onTouchEvent(event)
                }
                textScroller.fling(
                    scrollX, scrollY,
                    -velocityX, -velocityY,
                    0, layout.width - width + paddingLeft + paddingRight,
                    0, layout.height - height + paddingTop + paddingBottom
                )
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
        updateSyntaxHighlighting()
    }

    override fun computeScroll() {
        if (!isInEditMode) {
            if (textScroller.computeScrollOffset()) {
                scrollTo(textScroller.currX, textScroller.currY)
            }
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
            MotionEvent.ACTION_UP -> zoomPinch = false
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

    // endregion PINCH_ZOOM

    // region SUGGESTIONS

    override fun showDropDown() {
        if (!isPopupShowing) {
            if (hasFocus()) {
                super.showDropDown()
            }
        }
    }

    private fun fillWithPredefinedSuggestions() {
        suggestionAdapter.setWordsManager(wordsManager)
        wordsManager.setSuggestions(language.getSuggestions())
        wordsManager.processSuggestions()
    }

    private fun onDropDownSizeChange(width: Int, height: Int) {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)

        dropDownWidth = width * 1/2 // 1/2 width of screen
        dropDownHeight = height * 1/2 // 1/2 height of screen

        onPopupChangePosition() // change position
    }

    private fun onPopupChangePosition() {
        if (layout != null) {
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

    // endregion SUGGESTIONS
}