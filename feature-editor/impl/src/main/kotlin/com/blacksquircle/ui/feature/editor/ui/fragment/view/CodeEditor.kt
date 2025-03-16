/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.fragment.view

import android.content.Context
import android.util.AttributeSet
import io.github.rosemoe.sora.R
import io.github.rosemoe.sora.widget.CodeEditor as SoraEditor

/**
 * Changes:
 * - Changed divider and linenumber margins
 * - Added support for minimum gutter width
 */
internal class CodeEditor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.codeEditorStyle,
) : SoraEditor(context, attrs, defStyleAttr) {

    override fun initialize(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        super.initialize(attrs, defStyleAttr, defStyleRes)
        lineNumberMarginLeft = DIVIDER_MARGIN_LEFT * dpUnit
        setDividerMargin(
            DIVIDER_MARGIN_LEFT * dpUnit,
            DIVIDER_MARGIN_RIGHT * dpUnit,
        )
    }

    override fun measureLineNumber(): Float {
        val minGutterWidth = dpUnit * MIN_GUTTER_WIDTH
        val gutterWidth = super.measureLineNumber()
        return if (gutterWidth < minGutterWidth) {
            minGutterWidth
        } else {
            gutterWidth
        }
    }

    companion object {
        private const val MIN_GUTTER_WIDTH = 28
        private const val DIVIDER_MARGIN_LEFT = 2
        private const val DIVIDER_MARGIN_RIGHT = 4
    }
}