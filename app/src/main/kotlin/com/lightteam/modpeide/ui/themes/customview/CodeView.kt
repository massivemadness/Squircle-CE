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

package com.lightteam.modpeide.ui.themes.customview

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.toSpannable
import com.lightteam.language.base.Language
import com.lightteam.modpeide.data.converter.ThemeConverter
import com.lightteam.modpeide.domain.model.theme.ThemeModel

class CodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        val CODE_PREVIEW = """
            function useItem(x, y, z, itemId, blockId, side) {
                if (itemId == 280) { // Any ID
                    Level.explode(x, y, z, 16);
                }
            }
            
            function procCmd(cmd) {
                var command = cmd.split(" ");
                if (command[0] == "kit") {
                    if (command[1] == "start") {
                        // TODO: Implement this method
                    }
                    if (command[1] == "tools") {
                        // TODO: Implement this method
                    }
                }
            }
        """.trimIndent()
    }

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
            val syntaxScheme = ThemeConverter.toSyntaxScheme(it)
            language?.getStyler()?.parse(text.toString(), syntaxScheme)?.let { spans ->
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