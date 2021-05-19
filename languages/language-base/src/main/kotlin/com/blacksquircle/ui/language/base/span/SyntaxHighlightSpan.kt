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

package com.blacksquircle.ui.language.base.span

import android.text.TextPaint
import android.text.style.CharacterStyle

data class SyntaxHighlightSpan(
    private val span: StyleSpan,
    var start: Int,
    var end: Int
) : CharacterStyle(), Comparable<SyntaxHighlightSpan> {

    override fun updateDrawState(textPaint: TextPaint?) {
        textPaint?.color = span.color
        textPaint?.isFakeBoldText = span.bold
        textPaint?.isUnderlineText = span.underline
        if (span.italic) {
            textPaint?.textSkewX = -0.1f
        }
        if (span.strikethrough) {
            textPaint?.flags = TextPaint.STRIKE_THRU_TEXT_FLAG
        }
    }

    override fun compareTo(other: SyntaxHighlightSpan): Int {
        return start - other.start
    }
}