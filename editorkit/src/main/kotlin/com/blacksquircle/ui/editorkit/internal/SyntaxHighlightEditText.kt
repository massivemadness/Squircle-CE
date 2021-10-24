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

package com.blacksquircle.ui.editorkit.internal

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.util.AttributeSet
import androidx.core.text.PrecomputedTextCompat
import androidx.core.text.getSpans
import com.blacksquircle.ui.editorkit.R
import com.blacksquircle.ui.editorkit.model.FindParams
import com.blacksquircle.ui.editorkit.span.ErrorSpan
import com.blacksquircle.ui.editorkit.span.FindResultSpan
import com.blacksquircle.ui.editorkit.span.TabWidthSpan
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.plugin.base.bottomVisibleLine
import com.blacksquircle.ui.plugin.base.topVisibleLine
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

abstract class SyntaxHighlightEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : UndoRedoEditText(context, attrs, defStyleAttr) {

    var language: Language? = null

    private val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
    private val findResultSpans = mutableListOf<FindResultSpan>()

    private var findResultStyleSpan: StyleSpan? = null

    private var addedTextCount = 0
    private var selectedFindResult = 0

    private var isSyntaxHighlighting = false
    private var isErrorSpansVisible = false

    override fun colorize() {
        findResultStyleSpan = StyleSpan(color = colorScheme.findResultBackgroundColor)
        super.colorize()
    }

    override fun setTextContent(textParams: PrecomputedTextCompat) {
        syntaxHighlightSpans.clear()
        findResultSpans.clear()
        super.setTextContent(textParams)
        syntaxHighlight()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateSyntaxHighlighting()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        updateSyntaxHighlighting()
    }

    override fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        addedTextCount -= count
        cancelSyntaxHighlighting()
        if (!isSyntaxHighlighting) {
            super.doBeforeTextChanged(text, start, count, after)
        }
        abortFling()
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        addedTextCount += count
        if (!isSyntaxHighlighting) {
            super.doOnTextChanged(text, start, before, count)
        }
    }

    override fun doAfterTextChanged(text: Editable?) {
        if (!isSyntaxHighlighting) {
            shiftSpans(selectionStart, addedTextCount)
        }
        addedTextCount = 0
        syntaxHighlight()
    }

    fun clearFindResultSpans() {
        selectedFindResult = 0
        findResultSpans.clear()
        val spans = text.getSpans<FindResultSpan>(0, text.length)
        for (span in spans) {
            text.removeSpan(span)
        }
    }

    fun setErrorLine(lineNumber: Int) {
        if (lineNumber > 0) {
            val lineStart = lines.getIndexForStartOfLine(lineNumber - 1)
            val lineEnd = lines.getIndexForEndOfLine(lineNumber - 1)
            if (lineStart < text.length && lineEnd < text.length && lineStart > -1 && lineEnd > -1) {
                isErrorSpansVisible = true
                text.setSpan(ErrorSpan(), lineStart, lineEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    fun find(findText: String, findParams: FindParams) {
        if (findText.isNotEmpty()) {
            try {
                val pattern = if (findParams.regex) {
                    if (findParams.matchCase) {
                        Pattern.compile(findText)
                    } else {
                        Pattern.compile(findText, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
                    }
                } else {
                    if (findParams.wordsOnly) {
                        if (findParams.matchCase) {
                            Pattern.compile("\\s$findText\\s")
                        } else {
                            Pattern.compile(
                                "\\s" + Pattern.quote(findText) + "\\s",
                                Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE
                            )
                        }
                    } else {
                        if (findParams.matchCase) {
                            Pattern.compile(Pattern.quote(findText))
                        } else {
                            Pattern.compile(
                                Pattern.quote(findText),
                                Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE
                            )
                        }
                    }
                }
                val matcher = pattern.matcher(text)
                while (matcher.find()) {
                    findResultStyleSpan?.let {
                        val findResultSpan = FindResultSpan(it, matcher.start(), matcher.end())
                        findResultSpans.add(findResultSpan)

                        text.setSpan(
                            findResultSpan,
                            findResultSpan.start,
                            findResultSpan.end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                if (findResultSpans.isNotEmpty()) {
                    selectResult()
                }
            } catch (e: PatternSyntaxException) {
                // nothing
            }
        }
    }

    fun findNext() {
        if (selectedFindResult < findResultSpans.size - 1) {
            selectedFindResult += 1
            selectResult()
        }
    }

    fun findPrevious() {
        if (selectedFindResult > 0 && selectedFindResult < findResultSpans.size) {
            selectedFindResult -= 1
            selectResult()
        }
    }

    fun replaceFindResult(replaceText: String) {
        if (findResultSpans.isNotEmpty()) {
            val findResult = findResultSpans[selectedFindResult]
            text.replace(findResult.start, findResult.end, replaceText)
            findResultSpans.remove(findResult)
            if (selectedFindResult >= findResultSpans.size) {
                selectedFindResult--
            }
        }
    }

    fun replaceAllFindResults(replaceText: String) {
        if (findResultSpans.isNotEmpty()) {
            val stringBuilder = StringBuilder(text)
            for (index in findResultSpans.size - 1 downTo 0) {
                val findResultSpan = findResultSpans[index]
                stringBuilder.replace(findResultSpan.start, findResultSpan.end, replaceText)
                findResultSpans.removeAt(index)
            }
            setText(stringBuilder.toString())
        }
    }

    private fun selectResult() {
        val findResult = findResultSpans[selectedFindResult]
        setSelection(findResult.start, findResult.end)
        scrollToFindResult()
    }

    private fun scrollToFindResult() {
        if (selectedFindResult < findResultSpans.size) {
            val findResult = findResultSpans[selectedFindResult]
            if (findResult.start >= layout.getLineStart(topVisibleLine) &&
                findResult.end <= layout.getLineEnd(bottomVisibleLine)) {
                return
            }
            val height = layout.height - height + paddingBottom + paddingTop
            var lineTop = layout.getLineTop(layout.getLineForOffset(findResult.start))
            if (lineTop > height) {
                lineTop = height
            }
            val scrollX = if (!editorConfig.wordWrap) {
                layout.getPrimaryHorizontal(findResult.start).toInt()
            } else scrollX

            scrollTo(scrollX, lineTop)
        }
    }

    private fun shiftSpans(from: Int, byHowMuch: Int) {
        for (span in syntaxHighlightSpans) {
            if (span.start >= from) {
                span.start += byHowMuch
            }
            if (span.end >= from) {
                span.end += byHowMuch
            }
            /*if (span.start > span.end) {
                syntaxHighlightSpans.remove(span) // FIXME may cause ConcurrentModificationException
            }*/
        }
        for (findResult in findResultSpans) {
            /*if (from > findResult.start && from <= findResult.end) {
                findResultSpans.remove(findResult) // FIXME may cause IndexOutOfBoundsException
            }*/
            if (findResult.start > from) {
                findResult.start += byHowMuch
            }
            if (findResult.end >= from) {
                findResult.end += byHowMuch
            }
        }
        if (isErrorSpansVisible) {
            val spans = text.getSpans<ErrorSpan>(0, text.length)
            for (span in spans) {
                text.removeSpan(span)
            }
            isErrorSpansVisible = false
        }
    }

    private fun updateSyntaxHighlighting() {
        if (layout != null) {
            val lineStart = layout.getLineStart(topVisibleLine)
            val lineEnd = layout.getLineEnd(bottomVisibleLine)

            isSyntaxHighlighting = true
            val textSyntaxSpans = text.getSpans<SyntaxHighlightSpan>(0, text.length)
            for (span in textSyntaxSpans) {
                text.removeSpan(span)
            }
            for (span in syntaxHighlightSpans) {
                val isInText = span.start >= 0 && span.end <= text.length
                val isValid = span.start <= span.end
                val isVisible = span.start in lineStart..lineEnd ||
                        span.start <= lineEnd && span.end >= lineStart
                if (isInText && isValid && isVisible) {
                    text.setSpan(
                        span,
                        if (span.start < lineStart) lineStart else span.start,
                        if (span.end > lineEnd) lineEnd else span.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            isSyntaxHighlighting = false

            val textFindSpans = text.getSpans<FindResultSpan>(0, text.length)
            for (span in textFindSpans) {
                text.removeSpan(span)
            }
            for (span in findResultSpans) {
                val isInText = span.start >= 0 && span.end <= text.length
                val isValid = span.start <= span.end
                val isVisible = span.start in lineStart..lineEnd ||
                        span.start <= lineEnd && span.end >= lineStart
                if (isInText && isValid && isVisible) {
                    text.setSpan(
                        span,
                        if (span.start < lineStart) lineStart else span.start,
                        if (span.end > lineEnd) lineEnd else span.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            if (!editorConfig.useSpacesInsteadOfTabs) {
                // FIXME works pretty bad with word wrap
                val textTabSpans = text.getSpans<TabWidthSpan>(0, text.length)
                for (span in textTabSpans) {
                    text.removeSpan(span)
                }

                val tabPattern = Pattern.compile("\t")
                val matcher = tabPattern.matcher(text.subSequence(lineStart, lineEnd))
                while (matcher.find()) {
                    val start = matcher.start() + lineStart
                    val end = matcher.end() + lineStart
                    if (start >= 0 && end <= text.length) {
                        text.setSpan(
                            TabWidthSpan(editorConfig.tabWidth),
                            start,
                            end,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                }
            }
        }
    }

    private fun syntaxHighlight() {
        cancelSyntaxHighlighting()
        language?.getStyler()?.enqueue(text.toString(), colorScheme.syntaxScheme) { spans ->
            syntaxHighlightSpans.clear()
            syntaxHighlightSpans.addAll(spans)
            updateSyntaxHighlighting()
        }
    }

    private fun cancelSyntaxHighlighting() {
        language?.getStyler()?.cancel()
    }
}