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

package com.blacksquircle.ui.feature.explorer.ui.explorer.store.reducer

import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.redux.reducer.Reducer
import javax.inject.Inject

internal class WorkspaceReducer @Inject constructor() : Reducer<ExplorerState, ExplorerAction, ExplorerEvent>() {

    override fun reduce(action: ExplorerAction) {
        when (action) {
            is ExplorerAction.CommandAction.WorkspacesLoaded -> {
                state {
                    copy(
                        workspaces = action.workspaces,
                        selectedWorkspace = action.selectedWorkspace,
                        showHiddenFiles = action.showHiddenFiles,
                        compactPackages = action.compactPackages,
                        foldersOnTop = action.foldersOnTop,
                        sortMode = action.sortMode,
                    )
                }

                // Load files automatically
                action(ExplorerAction.CommandAction.LoadFiles(action.fileNode))
            }
            else -> Unit
        }
    }
}