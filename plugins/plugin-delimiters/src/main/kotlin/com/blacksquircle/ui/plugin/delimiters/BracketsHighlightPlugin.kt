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

package com.blacksquircle.ui.plugin.delimiters

import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.widget.EditText
import com.blacksquircle.ui.plugin.base.EditorPlugin

class BracketsHighlightPlugin : EditorPlugin(PLUGIN_ID) {

    private val editor: EditText
        get() = editText!!

    private val delimiters = charArrayOf('{', '[', '(', '<', '}', ']', ')', '>')

    private var openDelimiterSpan: BackgroundColorSpan? = null
    private var closedDelimiterSpan: BackgroundColorSpan? = null

    override fun onAttached(editText: EditText) {
        super.onAttached(editText)
        openDelimiterSpan = BackgroundColorSpan(colorScheme.delimiterBackgroundColor)
        closedDelimiterSpan = BackgroundColorSpan(colorScheme.delimiterBackgroundColor)

        Log.d(PLUGIN_ID, "BracketsHighlight plugin loaded successfully!")
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart == selEnd) {
            checkMatchingBracket(selStart)
        }
    }

    private fun checkMatchingBracket(pos: Int) {
        if (editor.layout == null) return
        if (openDelimiterSpan != null && closedDelimiterSpan != null) {
            editor.text.removeSpan(openDelimiterSpan)
            editor.text.removeSpan(closedDelimiterSpan)
        }
        if (pos > 0 && pos <= editor.text.length) {
            val c1 = editor.text[pos - 1]
            for (i in delimiters.indices) {
                if (delimiters[i] == c1) {
                    val half = delimiters.size / 2
                    val open = i <= half - 1
                    val c2 = delimiters[(i + half) % delimiters.size]
                    var k = pos
                    if (open) {
                        var nob = 1
                        while (k < editor.text.length) {
                            if (editor.text[k] == c2) {
                                nob--
                            }
                            if (editor.text[k] == c1) {
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
                            if (editor.text[k] == c2) {
                                ncb--
                            }
                            if (editor.text[k] == c1) {
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

    private fun showBracket(i: Int, j: Int) {
        if (openDelimiterSpan != null && closedDelimiterSpan != null) {
            editor.text.setSpan(openDelimiterSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            editor.text.setSpan(closedDelimiterSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    companion object {
        const val PLUGIN_ID = "brackets-highlight-1180"
    }
}