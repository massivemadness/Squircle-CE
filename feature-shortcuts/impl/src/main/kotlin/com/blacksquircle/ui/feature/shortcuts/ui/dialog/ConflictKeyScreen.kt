/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.shortcuts.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.sendFragmentResult
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.ui.fragment.ShortcutsFragment

@Composable
internal fun ConflictKeyScreen(
    onReassignClicked: (Boolean) -> Unit,
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
        confirmButton = stringResource(com.blacksquircle.ui.ds.R.string.common_continue),
        onConfirmClicked = { onReassignClicked(true) },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = { onReassignClicked(false) },
        onDismiss = { onReassignClicked(false) },
    )
}

@Preview
@Composable
private fun ConflictScreenPreview() {
    SquircleTheme {
        ConflictKeyScreen(
            onReassignClicked = {}
        )
    }
}