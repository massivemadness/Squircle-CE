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
import com.blacksquircle.ui.feature.editor.ui.fragment.view.EditorCommand
import com.blacksquircle.ui.feature.editor.ui.fragment.view.EditorState
import com.blacksquircle.ui.feature.editor.ui.fragment.view.TextContent
import com.blacksquircle.ui.feature.editor.ui.fragment.view.createFromRegistry
import com.blacksquircle.ui.feature.editor.ui.fragment.view.deleteLine
import com.blacksquircle.ui.feature.editor.ui.fragment.view.selectLine
import com.blacksquircle.ui.feature.editor.ui.fragment.view.syncScroll
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.widget.subscribeAlways

@Composable
internal fun CodeEditor(
    state: EditorState,
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
            editor.isDisableSoftKbdIfHardKbdAvailable = !settings.softKeyboard
            editor.setShowInvisibleChars(settings.showInvisibleChars)

            val editorLanguage = editor.createFromRegistry(
                language = language,
                codeCompletion = settings.codeCompletion,
                useTab = !settings.useSpacesInsteadOfTabs,
                tabSize = settings.tabWidth,
            )
            editor.setEditorLanguage(editorLanguage)
            editor.setText(content)
            editor.syncScroll()
        },
        onRelease = CodeEditor::release,
        modifier = modifier,
    )
    LaunchedEffect(Unit) {
        state.commands.collect { command ->
            when (command) {
                is EditorCommand.Cut -> view.cutText()
                is EditorCommand.Copy -> view.copyText()
                is EditorCommand.Paste -> view.pasteText()
                is EditorCommand.SelectAll -> view.selectAll()
                is EditorCommand.SelectLine -> view.selectLine()
                is EditorCommand.DeleteLine -> view.deleteLine()
                is EditorCommand.DuplicateLine -> view.duplicateLine()
                is EditorCommand.IndentOrTab -> view.indentOrCommitTab()
                is EditorCommand.InputText -> view.pasteText(command.text)
                is EditorCommand.MoveSelection -> view.setSelection(command.line, 0)
            }
        }
    }
}