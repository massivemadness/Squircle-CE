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

package com.blacksquircle.ui.feature.explorer.ui.explorer.store.mapper

import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewState
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.redux.mapper.ViewStateMapper
import javax.inject.Inject

internal class ExplorerViewStateMapper @Inject constructor() : ViewStateMapper<ExplorerState, ExplorerViewState> {

    override fun map(state: ExplorerState): ExplorerViewState {
        return ExplorerViewState(
            workspaces = state.workspaces,
            selectedWorkspace = state.selectedWorkspace,
            fileNodes = state.fileNodes,
            selection = state.selection,
            searchQuery = state.searchQuery,
            showHiddenFiles = state.showHiddenFiles,
            compactPackages = state.compactPackages,
            sortMode = state.sortMode,
            taskType = state.taskType,
        )
    }
}