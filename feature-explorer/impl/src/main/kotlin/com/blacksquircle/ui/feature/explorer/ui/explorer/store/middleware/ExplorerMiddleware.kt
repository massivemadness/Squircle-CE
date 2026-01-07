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

import com.blacksquircle.ui.feature.explorer.api.navigation.PropertiesRoute
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.feature.terminal.api.interactor.TerminalInteractor
import com.blacksquircle.ui.feature.terminal.api.model.ShellArgs
import com.blacksquircle.ui.feature.terminal.api.navigation.TerminalRoute
import com.blacksquircle.ui.navigation.api.Navigator
import com.blacksquircle.ui.redux.middleware.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

internal class ExplorerMiddleware @Inject constructor(
    private val terminalInteractor: TerminalInteractor,
    private val navigator: Navigator,
) : Middleware<ExplorerState, ExplorerAction> {

    override fun bind(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return merge(
            onPropertiesClicked(state, actions),
            onOpenTerminalClicked(state, actions),
        )
    }

    private fun onPropertiesClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnPropertiesClicked>()
            .transform {
                val currentState = state.first()
                val fileNode = currentState.selection.firstOrNull()
                if (fileNode != null) {
                    val screen = PropertiesRoute(
                        fileName = fileNode.file.name,
                        filePath = fileNode.file.path,
                        fileSize = fileNode.file.size,
                        lastModified = fileNode.file.lastModified,
                        permission = fileNode.file.permission,
                    )
                    navigator.navigate(screen)
                }
                emit(ExplorerAction.CommandAction.ResetBuffer)
            }
    }

    private fun onOpenTerminalClicked(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnOpenTerminalClicked>()
            .transform {
                val currentState = state.first()
                val fileNode = currentState.selection.firstOrNull()
                if (fileNode != null) {
                    if (terminalInteractor.isTermux()) {
                        val args = ShellArgs(workingDir = fileNode.file.path)
                        terminalInteractor.openTermux(args)
                    } else {
                        val screen = TerminalRoute(workingDir = fileNode.file.path)
                        navigator.navigate(screen)
                    }
                }
                emit(ExplorerAction.CommandAction.ResetBuffer)
            }
    }
}