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

package com.blacksquircle.ui.feature.editor.ui.viewmodel

import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.language.base.Language

sealed class EditorIntent {

    object LoadFiles : EditorIntent()
    object LoadSettings : EditorIntent()

    data class OpenFile(val fileModel: FileModel) : EditorIntent()
    data class SelectTab(val position: Int) : EditorIntent()
    data class MoveTab(val from: Int, val to: Int) : EditorIntent()
    data class CloseTab(val position: Int) : EditorIntent()
    data class CloseOthers(val position: Int) : EditorIntent()
    object CloseAll : EditorIntent()

    object SaveAs : EditorIntent()
    data class SaveFileAs(val filePath: String) : EditorIntent()

    object GotoLine : EditorIntent()
    data class GotoLineNumber(val line: String) : EditorIntent()

    object ColorPicker : EditorIntent()
    data class InsertColor(val color: Int) : EditorIntent()

    object ModifyContent : EditorIntent()
    data class SaveFile(
        val local: Boolean,
        val text: String,
        val language: Language?,
        val undoStack: UndoStack,
        val redoStack: UndoStack,
        val scrollX: Int,
        val scrollY: Int,
        val selectionStart: Int,
        val selectionEnd: Int
    ) : EditorIntent()

    object PanelDefault : EditorIntent()
    object PanelFind : EditorIntent()
    object PanelFindReplace : EditorIntent()
}