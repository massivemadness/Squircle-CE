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

package com.blacksquircle.ui.feature.editor.ui.closefile

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.api.navigation.CloseFileDialog
import com.blacksquircle.ui.feature.editor.ui.editor.ARG_FILE_UUID
import com.blacksquircle.ui.feature.editor.ui.editor.KEY_CLOSE_FILE

@Composable
internal fun CloseFileScreen(
    navArgs: CloseFileDialog,
    navController: NavController,
) {
    CloseFileScreen(
        fileName = navArgs.fileName,
        onConfirmClicked = {
            sendNavigationResult(
                key = KEY_CLOSE_FILE,
                result = bundleOf(ARG_FILE_UUID to navArgs.fileUuid)
            )
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun CloseFileScreen(
    fileName: String,
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    AlertDialog(
        title = fileName,
        content = {
            Text(
                text = stringResource(R.string.dialog_message_close_tab),
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
            )
        },
        confirmButton = stringResource(R.string.action_close),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = onConfirmClicked,
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun CloseFileScreenPreview() {
    PreviewBackground {
        CloseFileScreen(fileName = "untitled")
    }
}