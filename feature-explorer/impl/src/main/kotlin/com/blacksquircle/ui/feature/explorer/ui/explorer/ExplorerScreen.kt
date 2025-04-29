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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.util.fastForEachIndexed
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
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.feature.explorer.data.utils.clipText
import com.blacksquircle.ui.feature.explorer.data.utils.openFileWith
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.Breadcrumb
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.BreadcrumbNavigation
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.ExplorerToolbar
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.FileExplorer
import com.blacksquircle.ui.feature.explorer.ui.explorer.compose.Filesystems
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.BreadcrumbState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.servers.api.navigation.CloudScreen
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FilesystemType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem

internal const val KEY_AUTHENTICATION = "KEY_AUTHENTICATION"
internal const val KEY_COMPRESS_FILE = "KEY_COMPRESS_FILE"
internal const val KEY_CREATE_FILE = "KEY_CREATE_FILE"
internal const val KEY_CREATE_FOLDER = "KEY_CREATE_FOLDER"
internal const val KEY_CLONE_REPO = "KEY_CLONE_REPO"
internal const val KEY_RENAME_FILE = "KEY_RENAME_FILE"
internal const val KEY_DELETE_FILE = "KEY_DELETE_FILE"

internal const val ARG_USER_INPUT = "ARG_USER_INPUT"

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
        onFilesystemClicked = viewModel::onFilesystemClicked,
        onAddFilesystemClicked = viewModel::onAddFilesystemClicked,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQueryClicked = viewModel::onClearQueryClicked,
        onShowHiddenClicked = viewModel::onShowHiddenClicked,
        onSortModeSelected = viewModel::onSortModeSelected,
        onCreateFileClicked = viewModel::onCreateFileClicked,
        onCreateFolderClicked = viewModel::onCreateFolderClicked,
        onCloneRepoClicked = viewModel::onCloneRepoClicked,
        onCopyClicked = viewModel::onCopyClicked,
        onPasteClicked = viewModel::onPasteClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        onCutClicked = viewModel::onCutClicked,
        onSelectAllClicked = viewModel::onSelectAllClicked,
        onOpenWithClicked = viewModel::onOpenWithClicked,
        onRenameClicked = viewModel::onRenameClicked,
        onPropertiesClicked = viewModel::onPropertiesClicked,
        onCopyPathClicked = viewModel::onCopyPathClicked,
        onCompressClicked = viewModel::onCompressClicked,
        onErrorActionClicked = viewModel::onErrorActionClicked,
        onHomeClicked = viewModel::onHomeClicked,
        onBreadcrumbClicked = viewModel::onBreadcrumbClicked,
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

    NavResultEffect(CloudScreen.KEY_SAVE) {
        viewModel.onFilesystemAdded()
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
        viewModel.cloneRepository(url)
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
    onFilesystemClicked: (FilesystemModel) -> Unit = {},
    onAddFilesystemClicked: () -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onShowHiddenClicked: () -> Unit = {},
    onSortModeSelected: (SortMode) -> Unit = {},
    onCreateFileClicked: () -> Unit = {},
    onCreateFolderClicked: () -> Unit = {},
    onCloneRepoClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onPasteClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onCutClicked: () -> Unit = {},
    onSelectAllClicked: () -> Unit = {},
    onOpenWithClicked: () -> Unit = {},
    onRenameClicked: () -> Unit = {},
    onPropertiesClicked: () -> Unit = {},
    onCopyPathClicked: () -> Unit = {},
    onCompressClicked: () -> Unit = {},
    onErrorActionClicked: (ErrorAction) -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onBreadcrumbClicked: (BreadcrumbState) -> Unit = {},
    onFileClicked: (FileModel) -> Unit = {},
    onFileSelected: (FileModel) -> Unit = {},
    onRefreshClicked: () -> Unit = {},
) {
    Row(Modifier.fillMaxSize()) {
        Filesystems(
            filesystems = viewState.filesystems,
            selectedFilesystem = viewState.selectedFilesystem,
            onFilesystemClicked = onFilesystemClicked,
            onAddFilesystemClicked = onAddFilesystemClicked,
        )

        VerticalDivider()

        ScaffoldSuite(
            topBar = {
                ExplorerToolbar(
                    searchQuery = viewState.searchQuery,
                    selectedFiles = viewState.selectedFiles,
                    showHidden = viewState.showHidden,
                    sortMode = viewState.sortMode,
                    onQueryChanged = onQueryChanged,
                    onClearQueryClicked = onClearQueryClicked,
                    onShowHiddenClicked = onShowHiddenClicked,
                    onSortModeSelected = onSortModeSelected,
                    onCopyClicked = onCopyClicked,
                    onDeleteClicked = onDeleteClicked,
                    onCutClicked = onCutClicked,
                    onSelectAllClicked = onSelectAllClicked,
                    onOpenWithClicked = onOpenWithClicked,
                    onRenameClicked = onRenameClicked,
                    onPropertiesClicked = onPropertiesClicked,
                    onCopyPathClicked = onCopyPathClicked,
                    onCompressClicked = onCompressClicked,
                    onBackClicked = onBackClicked,
                )
            },
            backgroundColor = SquircleTheme.colors.colorBackgroundSecondary,
            modifier = Modifier.imePadding(),
        ) { contentPadding ->
            Column(Modifier.fillMaxSize()) {
                BreadcrumbNavigation(
                    tabs = {
                        viewState.breadcrumbs.fastForEachIndexed { index, state ->
                            Breadcrumb(
                                title = if (index == 0) "/" else state.fileModel.name,
                                selected = index == viewState.selectedBreadcrumb,
                                onClick = { onBreadcrumbClicked(state) },
                            )
                        }
                    },
                    selectedIndex = viewState.selectedBreadcrumb,
                    taskType = viewState.taskType,
                    onHomeClicked = onHomeClicked,
                    onPasteClicked = onPasteClicked,
                    onCreateFileClicked = onCreateFileClicked,
                    onCreateFolderClicked = onCreateFolderClicked,
                    onCloneRepoClicked = onCloneRepoClicked,
                    modifier = Modifier.fillMaxWidth(),
                )

                val breadcrumbState = viewState.breadcrumbs
                    .getOrNull(viewState.selectedBreadcrumb)
                if (breadcrumbState != null) {
                    FileExplorer(
                        contentPadding = contentPadding,
                        breadcrumbState = breadcrumbState,
                        selectedFiles = viewState.selectedFiles,
                        viewMode = viewState.viewMode,
                        isLoading = viewState.isLoading,
                        onFileClicked = onFileClicked,
                        onFileSelected = onFileSelected,
                        onErrorActionClicked = onErrorActionClicked,
                        onRefreshClicked = onRefreshClicked,
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
                filesystems = listOf(
                    FilesystemModel(
                        uuid = LocalFilesystem.LOCAL_UUID,
                        type = FilesystemType.LOCAL,
                        title = "Local",
                        defaultLocation = FileModel(
                            fileUri = "file:///storage/emulated/0/",
                            filesystemUuid = LocalFilesystem.LOCAL_UUID,
                        ),
                    ),
                    FilesystemModel(
                        uuid = RootFilesystem.ROOT_UUID,
                        type = FilesystemType.ROOT,
                        title = "Root",
                        defaultLocation = FileModel(
                            fileUri = "sufile:///",
                            filesystemUuid = RootFilesystem.ROOT_UUID,
                        ),
                    ),
                ),
                selectedFilesystem = FilesystemModel(
                    uuid = LocalFilesystem.LOCAL_UUID,
                    type = FilesystemType.LOCAL,
                    title = "Local",
                    defaultLocation = FileModel(
                        fileUri = "file:///storage/emulated/0/",
                        filesystemUuid = LocalFilesystem.LOCAL_UUID,
                    ),
                ),
                breadcrumbs = listOf(
                    BreadcrumbState(
                        fileModel = FileModel("file://", LocalFilesystem.LOCAL_UUID),
                        fileList = emptyList(),
                        errorState = ErrorState(
                            title = "Error",
                            subtitle = "Please try again",
                            action = ErrorAction.REQUEST_PERMISSIONS,
                        )
                    )
                ),
                selectedBreadcrumb = 0,
                isLoading = false,
            )
        )
    }
}