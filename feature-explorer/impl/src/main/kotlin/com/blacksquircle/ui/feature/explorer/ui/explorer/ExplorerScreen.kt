/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.explorer.ui.explorer

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.contract.PermissionResult
import com.blacksquircle.ui.core.contract.rememberStorageContract
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.core.effect.NavResultEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.divider.VerticalDivider
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.feature.explorer.data.utils.clipText
import com.blacksquircle.ui.feature.explorer.data.utils.openFileWith
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.ErrorStatus
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.ExplorerActionBar
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.ExplorerToolbar
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.FileExplorer
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.Workspaces
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FilesystemType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.ds.R as UiR

internal const val KEY_AUTHENTICATION = "KEY_AUTHENTICATION"
internal const val KEY_COMPRESS_FILE = "KEY_COMPRESS_FILE"
internal const val KEY_CREATE_FILE = "KEY_CREATE_FILE"
internal const val KEY_CREATE_FOLDER = "KEY_CREATE_FOLDER"
internal const val KEY_CLONE_REPO = "KEY_CLONE_REPO"
internal const val KEY_RENAME_FILE = "KEY_RENAME_FILE"
internal const val KEY_DELETE_FILE = "KEY_DELETE_FILE"

internal const val ARG_USER_INPUT = "ARG_USER_INPUT"
internal const val ARG_SUBMODULES = "ARG_SUBMODULES"

@Composable
internal fun ExplorerScreen(
    navController: NavController,
    viewModel: ExplorerViewModel = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        ExplorerViewModel.Factory().also(component::inject)
    },
    closeDrawer: () -> Unit = {},
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ExplorerScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onWorkspaceClicked = viewModel::onWorkspaceClicked,
        onAddWorkspaceClicked = viewModel::onAddWorkspaceClicked,
        onDeleteWorkspaceClicked = viewModel::onDeleteWorkspaceClicked,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQueryClicked = viewModel::onClearQueryClicked,
        onShowHiddenClicked = viewModel::onShowHiddenClicked,
        onCompactPackagesClicked = viewModel::onCompactPackagesClicked,
        onSortModeSelected = viewModel::onSortModeSelected,
        onCreateClicked = viewModel::onCreateClicked,
        onCloneClicked = viewModel::onCloneClicked,
        onCopyClicked = viewModel::onCopyClicked,
        onPasteClicked = viewModel::onPasteClicked,
        onClearBufferClicked = viewModel::onClearBufferClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        onCutClicked = viewModel::onCutClicked,
        onOpenWithClicked = viewModel::onOpenWithClicked,
        onRenameClicked = viewModel::onRenameClicked,
        onPropertiesClicked = viewModel::onPropertiesClicked,
        onCopyPathClicked = viewModel::onCopyPathClicked,
        onCompressClicked = viewModel::onCompressClicked,
        onErrorActionClicked = viewModel::onErrorActionClicked,
        onFileClicked = viewModel::onFileClicked,
        onFileSelected = viewModel::onFileSelected,
        onRefreshClicked = viewModel::onRefreshClicked,
    )

    val storageContract = rememberStorageContract { result ->
        when (result) {
            PermissionResult.DENIED,
            PermissionResult.DENIED_FOREVER -> viewModel.onPermissionDenied()
            PermissionResult.GRANTED -> viewModel.onPermissionGranted()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> closeDrawer()
                is ExplorerViewEvent.RequestPermission -> {
                    storageContract.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        } else {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                    )
                }
                is ExplorerViewEvent.OpenFileWith -> {
                    context.openFileWith(event.fileModel)
                }
                is ExplorerViewEvent.CopyPath -> {
                    event.fileModel.path.clipText(context)
                }
            }
        }
    }

    NavResultEffect(KEY_AUTHENTICATION) { bundle ->
        val credentials = bundle.getString(ARG_USER_INPUT).orEmpty()
        viewModel.onCredentialsEntered(credentials)
    }
    NavResultEffect(KEY_CREATE_FILE) { bundle ->
        val fileName = bundle.getString(ARG_USER_INPUT).orEmpty()
        viewModel.createFile(fileName)
    }
    NavResultEffect(KEY_CREATE_FOLDER) { bundle ->
        val fileName = bundle.getString(ARG_USER_INPUT).orEmpty()
        viewModel.createFolder(fileName)
    }
    NavResultEffect(KEY_CLONE_REPO) { bundle ->
        val url = bundle.getString(ARG_USER_INPUT).orEmpty()
        val submodules = bundle.getBoolean(ARG_SUBMODULES)
        viewModel.cloneRepository(url, submodules)
    }
    NavResultEffect(KEY_RENAME_FILE) { bundle ->
        val fileName = bundle.getString(ARG_USER_INPUT).orEmpty()
        viewModel.renameFile(fileName)
    }
    NavResultEffect(KEY_DELETE_FILE) {
        viewModel.deleteFile()
    }
    NavResultEffect(KEY_COMPRESS_FILE) { bundle ->
        val fileName = bundle.getString(ARG_USER_INPUT).orEmpty()
        viewModel.compressFiles(fileName)
    }

    CleanupEffect {
        ExplorerComponent.release()
    }
}

@Composable
private fun ExplorerScreen(
    viewState: ExplorerViewState,
    onBackClicked: () -> Unit = {},
    onWorkspaceClicked: (WorkspaceModel) -> Unit = {},
    onAddWorkspaceClicked: () -> Unit = {},
    onDeleteWorkspaceClicked: (WorkspaceModel) -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onShowHiddenClicked: () -> Unit = {},
    onCompactPackagesClicked: () -> Unit = {},
    onSortModeSelected: (SortMode) -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onCloneClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onPasteClicked: () -> Unit = {},
    onClearBufferClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onCutClicked: () -> Unit = {},
    onOpenWithClicked: () -> Unit = {},
    onRenameClicked: () -> Unit = {},
    onPropertiesClicked: () -> Unit = {},
    onCopyPathClicked: () -> Unit = {},
    onCompressClicked: () -> Unit = {},
    onErrorActionClicked: (ErrorAction) -> Unit = {},
    onFileClicked: (FileNode) -> Unit = {},
    onFileSelected: (FileNode) -> Unit = {},
    onRefreshClicked: () -> Unit = {},
) {
    Row(Modifier.fillMaxSize()) {
        Workspaces(
            workspaces = viewState.workspaces,
            selectedWorkspace = viewState.selectedWorkspace,
            onWorkspaceClicked = onWorkspaceClicked,
            onAddWorkspaceClicked = onAddWorkspaceClicked,
            onDeleteWorkspaceClicked = onDeleteWorkspaceClicked,
        )

        if (!SquircleTheme.colors.isDark) {
            VerticalDivider()
        }

        ScaffoldSuite(
            topBar = {
                ExplorerToolbar(
                    filesystemType = viewState.selectedWorkspace
                        ?.filesystemType ?: FilesystemType.LOCAL,
                    searchQuery = viewState.searchQuery,
                    selectedNodes = viewState.selectedNodes,
                    showHidden = viewState.showHidden,
                    compactPackages = viewState.compactPackages,
                    sortMode = viewState.sortMode,
                    onQueryChanged = onQueryChanged,
                    onClearQueryClicked = onClearQueryClicked,
                    onShowHiddenClicked = onShowHiddenClicked,
                    onCompactPackagesClicked = onCompactPackagesClicked,
                    onSortModeSelected = onSortModeSelected,
                    onCopyClicked = onCopyClicked,
                    onDeleteClicked = onDeleteClicked,
                    onCutClicked = onCutClicked,
                    onOpenWithClicked = onOpenWithClicked,
                    onRenameClicked = onRenameClicked,
                    onPropertiesClicked = onPropertiesClicked,
                    onCopyPathClicked = onCopyPathClicked,
                    onCompressClicked = onCompressClicked,
                    onBackClicked = onBackClicked,
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = viewState.showActionBar,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    ExplorerActionBar(
                        taskType = viewState.taskType,
                        onRefreshClicked = onRefreshClicked,
                        onCloneClicked = onCloneClicked,
                        onCreateClicked = onCreateClicked,
                        onPasteClicked = onPasteClicked,
                        onClearBufferClicked = onClearBufferClicked,
                    )
                }
            },
            backgroundColor = SquircleTheme.colors.colorBackgroundSecondary,
            modifier = Modifier.imePadding(),
        ) { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                FileExplorer(
                    contentPadding = contentPadding,
                    fileNodes = if (viewState.showFiles) viewState.fileNodes else emptyList(),
                    selectedNodes = viewState.selectedNodes,
                    onFileClicked = onFileClicked,
                    onFileSelected = onFileSelected,
                )
                if (viewState.isLoading) {
                    CircularProgress(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                if (viewState.isError && !viewState.isLoading) {
                    ErrorStatus(
                        errorState = viewState.errorState,
                        onActionClicked = onErrorActionClicked,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                if (viewState.isEmpty && !viewState.isError && !viewState.isLoading) {
                    EmptyView(
                        iconResId = UiR.drawable.ic_file_find,
                        title = stringResource(UiR.string.common_no_result),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ExplorerScreenPreview() {
    PreviewBackground {
        ExplorerScreen(
            viewState = ExplorerViewState(
                workspaces = listOf(
                    WorkspaceModel(
                        uuid = LocalFilesystem.LOCAL_UUID,
                        name = "Local",
                        filesystemType = FilesystemType.LOCAL,
                        defaultLocation = FileModel(
                            fileUri = "file:///storage/emulated/0/",
                            filesystemUuid = LocalFilesystem.LOCAL_UUID,
                        ),
                    ),
                    WorkspaceModel(
                        uuid = RootFilesystem.ROOT_UUID,
                        name = "Root",
                        filesystemType = FilesystemType.ROOT,
                        defaultLocation = FileModel(
                            fileUri = "sufile:///",
                            filesystemUuid = RootFilesystem.ROOT_UUID,
                        ),
                    ),
                ),
                selectedWorkspace = WorkspaceModel(
                    uuid = LocalFilesystem.LOCAL_UUID,
                    name = "Local",
                    filesystemType = FilesystemType.LOCAL,
                    defaultLocation = FileModel(
                        fileUri = "file:///storage/emulated/0/",
                        filesystemUuid = LocalFilesystem.LOCAL_UUID,
                    ),
                ),
            )
        )
    }
}