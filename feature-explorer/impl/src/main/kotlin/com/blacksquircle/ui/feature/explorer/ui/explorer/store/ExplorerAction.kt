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

import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.redux.MVIAction

internal sealed interface ExplorerAction : MVIAction {

    data object Init : ExplorerAction
    data class Error(val error: Throwable) : ExplorerAction

    sealed interface UiAction : ExplorerAction {
        data object OnBackClicked : UiAction

        data class OnWorkspaceClicked(val workspace: WorkspaceModel) : UiAction
        data object OnAddWorkspaceClicked : UiAction
        data class OnDeleteWorkspaceClicked(val workspace: WorkspaceModel) : UiAction

        sealed interface QueryAction : UiAction
        data class OnQueryChanged(val query: String) : QueryAction
        data object OnClearQueryClicked : QueryAction

        data object OnShowHiddenFilesClicked : UiAction
        data object OnCompactPackagesClicked : UiAction
        data class OnSortModeSelected(val sortMode: SortMode) : UiAction

        data object OnCreateClicked : UiAction
        data object OnRenameClicked : UiAction
        data object OnDeleteClicked : UiAction
        data object OnCutClicked : UiAction
        data object OnCopyClicked : UiAction
        data object OnPasteClicked : UiAction
        data object OnCompressClicked : UiAction
        data object OnClearBufferClicked : UiAction
        data object OnCloneClicked : UiAction
        data object OnOpenWithClicked : UiAction
        data object OnOpenTerminalClicked : UiAction
        data object OnPropertiesClicked : UiAction
        data object OnCopyPathClicked : UiAction
        data class OnErrorActionClicked(val errorAction: ErrorAction) : UiAction

        data class OnFileClicked(val fileNode: FileNode) : UiAction
        data class OnFileSelected(val fileNode: FileNode) : UiAction
        data object OnRefreshClicked : UiAction
    }

    sealed interface CommandAction : ExplorerAction {

        data class WorkspacesLoaded(
            val workspaces: List<WorkspaceModel>,
            val selectedWorkspace: WorkspaceModel,
            val showHiddenFiles: Boolean,
            val compactPackages: Boolean,
            val foldersOnTop: Boolean,
            val sortMode: SortMode,
            val fileNode: FileNode,
        ) : CommandAction

        data class LoadFiles(val fileNode: FileNode) : CommandAction
        data class LoadFilesError(val fileNode: FileNode, val error: Throwable) : CommandAction
        data class UpdateFiles(val fileNodes: List<FileNode>) : CommandAction
    }
}