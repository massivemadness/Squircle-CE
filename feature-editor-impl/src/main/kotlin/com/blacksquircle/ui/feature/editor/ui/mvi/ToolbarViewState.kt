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

import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.editorkit.model.FindParams
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.ui.manager.ToolbarManager

sealed class ToolbarViewState : ViewState() {

    data class ActionBar(
        val documents: List<DocumentModel> = emptyList(),
        val position: Int = -1,
        val mode: ToolbarManager.Mode = ToolbarManager.Mode.DEFAULT,
        val findParams: FindParams = FindParams()
    ) : ToolbarViewState()
}