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

package com.blacksquircle.ui.feature.shortcuts.ui.conflict

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
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.ARG_REASSIGN
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.KEY_RESOLVE
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ConflictKeyScreen(navController: NavController) {
    ConflictKeyScreen(
        onReassignClicked = { reassign ->
            sendNavigationResult(
                key = KEY_RESOLVE,
                result = bundleOf(ARG_REASSIGN to reassign)
            )
            navController.popBackStack()
        }
    )
}

@Composable
private fun ConflictKeyScreen(
    onReassignClicked: (Boolean) -> Unit = {},
) {
    AlertDialog(
        title = stringResource(android.R.string.dialog_alert_title),
        content = {
            Text(
                text = stringResource(R.string.shortcut_conflict),
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
            )
        },
        confirmButton = stringResource(UiR.string.common_continue),
        onConfirmClicked = { onReassignClicked(true) },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = { onReassignClicked(false) },
        onDismiss = { onReassignClicked(false) },
    )
}

@PreviewLightDark
@Composable
private fun ConflictKeyScreenPreview() {
    PreviewBackground {
        ConflictKeyScreen()
    }
}