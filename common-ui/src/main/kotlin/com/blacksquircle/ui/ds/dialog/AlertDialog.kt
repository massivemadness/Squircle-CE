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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = properties
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = SquircleTheme.colors.colorBackgroundSecondary,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = SquircleTheme.typography.text18Medium,
                    color = SquircleTheme.colors.colorTextAndIconPrimary,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                content()

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    if (dismissButton != null) {
                        TextButton(
                            text = dismissButton.uppercase(),
                            onClick = onDismissClicked
                        )
                    }
                    if (dismissButton != null && confirmButton != null) {
                        Spacer(Modifier.width(8.dp))
                    }
                    if (confirmButton != null) {
                        TextButton(
                            text = confirmButton.uppercase(),
                            onClick = onConfirmClicked
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AlertDialogPreview() {
    SquircleTheme {
        AlertDialog(
            title = "Alert Dialog",
            content = {
                Text(
                    text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
                    style = SquircleTheme.typography.text14Regular,
                    color = SquircleTheme.colors.colorTextAndIconSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            },
            confirmButton = "Confirm",
            dismissButton = "Cancel",
            onDismissClicked = {},
            onDismiss = {},
        )
    }
}