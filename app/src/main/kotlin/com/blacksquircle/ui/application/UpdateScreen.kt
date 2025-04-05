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

package com.blacksquircle.ui.application

import androidx.activity.compose.LocalActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.blacksquircle.ui.R
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.utils.InAppUpdate
import kotlinx.serialization.Serializable

@Serializable
internal data object UpdateDialog

@Composable
internal fun UpdateScreen(
    navController: NavController,
    inAppUpdate: InAppUpdate,
) {
    val activity = LocalActivity.current
    UpdateScreen(
        onConfirmClicked = {
            navController.popBackStack()
            if (activity != null) {
                inAppUpdate.installUpdate(activity)
            }
        },
        onCancelClicked = {
            navController.popBackStack()
        },
    )
}

@Composable
private fun UpdateScreen(
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
) {
    AlertDialog(
        title = stringResource(R.string.dialog_title_update),
        content = {
            Text(
                text = stringResource(R.string.dialog_message_update),
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
            )
        },
        confirmButton = stringResource(R.string.action_install),
        dismissButton = stringResource(R.string.action_later),
        onConfirmClicked = onConfirmClicked,
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
        properties = DialogProperties(
            dismissOnClickOutside = false,
        )
    )
}

@PreviewLightDark
@Composable
private fun UpdateScreenPreview() {
    PreviewBackground {
        UpdateScreen()
    }
}