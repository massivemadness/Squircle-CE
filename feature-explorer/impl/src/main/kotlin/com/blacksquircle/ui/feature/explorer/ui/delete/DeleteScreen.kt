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

package com.blacksquircle.ui.feature.explorer.ui.delete

import android.os.Bundle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteDialog
import com.blacksquircle.ui.feature.explorer.ui.explorer.KEY_DELETE_FILE
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun DeleteScreen(
    navArgs: DeleteDialog,
    navController: NavController,
) {
    DeleteScreen(
        fileName = navArgs.fileName,
        fileCount = navArgs.fileCount,
        onConfirmClicked = {
            sendNavigationResult(
                key = KEY_DELETE_FILE,
                result = Bundle.EMPTY,
            )
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun DeleteScreen(
    fileName: String,
    fileCount: Int,
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    val isMultiDelete = fileCount > 1

    AlertDialog(
        title = if (isMultiDelete) {
            stringResource(R.string.dialog_title_multi_delete)
        } else {
            fileName
        },
        content = {
            Text(
                text = if (isMultiDelete) {
                    stringResource(R.string.dialog_message_multi_delete)
                } else {
                    stringResource(R.string.dialog_message_delete)
                },
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
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
private fun DeleteScreenPreview() {
    PreviewBackground {
        DeleteScreen(
            fileName = "untitled.txt",
            fileCount = 1,
        )
    }
}