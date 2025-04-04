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

package com.blacksquircle.ui.feature.explorer.ui.permissions

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.explorer.R
import timber.log.Timber
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun NotificationDeniedScreen(navController: NavController) {
    val context = LocalContext.current
    NotificationDeniedScreen(
        onConfirmClicked = {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Timber.e(e, e.message)
                context.showToast(UiR.string.common_error_occurred)
            }
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun NotificationDeniedScreen(
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    AlertDialog(
        title = stringResource(R.string.dialog_title_notification_permission),
        content = {
            Text(
                text = stringResource(R.string.dialog_message_notification_permission),
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
            )
        },
        confirmButton = stringResource(UiR.string.common_continue),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = onConfirmClicked,
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun NotificationDeniedScreenPreview() {
    PreviewBackground {
        NotificationDeniedScreen()
    }
}