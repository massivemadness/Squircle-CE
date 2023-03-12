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

package com.blacksquircle.ui.feature.themes.ui.customview

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.toSpannable
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.editorkit.model.StyleSpan
import com.blacksquircle.ui.editorkit.model.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType

class CodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun syntaxHighlight(
        text: CharSequence,
        language: Language,
        colorScheme: ColorScheme,
    ) {
        if (layout == null) {
            return
        }
        val structure = TextStructure(SpannableStringBuilder(text))
        val results = language.getStyler().execute(structure)
        val currentText = text.toSpannable()
        for (result in results) {
            currentText.setSpan(
                SyntaxHighlightSpan(
                    StyleSpan(
                        color = when (result.tokenType) {
                            TokenType.NUMBER -> colorScheme.numberColor
                            TokenType.OPERATOR -> colorScheme.operatorColor
                            TokenType.KEYWORD -> colorScheme.keywordColor
                            TokenType.TYPE -> colorScheme.typeColor
                            TokenType.LANG_CONST -> colorScheme.langConstColor
                            TokenType.PREPROCESSOR -> colorScheme.preprocessorColor
                            TokenType.VARIABLE -> colorScheme.variableColor
                            TokenType.METHOD -> colorScheme.methodColor
                            TokenType.STRING -> colorScheme.stringColor
                            TokenType.COMMENT -> colorScheme.commentColor
                            TokenType.TAG -> colorScheme.tagColor
                            TokenType.TAG_NAME -> colorScheme.tagNameColor
                            TokenType.ATTR_NAME -> colorScheme.attrNameColor
                            TokenType.ATTR_VALUE -> colorScheme.attrValueColor
                            TokenType.ENTITY_REF -> colorScheme.entityRefColor
                        }
                    )
                ),
                result.start,
                result.end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }
        setText(currentText)
    }
}