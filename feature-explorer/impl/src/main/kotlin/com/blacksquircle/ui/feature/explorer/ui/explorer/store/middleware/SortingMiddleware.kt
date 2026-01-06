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
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderOptions
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.redux.middleware.Middleware
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class SortingMiddleware @Inject constructor(
    private val settingsManager: SettingsManager,
    private val asyncNodeBuilder: AsyncNodeBuilder,
    private val fileNodeCache: FileNodeCache,
) : Middleware<ExplorerState, ExplorerAction> {

    override fun bind(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return merge(
            onQueryChanged(state, actions),
            onShowHiddenFilesChanged(actions),
            showHiddenFilesUpdated(state, actions),
            onCompactPackagesChanged(actions),
            compactPackagesUpdated(state, actions),
            onSortModeChanged(actions),
            sortModeUpdated(state, actions),
            foldersOnTopUpdated(state, actions),
        )
    }

    private fun onQueryChanged(
        state: Flow<ExplorerState>,
        actions: Flow<ExplorerAction>
    ): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.QueryAction>()
            .flatMapLatest { action ->
                when (action) {
                    is ExplorerAction.UiAction.OnQueryChanged -> {
                        val currentState = state.first()
                        val fileNodes = asyncNodeBuilder.buildNodeList(
                            nodes = fileNodeCache.getAll(),
                            options = NodeBuilderOptions(
                                searchQuery = action.query,
                                showHidden = currentState.showHiddenFiles,
                                sortMode = currentState.sortMode,
                                foldersOnTop = currentState.foldersOnTop,
                                compactPackages = currentState.compactPackages,
                            )
                        )
                        flowOf(ExplorerAction.CommandAction.UpdateFiles(fileNodes))
                    }

                    is ExplorerAction.UiAction.OnClearQueryClicked -> {
                        val currentState = state.first()
                        val fileNodes = asyncNodeBuilder.buildNodeList(
                            nodes = fileNodeCache.getAll(),
                            options = NodeBuilderOptions(
                                searchQuery = "",
                                showHidden = currentState.showHiddenFiles,
                                sortMode = currentState.sortMode,
                                foldersOnTop = currentState.foldersOnTop,
                                compactPackages = currentState.compactPackages,
                            )
                        )
                        flowOf(ExplorerAction.CommandAction.UpdateFiles(fileNodes))
                    }
                }
            }
    }

    private fun onShowHiddenFilesChanged(actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnShowHiddenFilesChanged>()
            .flatMapLatest { action ->
                settingsManager.showHidden = action.showHiddenFiles
                emptyFlow()
            }
    }

    private fun showHiddenFilesUpdated(
        state: Flow<ExplorerState>,
        actions: Flow<ExplorerAction>
    ): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.Init>()
            .flatMapLatest { settingsManager.collect(SettingsManager.KEY_SHOW_HIDDEN_FILES) }
            .flatMapLatest {
                val currentState = state.first()
                val showHiddenFiles = settingsManager.showHidden
                val fileNodes = asyncNodeBuilder.buildNodeList(
                    nodes = fileNodeCache.getAll(),
                    options = NodeBuilderOptions(
                        searchQuery = currentState.searchQuery,
                        showHidden = showHiddenFiles,
                        sortMode = currentState.sortMode,
                        foldersOnTop = currentState.foldersOnTop,
                        compactPackages = currentState.compactPackages,
                    )
                )
                flowOf(
                    ExplorerAction.CommandAction.UpdateFiles(fileNodes),
                    ExplorerAction.CommandAction.ShowHiddenFilesUpdated(showHiddenFiles)
                )
            }
    }

    private fun onCompactPackagesChanged(actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnCompactPackagesChanged>()
            .flatMapLatest { action ->
                settingsManager.compactPackages = action.compactPackages
                emptyFlow()
            }
    }

    private fun compactPackagesUpdated(
        state: Flow<ExplorerState>,
        actions: Flow<ExplorerAction>,
    ): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.Init>()
            .flatMapLatest { settingsManager.collect(SettingsManager.KEY_COMPACT_PACKAGES) }
            .flatMapLatest {
                val currentState = state.first()
                val compactPackages = settingsManager.compactPackages
                val fileNodes = asyncNodeBuilder.buildNodeList(
                    nodes = fileNodeCache.getAll(),
                    options = NodeBuilderOptions(
                        searchQuery = currentState.searchQuery,
                        showHidden = currentState.showHiddenFiles,
                        sortMode = currentState.sortMode,
                        foldersOnTop = currentState.foldersOnTop,
                        compactPackages = compactPackages,
                    )
                )
                flowOf(
                    ExplorerAction.CommandAction.UpdateFiles(fileNodes),
                    ExplorerAction.CommandAction.CompactPackagesUpdated(compactPackages)
                )
            }
    }

    private fun onSortModeChanged(actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnSortModeChanged>()
            .flatMapLatest { action ->
                settingsManager.sortMode = action.sortMode.value
                emptyFlow()
            }
    }

    private fun sortModeUpdated(
        state: Flow<ExplorerState>,
        actions: Flow<ExplorerAction>,
    ): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.Init>()
            .flatMapLatest { settingsManager.collect(SettingsManager.KEY_SORT_MODE) }
            .flatMapLatest {
                val currentState = state.first()
                val sortMode = SortMode.of(settingsManager.sortMode)
                val fileNodes = asyncNodeBuilder.buildNodeList(
                    nodes = fileNodeCache.getAll(),
                    options = NodeBuilderOptions(
                        searchQuery = currentState.searchQuery,
                        showHidden = currentState.showHiddenFiles,
                        sortMode = sortMode,
                        foldersOnTop = currentState.foldersOnTop,
                        compactPackages = currentState.compactPackages,
                    )
                )
                flowOf(
                    ExplorerAction.CommandAction.UpdateFiles(fileNodes),
                    ExplorerAction.CommandAction.SortModeUpdated(sortMode)
                )
            }
    }

    private fun foldersOnTopUpdated(
        state: Flow<ExplorerState>,
        actions: Flow<ExplorerAction>,
    ): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.Init>()
            .flatMapLatest { settingsManager.collect(SettingsManager.KEY_FOLDERS_ON_TOP) }
            .flatMapLatest {
                val currentState = state.first()
                val foldersOnTop = settingsManager.foldersOnTop
                val fileNodes = asyncNodeBuilder.buildNodeList(
                    nodes = fileNodeCache.getAll(),
                    options = NodeBuilderOptions(
                        searchQuery = currentState.searchQuery,
                        showHidden = currentState.showHiddenFiles,
                        sortMode = currentState.sortMode,
                        foldersOnTop = foldersOnTop,
                        compactPackages = currentState.compactPackages,
                    )
                )
                flowOf(
                    ExplorerAction.CommandAction.UpdateFiles(fileNodes),
                    ExplorerAction.CommandAction.FoldersOnTopUpdated(foldersOnTop)
                )
            }
    }
}