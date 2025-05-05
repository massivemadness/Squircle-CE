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

package com.blacksquircle.ui.feature.editor.ui.editor.view

import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.feature.editor.data.model.LanguageScope
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.SelectionMovement

internal val Content.selectionStart: Int
    get() = cursor.left

internal val Content.selectionEnd: Int
    get() = cursor.right

internal fun CodeEditor.selectLine() {
    val line = cursor.rightLine
    val column = text.getColumnCount(line)
    setSelectionRegion(line, 0, line, column)
}

internal fun CodeEditor.deleteLine() {
    val line = cursor.rightLine
    val column = text.getColumnCount(line)
    text.delete(line, 0, line, column)
}

internal fun CodeEditor.toggleCase() {
    if (isTextSelected) {
        val left = cursor.left()
        val right = cursor.right()
        val replace = text.substring(left.index, right.index)
        if (replace.all(Char::isUpperCase)) {
            text.replace(left.index, right.index, replace.lowercase())
        } else {
            text.replace(left.index, right.index, replace.uppercase())
        }
        setSelectionRegion(left.line, left.column, right.line, right.column)
    }
}

internal fun CodeEditor.previousWord() {
    moveOrExtendSelection(SelectionMovement.PREVIOUS_WORD_BOUNDARY, false)
}

internal fun CodeEditor.nextWord() {
    moveOrExtendSelection(SelectionMovement.NEXT_WORD_BOUNDARY, false)
}

internal fun CodeEditor.startOfLine() {
    moveOrExtendSelection(SelectionMovement.LINE_START, false)
}

internal fun CodeEditor.endOfLine() {
    moveOrExtendSelection(SelectionMovement.LINE_END, false)
}

internal fun CodeEditor.createFromRegistry(
    language: String,
    codeCompletion: Boolean,
    autoIndentation: Boolean,
    autoClosePairs: Boolean,
    useTab: Boolean,
    tabSize: Int,
): Language {
    return try {
        if (language == LanguageScope.TEXT) {
            return EmptyLanguage()
        }
        TextMateLanguage.create(language, codeCompletion).apply {
            this.tabSize = tabSize
            newlineHandler.isEnabled = autoIndentation
            symbolPairs.setEnabled(autoClosePairs)
            useTab(useTab)
        }
    } catch (e: Exception) {
        context.showToast(text = "Couldn't load grammar from registry: ${e.message}")
        EmptyLanguage()
    }
}