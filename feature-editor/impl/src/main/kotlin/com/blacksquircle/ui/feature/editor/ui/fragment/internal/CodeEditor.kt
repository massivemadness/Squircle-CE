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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.blacksquircle.ui.feature.editor.data.model.EditorSettings
import com.blacksquircle.ui.feature.editor.ui.fragment.view.CodeEditor
import com.blacksquircle.ui.feature.editor.ui.fragment.view.CodeEditorEvent
import com.blacksquircle.ui.feature.editor.ui.fragment.view.CodeEditorState
import com.blacksquircle.ui.feature.editor.ui.fragment.view.TextContent
import com.blacksquircle.ui.feature.editor.ui.fragment.view.createFromRegistry
import com.blacksquircle.ui.feature.editor.ui.fragment.view.syncScroll
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.widget.subscribeAlways

@Composable
internal fun CodeEditor(
    state: CodeEditorState,
    content: TextContent,
    language: String,
    settings: EditorSettings,
    modifier: Modifier = Modifier,
    onContentChanged: () -> Unit = {},
) {
    val context = LocalContext.current
    val view = remember {
        CodeEditor(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            subscribeAlways<ContentChangeEvent> { event ->
                if (event.action != ContentChangeEvent.ACTION_SET_NEW_TEXT) {
                    onContentChanged()
                }
            }
        }
    }
    AndroidView(
        factory = { view },
        update = { editor ->
            editor.createSubEventManager()
            editor.setTextSize(settings.fontSize)
            editor.isWordwrap = settings.wordWrap
            editor.isScalable = settings.pinchZoom
            editor.isLineNumberEnabled = settings.lineNumbers
            editor.isHighlightCurrentLine = settings.highlightCurrentLine
            editor.isHighlightBracketPair = settings.highlightMatchingDelimiters
            editor.isBlockLineEnabled = settings.highlightCodeBlocks
            editor.isEditable = !settings.readOnly
            editor.tabWidth = settings.tabWidth
            editor.typefaceText = settings.fontType
            editor.typefaceLineNumber = settings.fontType
            editor.colorScheme = editor.createFromRegistry()
            editor.setShowInvisibleChars(settings.showInvisibleChars)
            editor.setEditorLanguage(editor.createFromRegistry(language, settings.codeCompletion))
            editor.setText(content)
            editor.syncScroll()
        },
        onRelease = CodeEditor::release,
        modifier = modifier,
    )
    LaunchedEffect(Unit) {
        state.eventBus.collect { event ->
            when (event) {
                is CodeEditorEvent.Cut -> view.cutText()
                is CodeEditorEvent.Copy -> view.copyText()
                is CodeEditorEvent.Paste -> view.pasteText()
            }
        }
    }
}