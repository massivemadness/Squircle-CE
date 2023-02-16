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

package com.blacksquircle.ui.editorkit.plugin.autocomplete

import android.graphics.Rect
import android.util.Log
import com.blacksquircle.ui.editorkit.plugin.base.EditorPlugin
import com.blacksquircle.ui.editorkit.widget.TextProcessor
import com.blacksquircle.ui.language.base.Language

class AutoCompletePlugin : EditorPlugin(PLUGIN_ID) {

    var suggestionAdapter: SuggestionAdapter? = null
        set(value) {
            field = value
            updateAdapter()
        }

    override fun onAttached(editText: TextProcessor) {
        super.onAttached(editText)
        editText.setTokenizer(SymbolsTokenizer())
        editText.setAdapter(suggestionAdapter)
        Log.d(PLUGIN_ID, "AutoComplete plugin loaded successfully!")
    }

    override fun onDetached(editText: TextProcessor) {
        editText.setTokenizer(null)
        editText.setAdapter(null)
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

    override fun setTextContent(text: CharSequence) {
        super.setTextContent(text)
        language?.getProvider()?.clearLines()
        language?.getProvider()?.processAllLines(structure)
    }

    override fun processLine(lineNumber: Int, lineStart: Int, lineEnd: Int) {
        super.processLine(lineNumber, lineStart, lineEnd)
        language?.getProvider()?.processLine(
            lineNumber = lineNumber,
            text = editText.text.subSequence(lineStart, lineEnd),
        )
    }

    override fun removeLine(lineNumber: Int) {
        super.removeLine(lineNumber)
        language?.getProvider()?.deleteLine(lineNumber)
    }

    override fun onLanguageChanged(language: Language?) {
        super.onLanguageChanged(language)
        language?.getProvider()?.let { provider ->
            suggestionAdapter?.setSuggestionProvider(provider)
        }
    }

    override fun showDropDown() {
        if (!editText.isPopupShowing) {
            if (editText.hasFocus()) {
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
                editText.setAdapter(adapter)
            }
        }
    }

    private fun onDropDownSizeChange(width: Int, height: Int) {
        editText.dropDownWidth = width * 1 / 2
        editText.dropDownHeight = height * 1 / 2
        onPopupChangePosition()
    }

    private fun onPopupChangePosition() {
        val layout = editText.layout ?: return
        val line = layout.getLineForOffset(editText.selectionStart)
        val x = layout.getPrimaryHorizontal(editText.selectionStart)
        val y = layout.getLineBaseline(line)

        val offsetHorizontal = x + editText.paddingStart
        editText.dropDownHorizontalOffset = offsetHorizontal.toInt()

        val offsetVertical = y - editText.scrollY
        val temp = offsetVertical + editText.dropDownHeight
        editText.dropDownVerticalOffset = if (temp > getVisibleHeight()) {
            offsetVertical - editText.dropDownHeight
        } else {
            offsetVertical
        }
    }

    private fun getVisibleHeight(): Int {
        val rect = Rect()
        editText.getWindowVisibleDisplayFrame(rect)
        return rect.bottom - rect.top
    }

    companion object {
        const val PLUGIN_ID = "autocomplete-6743"
    }
}