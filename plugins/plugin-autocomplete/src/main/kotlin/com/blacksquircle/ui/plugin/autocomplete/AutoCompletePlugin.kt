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
import com.blacksquircle.ui.plugin.base.EditorPlugin

class AutoCompletePlugin : EditorPlugin(PLUGIN_ID) {

    var suggestionAdapter: SuggestionAdapter? = null
        set(value) {
            field = value
            updateAdapter()
        }

    private val editor: MultiAutoCompleteTextView
        get() = editText as MultiAutoCompleteTextView // it's always safe

    override fun onAttached(editText: EditText) {
        super.onAttached(editText)
        editor.setTokenizer(SymbolsTokenizer())
        editor.setAdapter(suggestionAdapter)
        Log.d(PLUGIN_ID, "AutoComplete plugin loaded successfully!")
    }

    override fun onDetached(editText: EditText) {
        editor.setTokenizer(null)
        super.onDetached(editText)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onDropDownSizeChange(w, h)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(text, start, before, count)
        onPopupChangePosition()
    }

    override fun addLine(lineNumber: Int, lineStart: Int, lineLength: Int) {
        super.addLine(lineNumber, lineStart, lineLength)
        language?.getProvider()?.processLine(
            lineNumber = lineNumber,
            text = editor.text.substring(lineStart, lineStart + lineLength)
        )
    }

    override fun removeLine(lineNumber: Int) {
        super.removeLine(lineNumber)
        language?.getProvider()?.deleteLine(
            lineNumber = lines.getIndexForLine(lineNumber)
        )
    }

    override fun clearLines() {
        super.clearLines()
        language?.getProvider()?.clearLines()
    }

    override fun setTextContent(text: CharSequence) {
        super.setTextContent(text)
        updateAdapter() // probably language has been changed
    }

    override fun onTextReplaced(newStart: Int, newEnd: Int, newText: CharSequence) {
        super.onTextReplaced(newStart, newEnd, newText)
        val startLine = lines.getLineForIndex(newStart)
        val endLine = lines.getLineForIndex(newText.length + newStart)
        for (currentLine in startLine..endLine) {
            val lineStart = lines.getIndexForStartOfLine(currentLine)
            val lineEnd = lines.getIndexForEndOfLine(currentLine)
            if (lineStart <= lineEnd && lineEnd <= editor.text.length) {
                language?.getProvider()?.processLine(
                    lineNumber = currentLine,
                    text = editor.text.substring(lineStart, lineEnd)
                )
            }
        }
    }

    override fun showDropDown() {
        if (!editor.isPopupShowing) {
            if (editor.hasFocus()) {
                super.showDropDown()
            }
        }
    }

    private fun updateAdapter() {
        if (isAttached) {
            suggestionAdapter?.let { adapter ->
                language?.getProvider()?.let { provider ->
                    adapter.setSuggestionProvider(provider)
                }
                editor.setAdapter(adapter)
            }
        }
    }

    private fun onDropDownSizeChange(width: Int, height: Int) {
        editor.dropDownWidth = width * 1 / 2
        editor.dropDownHeight = height * 1 / 2
        onPopupChangePosition()
    }

    private fun onPopupChangePosition() {
        val layout = editor.layout ?: return
        val line = layout.getLineForOffset(editor.selectionStart)
        val x = layout.getPrimaryHorizontal(editor.selectionStart)
        val y = layout.getLineBaseline(line)

        val offsetHorizontal = x + editor.paddingStart
        editor.dropDownHorizontalOffset = offsetHorizontal.toInt()

        val offsetVertical = y - editor.scrollY
        val temp = offsetVertical + editor.dropDownHeight
        editor.dropDownVerticalOffset = if (temp > getVisibleHeight()) {
            offsetVertical - editor.dropDownHeight
        } else {
            offsetVertical
        }
    }

    private fun getVisibleHeight(): Int {
        val rect = Rect()
        editor.getWindowVisibleDisplayFrame(rect)
        return rect.bottom - rect.top
    }

    companion object {
        const val PLUGIN_ID = "autocomplete-6743"
    }
}