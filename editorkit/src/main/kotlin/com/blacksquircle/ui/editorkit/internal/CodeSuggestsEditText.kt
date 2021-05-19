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
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.text.PrecomputedTextCompat
import com.blacksquircle.ui.editorkit.R
import com.blacksquircle.ui.editorkit.adapter.SuggestionAdapter
import com.blacksquircle.ui.editorkit.utils.SymbolsTokenizer

abstract class CodeSuggestsEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : AutoIndentEditText(context, attrs, defStyleAttr) {

    var suggestionAdapter: SuggestionAdapter? = null

    override fun showDropDown() {
        if (!isPopupShowing) {
            if (hasFocus()) {
                super.showDropDown()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (editorConfig.codeCompletion) {
            onDropDownSizeChange(w, h)
        }
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.doOnTextChanged(text, start, before, count)
        if (editorConfig.codeCompletion) {
            onPopupChangePosition()
        }
    }

    override fun configure() {
        super.configure()
        if (editorConfig.codeCompletion) {
            setAdapter(suggestionAdapter)
            setTokenizer(SymbolsTokenizer())
        } else {
            setTokenizer(null)
        }
    }

    override fun colorize() {
        suggestionAdapter?.colorScheme = colorScheme
        super.colorize()
    }

    override fun setTextContent(textParams: PrecomputedTextCompat) {
        language?.getProvider()?.clearLines()
        super.setTextContent(textParams)
        language?.let {
            suggestionAdapter?.setSuggestionProvider(it.getProvider())
        }
    }

    override fun addLine(lineNumber: Int, lineStart: Int, lineLength: Int) {
        super.addLine(lineNumber, lineStart, lineLength)
        language?.getProvider()?.processLine(
            lineNumber = lineNumber,
            text = text.substring(lineStart, lineStart + lineLength)
        )
    }

    override fun removeLine(lineNumber: Int) {
        language?.getProvider()?.deleteLine(lines.getIndexForLine(lineNumber))
        super.removeLine(lineNumber)
    }

    override fun replaceText(newStart: Int, newEnd: Int, newText: CharSequence) {
        super.replaceText(newStart, newEnd, newText)
        val startLine = lines.getLineForIndex(newStart)
        val endLine = lines.getLineForIndex(newText.length + newStart)
        for (currentLine in startLine..endLine) {
            val lineStart = getIndexForStartOfLine(currentLine)
            val lineEnd = getIndexForEndOfLine(currentLine)
            if (lineStart <= lineEnd && lineEnd <= text.length) {
                language?.getProvider()?.processLine(
                    lineNumber = currentLine,
                    text = text.substring(lineStart, lineEnd)
                )
            }
        }
    }

    private fun onDropDownSizeChange(width: Int, height: Int) {
        dropDownWidth = width * 1 / 2
        dropDownHeight = height * 1 / 2
        onPopupChangePosition()
    }

    private fun onPopupChangePosition() {
        if (layout != null) {
            val line = layout.getLineForOffset(selectionStart)
            val x = layout.getPrimaryHorizontal(selectionStart)
            val y = layout.getLineBaseline(line)

            val offsetHorizontal = x + paddingStart
            dropDownHorizontalOffset = offsetHorizontal.toInt()

            val offsetVertical = y - scrollY
            val temp = offsetVertical + dropDownHeight
            dropDownVerticalOffset = if (temp < getVisibleHeight()) {
                offsetVertical
            } else {
                offsetVertical - dropDownHeight
            }
        }
    }

    private fun getVisibleHeight(): Int {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)
        return rect.bottom - rect.top
    }
}