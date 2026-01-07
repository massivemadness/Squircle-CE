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

internal class MoveFileMiddleware @Inject constructor(
    private val explorerRepository: ExplorerRepository,
    private val taskManager: TaskManager,
    private val fileNodeCache: FileNodeCache,
    private val navigator: Navigator,
) : Middleware<ExplorerState, ExplorerAction> {

    override fun bind(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return merge(
            onCutClicked(state, actions),
            onMoveFileClicked(state, actions),
        )
    }

    private fun onCutClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnCutClicked>()
            .map {
                val currentState = state.first()
                val fileNodes = currentState.selection.toList()

                ExplorerAction.CommandAction.FillBuffer(
                    taskType = TaskType.MOVE,
                    taskBuffer = fileNodes,
                )
            }
    }

    private fun onMoveFileClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnMoveFileClicked>()
            .transform { action ->
                val currentState = state.first()

                val parentNode = action.parent
                val fileNodes = currentState.taskBuffer
                val fileModels = fileNodes.map(FileNode::file)

                val taskId = explorerRepository.moveFiles(fileModels, parentNode.file)
                val screen = TaskRoute(taskId)
                navigator.navigate(screen)

                emit(ExplorerAction.CommandAction.ResetBuffer)

                val task = taskManager.monitor(taskId).first { it.isFinished }

                when (val status = task.status) {
                    is TaskStatus.Error -> {
                        emit(ExplorerAction.CommandAction.TaskFailed(status.exception))
                    }

                    is TaskStatus.Done -> {
                        fileNodes.forEach(fileNodeCache::removeNode)

                        emit(ExplorerAction.CommandAction.TaskComplete(task))
                        emit(ExplorerAction.CommandAction.LoadFiles(parentNode))
                    }

                    else -> Unit
                }
            }
    }
}