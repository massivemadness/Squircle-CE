/*
 * Copyright Squircle CE contributors.
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
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.redux.mapper.ViewState

@Immutable
internal data class ExplorerViewState(
    val workspaces: List<WorkspaceModel> = emptyList(),
    val selectedWorkspace: WorkspaceModel? = null,
    val fileNodes: List<FileNode> = emptyList(),
    val selection: List<FileNode> = emptyList(),
    val searchQuery: String = "",
    val showHiddenFiles: Boolean = true,
    val compactPackages: Boolean = true,
    val sortMode: SortMode = SortMode.SORT_BY_NAME,
    val taskType: TaskType = TaskType.CREATE,
    val errorState: ErrorState? = null,
    val showFiles: Boolean = false,
    val showActionBar: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
) : ViewState