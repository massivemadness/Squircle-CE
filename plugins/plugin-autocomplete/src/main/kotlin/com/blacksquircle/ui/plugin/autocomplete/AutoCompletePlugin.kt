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

package com.blacksquircle.ui.plugin.autocomplete

import android.graphics.Rect
import android.util.Log
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import com.blacksquircle.ui.language.base.provider.SuggestionProvider
import com.blacksquircle.ui.plugin.base.EditorPlugin

class AutoCompletePlugin : EditorPlugin(PLUGIN_ID) {

    var suggestionProvider: SuggestionProvider? = null
        set(value) = updateProvider(value)
    var suggestionAdapter: SuggestionAdapter? = null
        set(value) = updateAdapter(value)

    private val codeEditor: MultiAutoCompleteTextView
        get() = editText as MultiAutoCompleteTextView // it's always safe

    override fun onAttached(editText: EditText) {
        super.onAttached(editText)

        codeEditor.setTokenizer(SymbolsTokenizer())
        updateAdapter(suggestionAdapter)

        Log.d(PLUGIN_ID, "AutoComplete plugin loaded successfully!")
    }

    override fun onDetached(editText: EditText) {
        super.onDetached(editText)
        codeEditor.setTokenizer(null)
    }

    override fun showDropDown() {
        if (!codeEditor.isPopupShowing) {
            if (codeEditor.hasFocus()) {
                super.showDropDown()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onDropDownSizeChange(w, h)
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.doOnTextChanged(text, start, before, count)
        onPopupChangePosition()
    }

    override fun addLine(lineNumber: Int, lineStart: Int, lineLength: Int) {
        super.addLine(lineNumber, lineStart, lineLength)
        suggestionProvider?.processLine(
            lineNumber = lineNumber,
            text = codeEditor.text.substring(lineStart, lineStart + lineLength)
        )
    }

    override fun removeLine(lineNumber: Int) {
        super.removeLine(lineNumber)
        suggestionProvider?.deleteLine(
            lineNumber = lines.getIndexForLine(lineNumber)
        )
    }

    override fun setTextContent(text: CharSequence) {
        super.setTextContent(text)
        suggestionProvider?.clearLines()
    }

    override fun doOnTextReplaced(newStart: Int, newEnd: Int, newText: CharSequence) {
        super.doOnTextReplaced(newStart, newEnd, newText)
        val startLine = lines.getLineForIndex(newStart)
        val endLine = lines.getLineForIndex(newText.length + newStart)
        for (currentLine in startLine..endLine) {
            val lineStart = getIndexForStartOfLine(currentLine)
            val lineEnd = getIndexForEndOfLine(currentLine)
            if (lineStart <= lineEnd && lineEnd <= codeEditor.text.length) {
                suggestionProvider?.processLine(
                    lineNumber = currentLine,
                    text = codeEditor.text.substring(lineStart, lineEnd)
                )
            }
        }
    }

    fun updateAdapter(suggestionAdapter: SuggestionAdapter?) {
        this.suggestionAdapter = suggestionAdapter?.also { adapter ->
            if (editText != null) {
                codeEditor.setAdapter(adapter)
                suggestionProvider?.let { provider ->
                    adapter.setSuggestionProvider(provider)
                }
            }
        }
    }

    fun updateProvider(suggestionProvider: SuggestionProvider?) {
        this.suggestionProvider = suggestionProvider?.also { provider ->
            suggestionAdapter?.setSuggestionProvider(provider)
        }
    }

    private fun onDropDownSizeChange(width: Int, height: Int) {
        codeEditor.dropDownWidth = width * 1 / 2
        codeEditor.dropDownHeight = height * 1 / 2
        onPopupChangePosition()
    }

    private fun onPopupChangePosition() {
        codeEditor.layout?.let { layout ->
            val line = layout.getLineForOffset(codeEditor.selectionStart)
            val x = layout.getPrimaryHorizontal(codeEditor.selectionStart)
            val y = layout.getLineBaseline(line)

            val offsetHorizontal = x + codeEditor.paddingStart
            codeEditor.dropDownHorizontalOffset = offsetHorizontal.toInt()

            val offsetVertical = y - codeEditor.scrollY
            val temp = offsetVertical + codeEditor.dropDownHeight
            codeEditor.dropDownVerticalOffset = if (temp > getVisibleHeight()) {
                offsetVertical - codeEditor.dropDownHeight
            } else {
                offsetVertical
            }
        }
    }

    private fun getVisibleHeight(): Int {
        val rect = Rect()
        codeEditor.getWindowVisibleDisplayFrame(rect)
        return rect.bottom - rect.top
    }

    companion object {
        const val PLUGIN_ID = "autocomplete-6743"
    }
}