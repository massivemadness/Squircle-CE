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

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorSettings

@Immutable
internal data class EditorViewState(
    val documents: List<DocumentState> = emptyList(),
    val selectedDocument: Int = -1,
    val settings: EditorSettings = EditorSettings(),
    val showExtraKeys: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isLoading: Boolean = true,
) : ViewState {

    val currentDocument: DocumentState?
        get() = documents.getOrNull(selectedDocument)

    val isEmpty: Boolean
        get() = documents.isEmpty()

    val isError: Boolean
        get() = currentDocument?.errorState != null

    val showExtendedKeyboard: Boolean
        get() = settings.extendedKeyboard &&
            settings.keyboardPreset.isNotEmpty() &&
            !settings.readOnly &&
            !isError &&
            !isLoading &&
            !isEmpty
}