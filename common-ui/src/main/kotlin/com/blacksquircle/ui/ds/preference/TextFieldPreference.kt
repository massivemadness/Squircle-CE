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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.DsTextField

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
                Column(Modifier.padding(horizontal = 16.dp)) {
                    if (topHelperText != null) {
                        Text(
                            text = topHelperText,
                            style = SquircleTheme.typography.text12Regular,
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                    }
                    DsTextField(
                        value = text.value,
                        onValueChanged = { text.value = it },
                        textStyle = inputTextStyle,
                        singleLine = true,
                    )
                    if (bottomHelperText != null) {
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = bottomHelperText,
                            style = SquircleTheme.typography.text12Regular,
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                        )
                    }
                }
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