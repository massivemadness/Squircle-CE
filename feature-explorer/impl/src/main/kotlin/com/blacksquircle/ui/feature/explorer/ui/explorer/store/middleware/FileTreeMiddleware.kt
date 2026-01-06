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

import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.editor.api.provider.FileIconProvider
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.node.FileNodeCache
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderOptions
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.filesystem.base.exception.AuthRequiredException
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.redux.middleware.Middleware
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject
import com.blacksquircle.ui.ds.R as UiR

@OptIn(ExperimentalCoroutinesApi::class)
internal class FileTreeMiddleware @Inject constructor(
    private val explorerRepository: ExplorerRepository,
    private val fileIconProvider: FileIconProvider,
    private val stringProvider: StringProvider,
    private val asyncNodeBuilder: AsyncNodeBuilder,
    private val fileNodeCache: FileNodeCache,
) : Middleware<ExplorerState, ExplorerAction> {

    override fun bind(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return merge(
            loadFiles(state, actions),
            onExpandClicked(state, actions),
            onCollapseClicked(state, actions),
        )
    }

    private fun loadFiles(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.CommandAction.LoadFiles>()
            .flatMapMerge { action ->
                val currentState = state.first()

                flow {
                    val fileNode = action.fileNode

                    // Set loading state
                    fileNodeCache.updateNode(fileNode) {
                        it.copy(
                            isLoading = true,
                            errorState = null,
                        )
                    }
                    val fileNodes = buildNodeList(currentState)
                    emit(ExplorerAction.CommandAction.RenderNodeList(fileNodes))

                    // Load files
                    val fileList = explorerRepository.listFiles(action.fileNode.file)
                    val currentNodes = fileNodeCache.get(fileNode.key)
                    val updatedNodes = fileList.map { fileModel ->
                        val currentNode = currentNodes.find { it.file.fileUri == fileModel.fileUri }
                        FileNode(
                            file = fileModel,
                            depth = currentNode?.depth ?: (fileNode.depth + 1),
                            displayName = currentNode?.displayName ?: fileModel.name,
                            displayDepth = currentNode?.displayDepth ?: (fileNode.depth + 1),
                            displayIcon = fileIconProvider.fileIcon(fileModel),
                            isExpanded = currentNode?.isExpanded ?: false,
                            isLoading = currentNode?.isLoading ?: false,
                            errorState = currentNode?.errorState,
                        )
                    }
                    fileNodeCache.put(fileNode.key, updatedNodes)

                    // Update state
                    fileNodeCache.updateNode(fileNode) {
                        it.copy(
                            isLoading = false,
                            errorState = null,
                        )
                    }

                    val autoLoad = currentState.searchQuery.isBlank() &&
                        updatedNodes.size == 1 &&
                        updatedNodes[0].isDirectory &&
                        (currentState.showHiddenFiles || !updatedNodes[0].isHidden) &&
                        currentState.compactPackages

                    if (autoLoad) {
                        val nestedNode = updatedNodes[0].copy(
                            displayDepth = fileNode.depth,
                        )
                        fileNodeCache.updateNode(nestedNode) {
                            it.copy(isExpanded = true)
                        }
                        emit(ExplorerAction.CommandAction.LoadFiles(nestedNode))
                    } else {
                        val fileNodes = buildNodeList(currentState)
                        emit(ExplorerAction.CommandAction.RenderNodeList(fileNodes))
                    }
                }.catch<ExplorerAction> { error ->
                    val fileNode = action.fileNode
                    emit(ExplorerAction.CommandAction.LoadFilesError(fileNode, error))

                    fileNodeCache.updateNode(fileNode) {
                        it.copy(
                            isLoading = false,
                            errorState = errorState(error)
                        )
                    }

                    val fileNodes = buildNodeList(currentState)
                    emit(ExplorerAction.CommandAction.RenderNodeList(fileNodes))
                }
            }
    }

    private fun onExpandClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnExpandClicked>()
            .map { action ->
                fileNodeCache.updateNode(action.fileNode) {
                    it.copy(isExpanded = true)
                }
                if (fileNodeCache.contains(action.fileNode.key)) {
                    val fileNodes = buildNodeList(state.first())
                    ExplorerAction.CommandAction.RenderNodeList(fileNodes)
                } else {
                    ExplorerAction.CommandAction.LoadFiles(action.fileNode)
                }
            }
    }

    private fun onCollapseClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnCollapseClicked>()
            .map { action ->
                fileNodeCache.updateNode(action.fileNode) {
                    it.copy(isExpanded = false)
                }
                if (fileNodeCache.contains(action.fileNode.key)) {
                    val fileNodes = buildNodeList(state.first())
                    ExplorerAction.CommandAction.RenderNodeList(fileNodes)
                } else {
                    ExplorerAction.CommandAction.LoadFiles(action.fileNode)
                }
            }
    }

    private fun errorState(e: Throwable): ErrorState {
        return when (e) {
            is PermissionException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_access_denied_dialog_title),
                subtitle = stringProvider.getString(UiR.string.common_access_denied_dialog_message),
                action = ErrorAction.REQUEST_PERMISSIONS,
            )

            is AuthRequiredException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(R.string.explorer_error_view_title_auth),
                subtitle = when (e.authMethod) {
                    AuthMethod.PASSWORD ->
                        stringProvider.getString(R.string.explorer_error_view_message_password)

                    AuthMethod.KEY ->
                        stringProvider.getString(R.string.explorer_error_view_message_passphrase)
                },
                action = when (e.authMethod) {
                    AuthMethod.PASSWORD -> ErrorAction.ENTER_PASSWORD
                    AuthMethod.KEY -> ErrorAction.ENTER_PASSPHRASE
                }
            )

            is AuthenticationException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_error_occurred),
                subtitle = when (e.authMethod) {
                    AuthMethod.PASSWORD ->
                        stringProvider.getString(R.string.explorer_error_view_message_password)

                    AuthMethod.KEY ->
                        stringProvider.getString(R.string.explorer_error_view_message_passphrase)
                },
                action = when (e.authMethod) {
                    AuthMethod.PASSWORD -> ErrorAction.ENTER_PASSWORD
                    AuthMethod.KEY -> ErrorAction.ENTER_PASSPHRASE
                }
            )

            else -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_error_occurred),
                subtitle = e.message.orEmpty(),
                action = ErrorAction.UNDEFINED,
            )
        }
    }

    private suspend fun buildNodeList(state: ExplorerState): List<FileNode> {
        return asyncNodeBuilder.buildNodeList(
            nodes = fileNodeCache.getAll(),
            options = NodeBuilderOptions(
                searchQuery = state.searchQuery,
                showHidden = state.showHiddenFiles,
                sortMode = state.sortMode,
                foldersOnTop = state.foldersOnTop,
                compactPackages = state.compactPackages,
            )
        )
    }
}