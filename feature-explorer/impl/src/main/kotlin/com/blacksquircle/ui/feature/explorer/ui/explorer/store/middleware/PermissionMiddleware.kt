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

import com.blacksquircle.ui.feature.explorer.api.navigation.StorageDeniedRoute
import com.blacksquircle.ui.feature.explorer.data.node.FileNodeCache
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.navigation.api.Navigator
import com.blacksquircle.ui.redux.middleware.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import kotlin.collections.firstOrNull

internal class PermissionMiddleware @Inject constructor(
    private val fileNodeCache: FileNodeCache,
    private val navigator: Navigator,
) : Middleware<ExplorerState, ExplorerAction> {

    override fun bind(state: Flow<ExplorerState>, actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return merge(
            onPermissionGranted(actions),
            onPermissionDenied(actions),
        )
    }

    private fun onPermissionGranted(actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnPermissionGranted>()
            .transform {
                val fileNode = fileNodeCache.get(NodeKey.Root).firstOrNull()
                if (fileNode != null) {
                    emit(ExplorerAction.CommandAction.LoadFiles(fileNode))
                }
            }
    }

    private fun onPermissionDenied(actions: Flow<ExplorerAction>): Flow<ExplorerAction> {
        return actions.filterIsInstance<ExplorerAction.UiAction.OnPermissionDenied>()
            .transform {
                navigator.navigate(StorageDeniedRoute)
            }
    }
}