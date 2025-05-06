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

package com.blacksquircle.ui.feature.explorer.ui.explorer

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode

@Immutable
internal data class ExplorerViewState(
    val workspaces: List<WorkspaceModel> = emptyList(),
    val selectedWorkspace: WorkspaceModel? = null,
    val fileNodes: List<FileNode> = emptyList(),
    val selectedNodes: List<FileNode> = emptyList(),
    val searchQuery: String = "",
    val showHidden: Boolean = true,
    val compactPackages: Boolean = true,
    val sortMode: SortMode = SortMode.SORT_BY_NAME,
    val taskType: TaskType = TaskType.CREATE,
) : ViewState {

    val showFiles: Boolean
        get() = !isLoading && !isError

    val showActionBar: Boolean
        get() = selectedNodes.size == 1 && selectedNodes[0].isDirectory

    val errorState: ErrorState?
        get() = fileNodes.getOrNull(0)?.errorState

    val isLoading: Boolean
        get() = fileNodes.size == 1 && fileNodes[0].isRoot && fileNodes[0].isLoading

    val isError: Boolean
        get() = fileNodes.size == 1 && fileNodes[0].isRoot && fileNodes[0].isError

    val isEmpty: Boolean
        get() = fileNodes.isEmpty() ||
            (fileNodes.size == 1 && fileNodes[0].isRoot && fileNodes[0].isExpanded)
}