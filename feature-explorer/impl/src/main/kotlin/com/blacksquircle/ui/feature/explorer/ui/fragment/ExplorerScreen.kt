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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.tabs.Breadcrumb
import com.blacksquircle.ui.ds.tabs.BreadcrumbNavigation
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.ui.fragment.internal.ExplorerToolbar
import com.blacksquircle.ui.feature.explorer.ui.fragment.internal.FileExplorer
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.BreadcrumbState
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem

@Composable
internal fun ExplorerScreen(viewModel: ExplorerViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ExplorerScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onFilesystemSelected = viewModel::onFilesystemSelected,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQueryClicked = viewModel::onClearQueryClicked,
        onShowHiddenClicked = viewModel::onShowHiddenClicked,
        onSortModeSelected = viewModel::onSortModeSelected,
        onErrorActionClicked = viewModel::onErrorActionClicked,
        onHomeClicked = viewModel::onHomeClicked,
        onActionClicked = viewModel::onActionClicked,
        onBreadcrumbClicked = viewModel::onBreadcrumbClicked,
        onFileClicked = viewModel::onFileClicked,
        onRefreshClicked = viewModel::onRefreshClicked,
    )
}

@Composable
private fun ExplorerScreen(
    viewState: ExplorerViewState,
    onBackClicked: () -> Unit = {},
    onFilesystemSelected: (String) -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onShowHiddenClicked: () -> Unit = {},
    onSortModeSelected: (SortMode) -> Unit = {},
    onErrorActionClicked: (ErrorAction) -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onActionClicked: () -> Unit = {},
    onBreadcrumbClicked: (BreadcrumbState) -> Unit = {},
    onFileClicked: (FileModel) -> Unit = {},
    onRefreshClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ExplorerToolbar(
                searchQuery = viewState.searchQuery,
                selectedFilesystem = viewState.selectedFilesystem,
                filesystems = viewState.filesystems,
                showHidden = viewState.showHidden,
                sortMode = viewState.sortMode,
                onFilesystemSelected = onFilesystemSelected,
                onQueryChanged = onQueryChanged,
                onClearQueryClicked = onClearQueryClicked,
                onShowHiddenClicked = onShowHiddenClicked,
                onSortModeSelected = onSortModeSelected,
                onBackClicked = onBackClicked,
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Column(Modifier.fillMaxSize()) {
            BreadcrumbNavigation(
                tabs = {
                    viewState.breadcrumbs.fastForEachIndexed { index, state ->
                        Breadcrumb(
                            title = state.fileModel?.name ?: "/",
                            selected = index == viewState.selectedBreadcrumb,
                            onClick = { onBreadcrumbClicked(state) },
                        )
                    }
                },
                selectedIndex = viewState.selectedBreadcrumb,
                onHomeClicked = onHomeClicked,
                onActionClicked = onActionClicked,
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider()

            val breadcrumbState = viewState.breadcrumbs
                .getOrNull(viewState.selectedBreadcrumb)
            if (breadcrumbState != null) {
                FileExplorer(
                    contentPadding = contentPadding,
                    breadcrumbState = breadcrumbState,
                    isLoading = viewState.isLoading,
                    onFileClicked = onFileClicked,
                    onErrorActionClicked = onErrorActionClicked,
                    onRefreshClicked = onRefreshClicked,
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
                selectedFilesystem = LocalFilesystem.LOCAL_UUID,
                breadcrumbs = listOf(
                    BreadcrumbState(
                        fileModel = null,
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