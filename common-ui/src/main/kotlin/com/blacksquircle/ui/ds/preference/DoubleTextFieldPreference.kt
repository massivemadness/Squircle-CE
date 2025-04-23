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

import androidx.compose.foundation.layout.Column
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

@Composable
fun DoubleTextFieldPreference(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
    confirmButton: String? = null,
    dismissButton: String? = null,
    labelText1: String? = null,
    labelText2: String? = null,
    helpText1: String? = null,
    helpText2: String? = null,
    inputTextStyle: TextStyle = LocalTextStyle.current,
    inputValue1: String = "",
    inputValue2: String = "",
    requireBothFields: Boolean = false,
    onConfirmClicked: (String, String) -> Unit = { _, _ -> },
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
        var text1 by rememberSaveable { mutableStateOf(inputValue1) }
        var text2 by rememberSaveable { mutableStateOf(inputValue2) }
        val confirmEnabled = !requireBothFields || (text1.isNotBlank() && text2.isNotBlank())
        AlertDialog(
            title = dialogTitle ?: title,
            content = {
                Column {
                    TextField(
                        inputText = text1,
                        onInputChanged = { text1 = it },
                        labelText = labelText1,
                        helpText = helpText1,
                        textStyle = inputTextStyle,
                    )
                    TextField(
                        inputText = text2,
                        onInputChanged = { text2 = it },
                        labelText = labelText2,
                        helpText = helpText2,
                        textStyle = inputTextStyle,
                    )
                }
            },
            confirmButton = confirmButton,
            onConfirmClicked = {
                showDialog = false
                onConfirmClicked(text1, text2)
            },
            confirmEnabled = confirmEnabled,
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
private fun DoubleTextFieldPreferencePreview() {
    PreviewBackground {
        DoubleTextFieldPreference(
            title = "Title",
            subtitle = "Subtitle",
            inputValue1 = "First value",
            inputValue2 = "Second value",
            labelText1 = "Label Text 1",
            labelText2 = "Label Text 2",
            helpText1 = "Help Text for first field",
            helpText2 = "Help Text for second field",
            confirmButton = "Save",
            dismissButton = "Cancel",
            dialogShown = true,
            requireBothFields = true,
        )
    }
}