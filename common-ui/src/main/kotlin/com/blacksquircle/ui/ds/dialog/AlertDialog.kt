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

package com.blacksquircle.ui.ds.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.TextButton

@Composable
fun AlertDialog(
    title: String,
    content: @Composable (BoxScope.() -> Unit),
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    verticalScroll: Boolean = true,
    horizontalPadding: Boolean = true,
    confirmButton: String? = null,
    dismissButton: String? = null,
    onConfirmClicked: () -> Unit = {},
    onDismissClicked: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
    confirmButtonEnabled: Boolean = true,
    dismissButtonEnabled: Boolean = true,
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
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = SquircleTheme.typography.text18Medium,
                    color = SquircleTheme.colors.colorTextAndIconPrimary,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 24.dp,
                            bottom = 16.dp,
                        )
                )

                val scrollState = rememberScrollState()
                val scrollableModifier = if (verticalScroll) {
                    Modifier.verticalScroll(scrollState)
                } else {
                    Modifier
                }
                val paddingModifier = if (horizontalPadding) {
                    Modifier.padding(horizontal = 24.dp)
                } else {
                    Modifier
                }
                Box(
                    content = content,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .then(scrollableModifier)
                        .then(paddingModifier)
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (dismissButton != null) {
                        TextButton(
                            text = dismissButton,
                            onClick = onDismissClicked,
                            enabled = dismissButtonEnabled
                        )
                    }
                    if (dismissButton != null && confirmButton != null) {
                        Spacer(Modifier.width(8.dp))
                    }
                    if (confirmButton != null) {
                        TextButton(
                            text = confirmButton,
                            onClick = onConfirmClicked,
                            enabled = confirmButtonEnabled
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AlertDialogPreview() {
    PreviewBackground {
        AlertDialog(
            title = "Alert Dialog",
            content = {
                Text(
                    text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
                    style = SquircleTheme.typography.text16Regular,
                    color = SquircleTheme.colors.colorTextAndIconSecondary,
                )
            },
            confirmButton = "Confirm",
            dismissButton = "Cancel",
            onDismissClicked = {},
            onDismiss = {},
        )
    }
}