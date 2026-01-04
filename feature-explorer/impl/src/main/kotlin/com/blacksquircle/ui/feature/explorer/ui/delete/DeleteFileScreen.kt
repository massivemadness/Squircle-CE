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

package com.blacksquircle.ui.feature.explorer.ui.delete

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.core.effect.ResultEventBus
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteFileRoute
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.create.CreateFileViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.KEY_DELETE_FILE

@Composable
internal fun DeleteFileScreen(
    navArgs: DeleteFileRoute,
    viewModel: DeleteFileViewModel = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        DeleteFileViewModel.Factory().also(component::inject)
    }
) {
    DeleteFileScreen(
        fileName = navArgs.fileName,
        fileCount = navArgs.fileCount,
        onConfirmClicked = {
            ResultEventBus.sendResult(KEY_DELETE_FILE, Unit)
            viewModel.onBackClicked()
        },
        onCancelClicked = viewModel::onBackClicked,
    )
}

@Composable
private fun DeleteFileScreen(
    fileName: String,
    fileCount: Int,
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    val isMultiDelete = fileCount > 1

    AlertDialog(
        title = if (isMultiDelete) {
            stringResource(R.string.explorer_multidelete_dialog_title)
        } else {
            fileName
        },
        content = {
            Text(
                text = if (isMultiDelete) {
                    stringResource(R.string.explorer_multidelete_dialog_message)
                } else {
                    stringResource(R.string.explorer_delete_dialog_message)
                },
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
            )
        },
        confirmButton = stringResource(R.string.explorer_delete_dialog_button_delete),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = onConfirmClicked,
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun DeleteFileScreenPreview() {
    PreviewBackground {
        DeleteFileScreen(
            fileName = "untitled.txt",
            fileCount = 1,
        )
    }
}