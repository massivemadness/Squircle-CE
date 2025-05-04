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

package com.blacksquircle.ui.feature.explorer.ui.workspace

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.layout.ActionLayout
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun WorkspaceScreen(navController: NavController) {
    WorkspaceScreen(
        onLocalDirectoryClicked = {},
        onInternalStorageClicked = {},
        onRemoteServerClicked = {},
        onBackClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun WorkspaceScreen(
    onLocalDirectoryClicked: () -> Unit = {},
    onInternalStorageClicked: () -> Unit = {},
    onRemoteServerClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    AlertDialog(
        title = stringResource(R.string.dialog_title_add_workspace),
        horizontalPadding = false,
        content = {
            Column {
                ActionLayout(
                    iconRes = UiR.drawable.ic_folder_plus,
                    title = stringResource(R.string.workspace_local_storage_title),
                    subtitle = stringResource(R.string.workspace_local_storage_description),
                    onClick = onLocalDirectoryClicked,
                )
                ActionLayout(
                    iconRes = UiR.drawable.ic_folder_open,
                    title = stringResource(R.string.workspace_internal_storage_title),
                    subtitle = stringResource(R.string.workspace_internal_storage_description),
                    onClick = onInternalStorageClicked,
                )
                ActionLayout(
                    iconRes = UiR.drawable.ic_server_network,
                    title = stringResource(R.string.workspace_remote_server_title),
                    subtitle = stringResource(R.string.workspace_remote_server_description),
                    onClick = onRemoteServerClicked,
                )
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onBackClicked,
        onDismiss = onBackClicked
    )
}

@PreviewLightDark
@Composable
private fun WorkspaceScreenPreview() {
    PreviewBackground {
        WorkspaceScreen()
    }
}