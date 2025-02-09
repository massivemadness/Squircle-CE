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

package com.blacksquircle.ui.feature.explorer.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun CompressScreen(
    onConfirmClicked: (String) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    val placeholder = stringResource(UiR.string.common_untitled)
    var fileName by rememberSaveable {
        mutableStateOf("")
    }
    AlertDialog(
        title = stringResource(R.string.dialog_title_archive_name),
        content = {
            TextField(
                inputText = fileName,
                onInputChanged = { fileName = it },
                labelText = stringResource(R.string.hint_enter_archive_name),
                placeholderText = placeholder,
            )
        },
        confirmButton = stringResource(R.string.action_compress),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = { onConfirmClicked(fileName.ifEmpty { placeholder }) },
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun CompressScreenPreview() {
    PreviewBackground {
        CompressScreen()
    }
}