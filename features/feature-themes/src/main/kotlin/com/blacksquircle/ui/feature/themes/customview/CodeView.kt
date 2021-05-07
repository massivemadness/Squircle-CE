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

package com.blacksquircle.ui.feature.themes.customview

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.toSpannable
import com.blacksquircle.ui.domain.model.themes.ThemeModel
import com.blacksquircle.ui.language.base.Language

class CodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var language: Language? = null
        set(value) {
            field = value
            syntaxHighlight()
        }

    var themeModel: ThemeModel? = null
        set(value) {
            field = value
            colorize()
        }

    private fun colorize() {
        themeModel?.let {
            setTextColor(it.colorScheme.textColor)
            // setBackgroundColor(it.colorScheme.backgroundColor)
        }
    }

    private fun syntaxHighlight() {
        themeModel?.let {
            language?.getStyler()?.execute(text.toString(), it.colorScheme.syntaxScheme)?.let { spans ->
                if (layout != null) {
                    val currentText = text.toSpannable()
                    for (span in spans) {
                        currentText.setSpan(span, span.start, span.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    text = currentText
                }
            }
        }
    }
}