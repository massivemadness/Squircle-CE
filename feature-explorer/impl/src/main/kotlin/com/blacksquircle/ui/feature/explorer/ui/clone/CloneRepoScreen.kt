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

package com.blacksquircle.ui.feature.explorer.ui.clone

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.isValidUrl
import com.blacksquircle.ui.feature.explorer.ui.explorer.ARG_SUBMODULES
import com.blacksquircle.ui.feature.explorer.ui.explorer.ARG_USER_INPUT
import com.blacksquircle.ui.feature.explorer.ui.explorer.KEY_CLONE_REPO
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun CloneRepoScreen(navController: NavController) {
    CloneRepoScreen(
        onConfirmClicked = { submodules, url ->
            sendNavigationResult(
                key = KEY_CLONE_REPO,
                result = bundleOf(
                    ARG_USER_INPUT to url,
                    ARG_SUBMODULES to submodules,
                )
            )
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun CloneRepoScreen(
    onConfirmClicked: (Boolean, String) -> Unit = { _, _ -> },
    onCancelClicked: () -> Unit = {}
) {
    var url by rememberSaveable { mutableStateOf("") }
    var submodules by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        title = stringResource(R.string.dialog_title_clone),
        content = {
            Column {
                TextField(
                    inputText = url,
                    onInputChanged = { value ->
                        url = value
                        isError = false
                    },
                    labelText = stringResource(R.string.hint_enter_git_url),
                    errorText = stringResource(R.string.message_invalid_url),
                    placeholderText = stringResource(UiR.string.common_https),
                    error = isError,
                )
                Spacer(Modifier.height(8.dp))
                CheckBox(
                    title = stringResource(R.string.action_submodules),
                    checked = submodules,
                    onClick = { submodules = !submodules },
                )
            }
        },
        confirmButton = stringResource(R.string.action_clone),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = {
            if (url.isValidUrl()) {
                onConfirmClicked(submodules, url)
            } else {
                isError = true
            }
        },
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun CloneRepoScreenPreview() {
    PreviewBackground {
        CloneRepoScreen()
    }
}