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

package com.blacksquircle.ui.ds.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.TextButton

@Composable
fun AlertDialog(
    title: String,
    content: @Composable (() -> Unit),
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: String? = null,
    dismissButton: String? = null,
    onConfirmClicked: () -> Unit = {},
    onDismissClicked: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = content,
        confirmButton = {
            if (confirmButton != null) {
                TextButton(
                    text = confirmButton,
                    onClick = onConfirmClicked
                )
            }
        },
        dismissButton = {
            if (dismissButton != null) {
                TextButton(
                    text = dismissButton,
                    onClick = onDismissClicked
                )
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = properties,
    )
}

@Preview
@Composable
private fun AlertDialogPreview() {
    SquircleTheme {
        AlertDialog(
            title = "Alert Dialog",
            content = {
                Text(text = "Content")
            },
            confirmButton = "Confirm",
            dismissButton = "Cancel",
            onDismissClicked = {},
            onDismiss = {},
        )
    }
}