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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteWorkspaceDialog
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun DeleteWorkspaceScreen(
    navArgs: DeleteWorkspaceDialog,
    navController: NavController,
    viewModel: DeleteWorkspaceViewModel = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        DeleteWorkspaceViewModel.Factory().also(component::inject)
    }
) {
    DeleteWorkspaceScreen(
        name = navArgs.name,
        onConfirmClicked = {
            viewModel.onDeleteWorkspaceClicked(navArgs.uuid)
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        },
    )
}

@Composable
private fun DeleteWorkspaceScreen(
    name: String,
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    AlertDialog(
        title = name,
        content = {
            Text(
                text = stringResource(R.string.dialog_message_delete_workspace),
                style = SquircleTheme.typography.text16Regular,
                color = SquircleTheme.colors.colorTextAndIconSecondary,
            )
        },
        confirmButton = stringResource(UiR.string.common_delete),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = onConfirmClicked,
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun DeleteWorkspaceScreenPreview() {
    PreviewBackground {
        DeleteWorkspaceScreen("Documents")
    }
}