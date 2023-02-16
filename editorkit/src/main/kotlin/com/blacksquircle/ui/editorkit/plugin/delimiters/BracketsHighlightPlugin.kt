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

package com.blacksquircle.ui.editorkit.plugin.delimiters

import android.graphics.Color
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.Log
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.editorkit.plugin.base.EditorPlugin
import com.blacksquircle.ui.editorkit.widget.TextProcessor

class BracketsHighlightPlugin : EditorPlugin(PLUGIN_ID) {

    private val delimiters = charArrayOf('{', '[', '(', '<', '}', ']', ')', '>')

    private var openDelimiterSpan = BackgroundColorSpan(Color.GRAY)
    private var closedDelimiterSpan = BackgroundColorSpan(Color.GRAY)

    override fun onAttached(editText: TextProcessor) {
        super.onAttached(editText)
        Log.d(PLUGIN_ID, "BracketsHighlight plugin loaded successfully!")
    }

    override fun onColorSchemeChanged(colorScheme: ColorScheme) {
        super.onColorSchemeChanged(colorScheme)
        openDelimiterSpan = BackgroundColorSpan(colorScheme.delimiterBackgroundColor)
        closedDelimiterSpan = BackgroundColorSpan(colorScheme.delimiterBackgroundColor)
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart == selEnd) {
            checkMatchingBracket(selStart)
        }
    }

    private fun checkMatchingBracket(pos: Int) {
        if (editText.layout == null) return
        editText.text.removeSpan(openDelimiterSpan)
        editText.text.removeSpan(closedDelimiterSpan)
        if (pos > 0 && pos <= editText.text.length) {
            val c1 = editText.text[pos - 1]
            for (i in delimiters.indices) {
                if (delimiters[i] == c1) {
                    val half = delimiters.size / 2
                    val open = i <= half - 1
                    val c2 = delimiters[(i + half) % delimiters.size]
                    var k = pos
                    if (open) {
                        var nob = 1
                        while (k < editText.text.length) {
                            if (editText.text[k] == c2) {
                                nob--
                            }
                            if (editText.text[k] == c1) {
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
                            if (editText.text[k] == c2) {
                                ncb--
                            }
                            if (editText.text[k] == c1) {
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
        editText.text.setSpan(openDelimiterSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        editText.text.setSpan(closedDelimiterSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    companion object {
        const val PLUGIN_ID = "brackets-highlight-1180"
    }
}