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

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.CompressFileRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.TaskRoute
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.node.FileNodeCache
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.navigation.api.Navigator
import com.blacksquircle.ui.redux.middleware.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import kotlin.collections.map

internal class CompressFileMiddleware @Inject constructor(
    private val stringProvider: StringProvider,
    private val explorerRepository: ExplorerRepository,
    private val taskManager: TaskManager,
    private val fileNodeCache: FileNodeCache,
    private val navigator: Navigator,
) : Middleware<ExplorerState, ExplorerAction> {

    override fun bind(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return merge(
            onCompressClicked(state, actions),
            onCompressFileClicked(state, actions),
        )
    }

    private fun onCompressClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnCompressClicked>()
            .map {
                val currentState = state.first()
                val fileNodes = currentState.selection.toList()

                if (!fileNodeCache.ensureCommonParentKey(fileNodes)) {
                    val message = stringProvider.getString(R.string.explorer_toast_same_directory_required)
                    return@map ExplorerAction.CommandAction.TaskFailed(IllegalStateException(message))
                }

                navigator.navigate(CompressFileRoute)

                ExplorerAction.CommandAction.FillBuffer(
                    taskType = TaskType.COMPRESS,
                    taskBuffer = fileNodes,
                )
            }
    }

    private fun onCompressFileClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnCompressFileClicked>()
            .transform { action ->
                val currentState = state.first()

                val fileModels = currentState.taskBuffer.map(FileNode::file)
                val fileNode = currentState.taskBuffer.firstOrNull() ?: return@transform
                val parentNode = fileNodeCache.parentNode(fileNode) ?: return@transform

                val taskId = explorerRepository.compressFiles(fileModels, parentNode.file, action.fileName)
                val screen = TaskRoute(taskId)
                navigator.navigate(screen)

                emit(ExplorerAction.CommandAction.ResetBuffer)

                val task = taskManager.monitor(taskId).first { it.isFinished }

                when (val status = task.status) {
                    is TaskStatus.Error -> {
                        emit(ExplorerAction.CommandAction.TaskFailed(status.exception))
                    }

                    is TaskStatus.Done -> {
                        emit(ExplorerAction.CommandAction.TaskComplete(task))
                        emit(ExplorerAction.CommandAction.LoadFiles(parentNode))
                    }

                    else -> Unit
                }
            }
    }
}