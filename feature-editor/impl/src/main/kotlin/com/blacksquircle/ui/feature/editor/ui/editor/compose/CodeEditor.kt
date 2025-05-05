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

package com.blacksquircle.ui.feature.editor.ui.editor.compose

import android.view.KeyEvent
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorCommand
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorController
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorSettings
import com.blacksquircle.ui.feature.editor.ui.editor.view.CodeEditor
import com.blacksquircle.ui.feature.editor.ui.editor.view.SquircleScheme
import com.blacksquircle.ui.feature.editor.ui.editor.view.createFromRegistry
import com.blacksquircle.ui.feature.editor.ui.editor.view.deleteLine
import com.blacksquircle.ui.feature.editor.ui.editor.view.endOfLine
import com.blacksquircle.ui.feature.editor.ui.editor.view.nextWord
import com.blacksquircle.ui.feature.editor.ui.editor.view.previousWord
import com.blacksquircle.ui.feature.editor.ui.editor.view.selectLine
import com.blacksquircle.ui.feature.editor.ui.editor.view.startOfLine
import com.blacksquircle.ui.feature.editor.ui.editor.view.toggleCase
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.KeyBindingEvent
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.util.regex.RegexBackrefGrammar
import io.github.rosemoe.sora.widget.EditorSearcher.SearchOptions
import io.github.rosemoe.sora.widget.subscribeAlways

@Composable
internal fun CodeEditor(
    content: Content,
    language: String,
    settings: EditorSettings,
    controller: EditorController,
    modifier: Modifier = Modifier,
    onContentChanged: () -> Unit = {},
    onShortcutPressed: (Boolean, Boolean, Boolean, Int) -> Unit = { _, _, _, _ -> },
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
            subscribeAlways<KeyBindingEvent> { event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    val ctrl = event.isCtrlPressed
                    val shift = (event.metaState and KeyEvent.META_SHIFT_ON) != 0
                    val alt = (event.metaState and KeyEvent.META_ALT_ON) != 0
                    if (ctrl || alt) {
                        event.intercept()
                        onShortcutPressed(ctrl, shift, alt, event.keyCode)
                    }
                }
            }
            colorScheme = SquircleScheme.create()
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
            editor.isDisableSoftKbdIfHardKbdAvailable = !settings.softKeyboard
            editor.setShowInvisibleChars(settings.showInvisibleChars)

            val editorLanguage = editor.createFromRegistry(
                language = language,
                codeCompletion = settings.codeCompletion,
                autoIndentation = settings.autoIndentation,
                autoClosePairs = settings.autoClosePairs,
                useTab = !settings.useSpacesInsteadOfTabs,
                tabSize = settings.tabWidth,
            )
            editor.setEditorLanguage(editorLanguage)
            editor.setText(content)
            editor.scroller.startScroll(0, 0, content.scrollX, content.scrollY, 0)
            editor.scroller.abortAnimation()
        },
        onRelease = CodeEditor::release,
        modifier = modifier,
    )
    LaunchedEffect(Unit) {
        controller.commands.collect { command ->
            when (command) {
                is EditorCommand.Cut -> view.cutText()
                is EditorCommand.Copy -> view.copyText()
                is EditorCommand.Paste -> view.pasteText()

                is EditorCommand.SelectAll -> view.selectAll()
                is EditorCommand.SelectLine -> view.selectLine()
                is EditorCommand.DeleteLine -> view.deleteLine()
                is EditorCommand.DuplicateLine -> view.duplicateLine()
                is EditorCommand.ToggleCase -> view.toggleCase()

                is EditorCommand.PreviousWord -> view.previousWord()
                is EditorCommand.NextWord -> view.nextWord()
                is EditorCommand.StartOfLine -> view.startOfLine()
                is EditorCommand.EndOfLine -> view.endOfLine()

                is EditorCommand.Insert -> {
                    if (view.isFocused) {
                        view.pasteText(command.text)
                    }
                }
                is EditorCommand.IndentOrTab -> {
                    if (view.isFocused) {
                        view.indentOrCommitTab()
                    }
                }

                is EditorCommand.Find -> {
                    try {
                        val type = when {
                            command.searchState.regex -> SearchOptions.TYPE_REGULAR_EXPRESSION
                            command.searchState.wordsOnly -> SearchOptions.TYPE_WHOLE_WORD
                            else -> SearchOptions.TYPE_NORMAL
                        }
                        val searchOptions = SearchOptions(
                            /* type = */ type,
                            /* caseInsensitive = */ !command.searchState.matchCase,
                            /* regexBackrefGrammar = */ RegexBackrefGrammar.DEFAULT,
                        )
                        val findText = command.searchState.findText
                        if (findText.isNotEmpty()) {
                            view.searcher.search(findText, searchOptions)
                        } else {
                            view.searcher.stopSearch()
                        }
                    } catch (e: Exception) {
                        // ignored
                    }
                }
                is EditorCommand.Replace -> {
                    if (view.searcher.hasQuery()) {
                        view.searcher.replaceCurrentMatch(command.replacement)
                    }
                }
                is EditorCommand.ReplaceAll -> {
                    if (view.searcher.hasQuery()) {
                        view.searcher.replaceAll(command.replacement)
                    }
                }
                is EditorCommand.GoToLine -> {
                    view.setSelection(command.line, 0)
                }
                is EditorCommand.PreviousMatch -> {
                    if (view.searcher.hasQuery()) {
                        view.searcher.gotoPrevious()
                    }
                }
                is EditorCommand.NextMatch -> {
                    if (view.searcher.hasQuery()) {
                        view.searcher.gotoNext()
                    }
                }
                is EditorCommand.StopSearch -> {
                    if (view.searcher.hasQuery()) {
                        view.searcher.stopSearch()
                    }
                }
            }
        }
    }
}