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

package com.lightteam.modpeide.ui.editor.customview.internal

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import androidx.core.text.getSpans
import com.lightteam.language.language.Language
import com.lightteam.language.parser.span.ErrorSpan
import com.lightteam.language.styler.Styleable
import com.lightteam.language.styler.span.SyntaxHighlightSpan
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.ThemeConverter
import java.util.regex.Pattern

open class SyntaxHighlightEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : UndoRedoEditText(context, attrs, defStyleAttr), Styleable {

    var isSyntaxHighlighting = false
    var isFindSpansVisible = false
    var isErrorSpansVisible = false

    var language: Language? = null

    private var syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()

    private val delimiters = charArrayOf('{', '[', '(', '}', ']', ')')
    private var openDelimiterSpan: BackgroundColorSpan? = null
    private var closedDelimiterSpan: BackgroundColorSpan? = null

    private var topDirtyLine = 0
    private var bottomDirtyLine = 0

    override fun colorize() {
        super.colorize()
        theme?.let {
            openDelimiterSpan = BackgroundColorSpan(it.colorScheme.delimiterBackgroundColor)
            closedDelimiterSpan = BackgroundColorSpan(it.colorScheme.delimiterBackgroundColor)
        }
    }

    override fun processText(newText: String) {
        super.processText(newText)
        syntaxHighlight()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (selStart == selEnd) {
            checkMatchingBracket(selStart)
        }
        // invalidate()
    }

    override fun setSelection(start: Int, stop: Int) {
        if (start <= text.length && stop <= text.length) {
            super.setSelection(start, stop)
        }
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
        cancelSyntaxHighlighting()
        if (!isSyntaxHighlighting) {
            super.doBeforeTextChanged(text, start, count, after)
        }
        abortFling()
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        if (!isSyntaxHighlighting) {
            super.doOnTextChanged(text, start, before, count)
        }
    }

    override fun doAfterTextChanged(text: Editable?) {
        super.doAfterTextChanged(text)
        /*if (!isSyntaxHighlighting) {
            shiftSpans(selectionStart, addedTextCount)
        }*/
        clearSpans()
        syntaxHighlight()
    }

    override fun setSpans(spans: List<SyntaxHighlightSpan>) {
        syntaxHighlightSpans = spans as MutableList<SyntaxHighlightSpan>
        updateSyntaxHighlighting()
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

    private fun updateSyntaxHighlighting() {
        if (layout != null) {
            val textSyntaxSpans = text.getSpans<SyntaxHighlightSpan>(0, text.length)
            for (span in textSyntaxSpans) {
                text.removeSpan(span)
            }

            var topLine = scrollY / lineHeight - 10
            if (topLine >= lineCount) {
                topLine = lineCount - 1
            } else if (topLine < 0) {
                topLine = 0
            }

            var bottomLine = (scrollY + height) / lineHeight + 10
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

    private fun syntaxHighlight() {
        cancelSyntaxHighlighting()
        theme?.let {
            language?.runStyler(
                this,
                getProcessedText(),
                ThemeConverter.toSyntaxScheme(it)
            )
        }
    }

    private fun cancelSyntaxHighlighting() {
        language?.cancelStyler()
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
        // clearSpans()
    }*/

    private fun checkMatchingBracket(pos: Int) {
        if (layout != null) {
            if (openDelimiterSpan != null && closedDelimiterSpan != null) {
                text.removeSpan(openDelimiterSpan)
                text.removeSpan(closedDelimiterSpan)
            }
            if (config.highlightDelimiters) {
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
        if (openDelimiterSpan != null && closedDelimiterSpan != null) {
            text.setSpan(openDelimiterSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(closedDelimiterSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
