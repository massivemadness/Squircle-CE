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

package com.blacksquircle.ui.feature.editor.ui.editor

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorCommand

internal sealed class EditorViewEvent : ViewEvent {

    data object CreateFileContract : EditorViewEvent()
    data object OpenFileContract : EditorViewEvent()
    data class SaveAsFileContract(
        val fileName: String
    ) : EditorViewEvent()

    data class Command(
        val command: EditorCommand
    ) : EditorViewEvent()
}