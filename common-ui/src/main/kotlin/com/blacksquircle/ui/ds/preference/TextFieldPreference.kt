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

package com.blacksquircle.ui.ds.preference

import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField

@Composable
fun TextFieldPreference(
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    confirmButton: String? = null,
    dismissButton: String? = null,
    topHelperText: String? = null,
    bottomHelperText: String? = null,
    inputTextStyle: TextStyle = LocalTextStyle.current,
    inputValue: String = "",
    onInputConfirmed: (String) -> Unit = {},
) {
    var dialogShown by rememberSaveable { mutableStateOf(false) }
    Preference(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        onClick = { dialogShown = true },
    )
    if (dialogShown) {
        val text = rememberSaveable { mutableStateOf(inputValue) }
        AlertDialog(
            title = title,
            content = {
                TextField(
                    inputText = text.value,
                    onInputChanged = { text.value = it },
                    topHelperText = topHelperText,
                    bottomHelperText = bottomHelperText,
                    textStyle = inputTextStyle,
                    singleLine = true,
                )
            },
            confirmButton = confirmButton,
            onConfirmClicked = {
                dialogShown = false
                onInputConfirmed(text.value)
            },
            dismissButton = dismissButton,
            onDismissClicked = {
                dialogShown = false
            },
            onDismiss = { dialogShown = false },
        )
    }
}