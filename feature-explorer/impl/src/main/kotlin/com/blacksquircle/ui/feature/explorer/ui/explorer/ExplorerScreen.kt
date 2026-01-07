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

package com.blacksquircle.ui.feature.explorer.ui.explorer

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
import com.blacksquircle.ui.core.contract.PermissionResult
import com.blacksquircle.ui.core.contract.rememberStorageContract
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.core.effect.ResultEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.divider.VerticalDivider
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.feature.explorer.data.utils.openFileWith
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceType
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.ErrorStatus
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.ExplorerActionBar
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.ExplorerToolbar
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.FileExplorer
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.Workspaces
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerEvent
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.ds.R as UiR

internal const val KEY_SERVER_AUTHENTICATE = "KEY_SERVER_AUTHENTICATE"
internal const val KEY_COMPRESS_FILE = "KEY_COMPRESS_FILE"
internal const val KEY_CREATE_FILE = "KEY_CREATE_FILE"
internal const val KEY_CREATE_FOLDER = "KEY_CREATE_FOLDER"
internal const val KEY_CLONE_REPO = "KEY_CLONE_REPO"
internal const val KEY_RENAME_FILE = "KEY_RENAME_FILE"
internal const val KEY_DELETE_FILE = "KEY_DELETE_FILE"

// FIXME requires :feature-explorer:impl dependency
@Composable
fun DrawerExplorer(closeDrawer: () -> Unit = {}) {
    ExplorerScreen(closeDrawer = closeDrawer)
}

@Composable
internal fun ExplorerScreen(
    viewModel: ExplorerViewModel = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        ExplorerViewModel.Factory().also(component::inject)
    },
    viewModel2: ExplorerViewModel2 = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        ExplorerViewModel2.Factory().also(component::inject)
    },
    closeDrawer: () -> Unit = {},
) {
    val viewState by viewModel2.viewState.collectAsStateWithLifecycle()

    ExplorerScreen(
        viewState = viewState,
        dispatch = viewModel2::dispatch,
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
        viewModel2.events.collect { event ->
            when (event) {
                is ExplorerEvent.Toast -> context.showToast(text = event.message)
                is ExplorerEvent.OpenFileWith -> {
                    context.openFileWith(event.fileModel)
                }

                is ExplorerEvent.CloseDrawer -> closeDrawer()

                /*is ExplorerViewEvent.RequestPermission -> {
                    storageContract.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        } else {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                    )
                }
                is ExplorerViewEvent.CopyPath -> {
                    context.copyText(event.fileModel.path)
                }*/
            }
        }
    }

    ResultEffect<String>(KEY_SERVER_AUTHENTICATE) { credentials ->
        viewModel.onCredentialsEntered(credentials)
    }
    ResultEffect<String>(KEY_CREATE_FILE) { fileName ->
        viewModel2.dispatch(ExplorerAction.UiAction.OnCreateFileClicked(fileName, isFolder = false))
    }
    ResultEffect<String>(KEY_CREATE_FOLDER) { fileName ->
        viewModel2.dispatch(ExplorerAction.UiAction.OnCreateFileClicked(fileName, isFolder = true))
    }
    ResultEffect<String>(KEY_CLONE_REPO) { url ->
        viewModel.cloneRepository(url)
    }
    ResultEffect<String>(KEY_RENAME_FILE) { fileName ->
        viewModel2.dispatch(ExplorerAction.UiAction.OnRenameFileClicked(fileName))
    }
    ResultEffect<Unit>(KEY_DELETE_FILE) {
        viewModel2.dispatch(ExplorerAction.UiAction.OnDeleteFileClicked)
    }
    ResultEffect<String>(KEY_COMPRESS_FILE) { fileName ->
        viewModel.compressFiles(fileName)
    }

    CleanupEffect {
        ExplorerComponent.release()
    }
}

@Composable
private fun ExplorerScreen(
    viewState: ExplorerViewState,
    dispatch: (ExplorerAction.UiAction) -> Unit = {},
) {
    Row(Modifier.fillMaxSize()) {
        Workspaces(
            workspaces = viewState.workspaces,
            selectedWorkspace = viewState.selectedWorkspace,
            onWorkspaceClicked = {
                dispatch(ExplorerAction.UiAction.OnWorkspaceClicked(it))
            },
            onAddWorkspaceClicked = {
                dispatch(ExplorerAction.UiAction.OnAddWorkspaceClicked)
            },
            onDeleteWorkspaceClicked = {
                dispatch(ExplorerAction.UiAction.OnDeleteWorkspaceClicked(it))
            },
        )

        if (!SquircleTheme.colors.isDark) {
            VerticalDivider()
        }

        ScaffoldSuite(
            topBar = {
                ExplorerToolbar(
                    workspaceType = viewState.selectedWorkspace
                        ?.type ?: WorkspaceType.LOCAL,
                    searchQuery = viewState.searchQuery,
                    selection = viewState.selection,
                    showHidden = viewState.showHiddenFiles,
                    compactPackages = viewState.compactPackages,
                    sortMode = viewState.sortMode,
                    dispatch = dispatch,
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
                        dispatch = dispatch,
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
                    selectedNodes = viewState.selection,
                    onFileClicked = {
                        dispatch(ExplorerAction.UiAction.OnFileClicked(it))
                    },
                    onFileSelected = {
                        dispatch(ExplorerAction.UiAction.OnFileSelected(it))
                    },
                )
                if (viewState.isLoading) {
                    CircularProgress(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                if (viewState.isError && !viewState.isLoading) {
                    ErrorStatus(
                        errorState = viewState.errorState,
                        onActionClicked = {
                            dispatch(ExplorerAction.UiAction.OnErrorActionClicked(it))
                        },
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
        val workspaces = listOf(
            WorkspaceModel(
                uuid = LocalFilesystem.LOCAL_UUID,
                name = "Local",
                type = WorkspaceType.LOCAL,
                defaultLocation = FileModel(
                    fileUri = "file:///storage/emulated/0/",
                    filesystemUuid = LocalFilesystem.LOCAL_UUID,
                ),
            ),
            WorkspaceModel(
                uuid = RootFilesystem.ROOT_UUID,
                name = "Root",
                type = WorkspaceType.ROOT,
                defaultLocation = FileModel(
                    fileUri = "sufile:///",
                    filesystemUuid = RootFilesystem.ROOT_UUID,
                ),
            )
        )
        ExplorerScreen(
            viewState = ExplorerViewState(
                workspaces = workspaces,
                selectedWorkspace = workspaces[0],
            )
        )
    }
}