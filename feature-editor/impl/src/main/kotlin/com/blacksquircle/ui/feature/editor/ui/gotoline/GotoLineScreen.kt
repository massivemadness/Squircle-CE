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

package com.blacksquircle.ui.feature.editor.ui.gotoline

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.api.navigation.GoToLineDialog
import com.blacksquircle.ui.feature.editor.ui.editor.ARG_LINE_NUMBER
import com.blacksquircle.ui.feature.editor.ui.editor.KEY_GOTO_LINE

private const val DEFAULT_LINE = 0

@Composable
internal fun GoToLineScreen(
    navArgs: GoToLineDialog,
    navController: NavController
) {
    GotoLineScreen(
        lineCount = navArgs.lineCount,
        onConfirmClicked = { lineNumber ->
            sendNavigationResult(
                key = KEY_GOTO_LINE,
                result = bundleOf(ARG_LINE_NUMBER to lineNumber)
            )
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun GotoLineScreen(
    lineCount: Int,
    onConfirmClicked: (Int) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    var lineNumber by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        title = stringResource(R.string.editor_goto_line_dialog_title),
        content = {
            TextField(
                inputText = lineNumber,
                onInputChanged = { lineNumber = it },
                labelText = stringResource(R.string.editor_goto_line_dialog_input_label),
                placeholderText = "$DEFAULT_LINE..$lineCount",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                modifier = Modifier.focusRequester(focusRequester)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        },
        confirmButton = stringResource(R.string.editor_goto_line_dialog_button_go_to),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = {
            val intValue = lineNumber.toIntOrNull() ?: 0
            onConfirmClicked(intValue - 1)
        },
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun GotoLineScreenPreview() {
    PreviewBackground {
        GotoLineScreen(lineCount = 1000)
    }
}