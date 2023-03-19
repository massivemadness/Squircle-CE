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

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.editorkit.model.FindResult

sealed class EditorViewEvent : ViewEvent() {

    data class FindResults(val results: List<FindResult>) : EditorViewEvent()
    data class InsertColor(val color: String) : EditorViewEvent()
    data class GotoLine(val line: Int) : EditorViewEvent()
}