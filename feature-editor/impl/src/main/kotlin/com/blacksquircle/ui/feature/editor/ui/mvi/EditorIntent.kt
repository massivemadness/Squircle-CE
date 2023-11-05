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

package com.blacksquircle.ui.feature.editor.ui.mvi

import android.net.Uri
import com.blacksquircle.ui.core.mvi.ViewIntent
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class EditorIntent : ViewIntent() {

    data object LoadFiles : EditorIntent()
    data object LoadSettings : EditorIntent()

    data class NewFile(val fileUri: Uri) : EditorIntent()
    data class OpenFile(val fileModel: FileModel) : EditorIntent()
    data class OpenFileUri(val fileUri: Uri) : EditorIntent()
    data class SelectTab(val position: Int) : EditorIntent()
    data class MoveTab(val from: Int, val to: Int) : EditorIntent()
    data class CloseTab(val position: Int, val allowModified: Boolean) : EditorIntent()
    data class CloseOthers(val position: Int) : EditorIntent()
    data object CloseAll : EditorIntent()

    data object GotoLine : EditorIntent()
    data class GotoLineNumber(val line: String) : EditorIntent()

    data object ColorPicker : EditorIntent()
    data class InsertColor(val color: Int) : EditorIntent()

    data object ForceSyntax : EditorIntent()
    data class ForceSyntaxHighlighting(val languageName: String) : EditorIntent()

    data class SaveFile(
        val local: Boolean,
        val unselected: Boolean,
        val text: CharSequence,
        val undoStack: UndoStack,
        val redoStack: UndoStack,
        val scrollX: Int,
        val scrollY: Int,
        val selectionStart: Int,
        val selectionEnd: Int,
    ) : EditorIntent()
    data class SaveFileAs(val fileUri: Uri) : EditorIntent()

    data object ModifyContent : EditorIntent()
    data object SwapKeyboard : EditorIntent()

    data object PanelDefault : EditorIntent()
    data object PanelFind : EditorIntent()
    data object PanelFindReplace : EditorIntent()

    data class FindQuery(val text: CharSequence, val query: String) : EditorIntent()
    data class FindRegex(val text: CharSequence) : EditorIntent()
    data class FindMatchCase(val text: CharSequence) : EditorIntent()
    data class FindWordsOnly(val text: CharSequence) : EditorIntent()
}