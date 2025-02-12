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

package com.blacksquircle.ui.feature.explorer.ui.fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.tabs.Breadcrumb
import com.blacksquircle.ui.ds.tabs.BreadcrumbNavigation
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.ui.fragment.internal.CompactFileItem
import com.blacksquircle.ui.feature.explorer.ui.fragment.internal.ExplorerError
import com.blacksquircle.ui.feature.explorer.ui.fragment.internal.ExplorerToolbar
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileTree
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ExplorerScreen(viewModel: ExplorerViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ExplorerScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onFilesystemSelected = viewModel::onFilesystemSelected,
        onGrantPermissionClicked = viewModel::onPermissionRequested,
        onHomeClicked = viewModel::onHomeClicked,
        onActionClicked = viewModel::onActionClicked,
        onBreadcrumbClicked = viewModel::onBreadcrumbClicked,
        onFileClicked = viewModel::onFileClicked,
    )
}

@Composable
private fun ExplorerScreen(
    viewState: ExplorerViewState,
    onBackClicked: () -> Unit = {},
    onFilesystemSelected: (String) -> Unit = {},
    onGrantPermissionClicked: () -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onActionClicked: () -> Unit = {},
    onBreadcrumbClicked: (FileTree) -> Unit = {},
    onFileClicked: (FileModel) -> Unit = {},
) {
    Scaffold(
        topBar = {
            ExplorerToolbar(
                currentFilesystem = viewState.selectedFilesystem,
                filesystems = viewState.filesystems,
                onFilesystemSelected = onFilesystemSelected,
                onBackClicked = onBackClicked,
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val files = viewState.breadcrumbs
                .getOrNull(viewState.selectedBreadcrumb)
                ?.children.orEmpty()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopStart)
            ) {
                BreadcrumbNavigation(
                    tabs = {
                        viewState.breadcrumbs.fastForEachIndexed { index, fileTree ->
                            Breadcrumb(
                                title = fileTree.parent?.name ?: "/",
                                selected = index == viewState.selectedBreadcrumb,
                                onClick = { onBreadcrumbClicked(fileTree) },
                            )
                        }
                    },
                    selectedIndex = viewState.selectedBreadcrumb,
                    onHomeClicked = onHomeClicked,
                    onActionClicked = onActionClicked,
                    modifier = Modifier.fillMaxWidth(),
                )

                HorizontalDivider()

                if (!viewState.isLoading && !viewState.isError) {
                    LazyColumn(
                        contentPadding = contentPadding,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(files) { fileModel ->
                            CompactFileItem(
                                fileModel = fileModel,
                                onClick = { onFileClicked(fileModel) },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                }
            }

            if (viewState.isError) {
                ExplorerError(
                    errorState = viewState.errorState,
                    onGrantPermissionClicked = onGrantPermissionClicked,
                )
                return@Scaffold
            }

            if (viewState.isLoading) {
                CircularProgress()
                return@Scaffold
            }

            if (files.isEmpty()) {
                EmptyView(
                    iconResId = UiR.drawable.ic_file_find,
                    title = stringResource(UiR.string.common_no_result),
                )
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
                selectedFilesystem = LocalFilesystem.LOCAL_UUID,
                filesystems = listOf(
                    FilesystemModel(
                        uuid = LocalFilesystem.LOCAL_UUID,
                        title = "Local Storage",
                    ),
                    FilesystemModel(
                        uuid = RootFilesystem.ROOT_UUID,
                        title = "Root Storage",
                    ),
                ),
                selectedBreadcrumb = 0,
                breadcrumbs = emptyList(),
                errorState = ErrorState(
                    title = "Error",
                    subtitle = "Please try again",
                    action = ErrorAction.REQUEST_PERMISSIONS,
                )
            )
        )
    }
}