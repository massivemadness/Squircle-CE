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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import io.github.rosemoe.sora.text.Content

@Immutable
internal data class DocumentState(
    val document: DocumentModel,
    val content: Content? = null,
    val errorState: ErrorState? = null,
    val searchState: SearchState? = null,
) {
    private val _canUndo = mutableStateOf(content?.canUndo() ?: false)
    val canUndo: State<Boolean> = _canUndo
    private val _canRedo = mutableStateOf(content?.canRedo() ?: false)
    val canRedo: State<Boolean> = _canRedo

    fun syncState() {
        _canUndo.value = content?.canUndo() ?: false
        _canRedo.value = content?.canRedo() ?: false
    }
}