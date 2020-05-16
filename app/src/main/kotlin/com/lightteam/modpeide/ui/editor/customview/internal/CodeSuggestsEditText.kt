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
import android.graphics.Rect
import android.util.AttributeSet
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.feature.suggestion.WordsManager
import com.lightteam.modpeide.ui.editor.adapters.SuggestionAdapter
import com.lightteam.modpeide.ui.editor.customview.utils.SymbolsTokenizer

open class CodeSuggestsEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : AutoIndentEditText(context, attrs, defStyleAttr) {

    private val wordsManager: WordsManager = WordsManager()
    private val suggestionAdapter = SuggestionAdapter(context, R.layout.item_suggestion)

    override fun showDropDown() {
        if (!isPopupShowing) {
            if (hasFocus()) {
                super.showDropDown()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (config.codeCompletion) {
            onDropDownSizeChange(w, h)
        }
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.doOnTextChanged(text, start, before, count)
        if (config.codeCompletion) {
            onPopupChangePosition()
        }
    }

    override fun configure() {
        super.configure()
        if (config.codeCompletion) {
            setAdapter(suggestionAdapter)
            setTokenizer(SymbolsTokenizer())
        } else {
            setTokenizer(null)
        }
    }

    override fun colorize() {
        super.colorize()
        theme?.let {
            suggestionAdapter.setColorScheme(it.colorScheme)
        }
    }

    override fun processText(newText: String) {
        wordsManager.clear()
        super.processText(newText)
        fillWithPredefinedSuggestions()
    }

    override fun addLine(lineNumber: Int, lineStart: Int, lineLength: Int) {
        super.addLine(lineNumber, lineStart, lineLength)
        wordsManager.processLine(
            processedText, lines.getLine(lineNumber),
            lineStart, lineStart + lineLength
        )
    }

    override fun removeLine(line: Int) {
        wordsManager.deleteLine(lines.getLine(line))
        super.removeLine(line)
    }

    override fun replaceText(newStart: Int, newEnd: Int, newText: CharSequence) {
        super.replaceText(newStart, newEnd, newText)
        val start = if (newStart < 0) 0 else newStart
        val startLine = lines.getLineForIndex(start)
        val endLine = lines.getLineForIndex(newText.length + start)
        for (currentLine in startLine..endLine) {
            wordsManager.processLine(
                processedText,
                lines.getLine(currentLine),
                getIndexForStartOfLine(currentLine),
                getIndexForEndOfLine(currentLine)
            )
        }
    }

    private fun fillWithPredefinedSuggestions() {
        suggestionAdapter.setWordsManager(wordsManager)
        language?.let {
            wordsManager.setSuggestions(it.getSuggestions())
            wordsManager.processSuggestions()
        }
    }

    private fun onDropDownSizeChange(width: Int, height: Int) {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)

        dropDownWidth = width * 1/2 // 1/2 width of screen
        dropDownHeight = height * 1/2 // 0.5 height of screen

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
}