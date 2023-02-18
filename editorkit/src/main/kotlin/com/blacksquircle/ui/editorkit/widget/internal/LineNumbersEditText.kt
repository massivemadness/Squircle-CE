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

package com.blacksquircle.ui.editorkit.widget.internal

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.blacksquircle.ui.language.base.model.TextStructure

abstract class LineNumbersEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.autoCompleteTextViewStyle,
) : ScrollableEditText(context, attrs, defStyleAttr) {

    var softKeyboard: Boolean = false
        set(value) {
            field = value
            imeOptions = if (value) {
                EditorInfo.IME_ACTION_UNSPECIFIED
            } else {
                EditorInfo.IME_FLAG_NO_EXTRACT_UI
            }
        }

    val structure = TextStructure(SpannableStringBuilder())

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
    private var textChangedNewText: CharSequence = ""

    init {
        gravity = Gravity.START or Gravity.TOP
        inputType = InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_MULTI_LINE or
            InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    open fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        textChangeStart = start
        textChangeEnd = start + count
    }

    open fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        textChangedNewText = text?.subSequence(start, start + count) ?: ""
        replaceText(textChangeStart, textChangeEnd, textChangedNewText)
        val startLine = structure.getLineForIndex(textChangeStart)
        val endLine = structure.getLineForIndex(textChangeStart + textChangedNewText.length)
        for (currentLine in startLine..endLine) {
            val lineStart = structure.getIndexForStartOfLine(currentLine)
            val lineEnd = structure.getIndexForEndOfLine(currentLine)
            if (lineStart <= lineEnd) {
                processLine(currentLine, lineStart, lineEnd)
            }
        }
    }

    open fun doAfterTextChanged(text: Editable?) = Unit

    open fun setTextContent(textParams: PrecomputedTextCompat) {
        removeTextChangedListener(textWatcher)
        setText(textParams)
        replaceText(0, structure.text.length, textParams)
        addTextChangedListener(textWatcher)
    }

    open fun setTextContent(text: CharSequence) {
        val textParams = TextViewCompat.getTextMetricsParams(this)
        val precomputedText = PrecomputedTextCompat.create(text, textParams)
        setTextContent(precomputedText)
    }

    open fun replaceText(newStart: Int, newEnd: Int, newText: CharSequence) {
        val start = if (newStart < 0) 0 else newStart
        val end = if (newEnd > structure.text.length) structure.text.length else newEnd
        val newCharCount = newText.length - (end - start)
        val startLine = structure.getLineForIndex(start)
        for (i in start until end) {
            if (structure.text[i] == '\n') {
                removeLine(startLine + 1)
            }
        }
        structure.shiftIndexes(structure.getLineForIndex(start) + 1, newCharCount)
        for (i in newText.indices) {
            if (newText[i] == '\n') {
                addLine(structure.getLineForIndex(start + i) + 1, start + i + 1)
            }
        }
        structure.text.replace(start, end, newText)
    }

    open fun processLine(lineNumber: Int, lineStart: Int, lineEnd: Int) = Unit

    open fun addLine(lineNumber: Int, lineStart: Int) {
        structure.add(lineNumber, lineStart)
    }

    open fun removeLine(lineNumber: Int) {
        structure.remove(lineNumber)
    }
}