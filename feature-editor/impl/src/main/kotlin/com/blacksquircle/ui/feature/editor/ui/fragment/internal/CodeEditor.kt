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

package com.blacksquircle.ui.feature.editor.ui.fragment.internal

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.blacksquircle.ui.feature.editor.data.model.EditorSettings
import com.blacksquircle.ui.feature.editor.ui.fragment.view.CodeEditor
import com.blacksquircle.ui.feature.editor.ui.fragment.view.TextContent
import com.blacksquircle.ui.feature.editor.ui.fragment.view.createFromRegistry
import com.blacksquircle.ui.feature.editor.ui.fragment.view.syncScroll

@Composable
internal fun CodeEditor(
    content: TextContent,
    language: String,
    settings: EditorSettings,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            CodeEditor(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = { editor ->
            editor.setTextSize(settings.fontSize)
            editor.isWordwrap = settings.wordWrap
            editor.isScalable = settings.pinchZoom
            editor.isLineNumberEnabled = settings.lineNumbers
            editor.isHighlightCurrentLine = settings.highlightCurrentLine
            editor.isHighlightCurrentBlock = true // TODO new setting
            editor.isHighlightBracketPair = settings.highlightMatchingDelimiters
            editor.isEditable = !settings.readOnly
            editor.tabWidth = settings.tabWidth
            editor.typefaceText = settings.fontType
            editor.typefaceLineNumber = settings.fontType
            editor.colorScheme = editor.createFromRegistry()
            editor.setEditorLanguage(editor.createFromRegistry(language, settings.codeCompletion))
            editor.setText(content)
            editor.syncScroll()
        },
        onRelease = CodeEditor::release,
        modifier = modifier,
    )
}