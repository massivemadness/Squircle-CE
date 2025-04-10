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

package com.blacksquircle.ui.feature.editor.ui.editor.model

internal sealed class EditorCommand {

    data object Cut : EditorCommand()
    data object Copy : EditorCommand()
    data object Paste : EditorCommand()

    data object SelectAll : EditorCommand()
    data object SelectLine : EditorCommand()
    data object DeleteLine : EditorCommand()
    data object DuplicateLine : EditorCommand()
    data object ToggleCase : EditorCommand()

    data object PreviousWord : EditorCommand()
    data object NextWord : EditorCommand()
    data object StartOfLine : EditorCommand()
    data object EndOfLine : EditorCommand()

    data class Insert(val text: String) : EditorCommand()
    data object IndentOrTab : EditorCommand()

    data class Find(val searchState: SearchState) : EditorCommand()
    data class Replace(val replacement: String) : EditorCommand()
    data class ReplaceAll(val replacement: String) : EditorCommand()
    data class GoToLine(val line: Int) : EditorCommand()
    data object PreviousMatch : EditorCommand()
    data object NextMatch : EditorCommand()
    data object StopSearch : EditorCommand()
}