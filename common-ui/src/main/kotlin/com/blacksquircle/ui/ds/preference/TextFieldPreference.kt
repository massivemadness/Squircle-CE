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

package com.blacksquircle.ui.ds.preference

import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.textfield.TextFieldStyleDefaults

@Composable
fun TextFieldPreference(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
    confirmButton: String? = null,
    dismissButton: String? = null,
    labelText: String? = null,
    helpText: String? = null,
    inputTextStyle: TextStyle = LocalTextStyle.current,
    inputValue: String = "",
    onConfirmClicked: (String) -> Unit = {},
    onDismissClicked: () -> Unit = {},
    dialogShown: Boolean = false,
    dialogTitle: String? = null,
) {
    var showDialog by rememberSaveable { mutableStateOf(dialogShown) }
    Preference(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        onClick = { showDialog = true },
        modifier = modifier,
    )
    if (showDialog) {
        val text = rememberSaveable { mutableStateOf(inputValue) }
        AlertDialog(
            title = dialogTitle ?: title,
            content = {
                TextField(
                    inputText = text.value,
                    onInputChanged = { text.value = it },
                    labelText = labelText,
                    helpText = helpText,
                    textFieldStyle = TextFieldStyleDefaults.Default.copy(
                        textStyle = inputTextStyle,
                    ),
                )
            },
            confirmButton = confirmButton,
            onConfirmClicked = {
                showDialog = false
                onConfirmClicked(text.value)
            },
            dismissButton = dismissButton,
            onDismissClicked = {
                showDialog = false
                onDismissClicked()
            },
            onDismiss = { showDialog = false },
        )
    }
}

@PreviewLightDark
@Composable
private fun TextFieldPreferencePreview() {
    PreviewBackground {
        TextFieldPreference(
            title = "Title",
            subtitle = "Subtitle",
            inputValue = "Hello World!",
            labelText = "Label Text",
            helpText = "Help Text",
            confirmButton = "Save",
            dismissButton = "Cancel",
            dialogShown = true,
        )
    }
}