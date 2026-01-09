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

package com.blacksquircle.ui.feature.explorer.ui.explorer.store

import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.CloneRepoMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.CompressFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.CopyFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.CreateFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.DeleteFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.ExplorerMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.ExtractFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.FileTreeMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.MoveFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.OpenFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.PermissionMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.RenameFileMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.ServerMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.SortingMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.middleware.WorkspaceMiddleware
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.reducer.ExplorerReducer
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.reducer.FileTaskReducer
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.reducer.FileTreeReducer
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.reducer.SortingReducer
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.reducer.WorkspaceReducer
import com.blacksquircle.ui.redux.reducer.CompoundReducer
import com.blacksquircle.ui.redux.store.Store
import com.blacksquircle.ui.redux.store.produceStore
import javax.inject.Inject

internal class ExplorerStore @Inject constructor(
    private val explorerReducer: ExplorerReducer,
    private val fileTaskReducer: FileTaskReducer,
    private val fileTreeReducer: FileTreeReducer,
    private val sortingReducer: SortingReducer,
    private val workspaceReducer: WorkspaceReducer,
    private val explorerMiddleware: ExplorerMiddleware,
    private val workspaceMiddleware: WorkspaceMiddleware,
    private val fileTreeMiddleware: FileTreeMiddleware,
    private val sortingMiddleware: SortingMiddleware,
    private val openFileMiddleware: OpenFileMiddleware,
    private val createFileMiddleware: CreateFileMiddleware,
    private val renameFileMiddleware: RenameFileMiddleware,
    private val deleteFileMiddleware: DeleteFileMiddleware,
    private val copyFileMiddleware: CopyFileMiddleware,
    private val moveFileMiddleware: MoveFileMiddleware,
    private val compressFileMiddleware: CompressFileMiddleware,
    private val extractFileMiddleware: ExtractFileMiddleware,
    private val cloneRepoMiddleware: CloneRepoMiddleware,
    private val serverMiddleware: ServerMiddleware,
    private val permissionMiddleware: PermissionMiddleware,
) : Store<ExplorerState, ExplorerAction, ExplorerEvent> by produceStore(
        initialState = ExplorerState(),
        initialAction = ExplorerAction.Init,
        reducer = CompoundReducer(
            reducers = listOf(
                explorerReducer,
                fileTaskReducer,
                fileTreeReducer,
                sortingReducer,
                workspaceReducer,
            )
        ),
        middlewares = listOf(
            explorerMiddleware,
            workspaceMiddleware,
            fileTreeMiddleware,
            sortingMiddleware,
            openFileMiddleware,
            createFileMiddleware,
            renameFileMiddleware,
            deleteFileMiddleware,
            copyFileMiddleware,
            moveFileMiddleware,
            compressFileMiddleware,
            extractFileMiddleware,
            cloneRepoMiddleware,
            serverMiddleware,
            permissionMiddleware,
        )
    )