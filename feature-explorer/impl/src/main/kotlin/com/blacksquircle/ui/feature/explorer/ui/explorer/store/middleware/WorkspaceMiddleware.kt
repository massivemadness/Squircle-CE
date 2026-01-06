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

package com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware

import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.explorer.data.node.FileNodeCache
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.redux.middleware.Middleware
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class WorkspaceMiddleware @Inject constructor(
    private val explorerRepository: ExplorerRepository,
    private val settingsManager: SettingsManager,
    private val fileNodeCache: FileNodeCache,
) : Middleware<ExplorerState, ExplorerAction> {

    override fun bind(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return merge(
            loadWorkspaces(state, actions)
        )
    }

    private fun loadWorkspaces(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.Init>()
            .flatMapLatest { explorerRepository.loadWorkspaces() }
            .map { workspaces ->
                val currentState = state.first()
                val currentWorkspace = explorerRepository.currentWorkspace
                if (currentWorkspace.uuid != currentState.selectedWorkspace?.uuid) {
                    explorerRepository.selectWorkspace(currentWorkspace)
                }

                val fileNode = FileNode(
                    file = currentWorkspace.defaultLocation,
                    isExpanded = true,
                    isLoading = true,
                )
                fileNodeCache.setRoot(fileNode)

                ExplorerAction.CommandAction.WorkspacesLoaded(
                    workspaces = workspaces,
                    selectedWorkspace = currentWorkspace,
                    showHiddenFiles = settingsManager.showHidden,
                    compactPackages = settingsManager.compactPackages,
                    foldersOnTop = settingsManager.foldersOnTop,
                    sortMode = SortMode.of(settingsManager.sortMode),
                    fileNode = fileNode,
                )
            }.catch<ExplorerAction> {
                emit(ExplorerAction.Error(it))
            }
    }
}