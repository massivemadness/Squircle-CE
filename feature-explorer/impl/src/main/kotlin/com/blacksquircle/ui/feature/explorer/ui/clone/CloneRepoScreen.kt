/*
 * Copyright Squircle CE contributors.
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.core.effect.ResultEventBus
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.isValidUrl
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.auth.ServerAuthViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.KEY_CLONE_REPO
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun CloneRepoScreen(
    viewModel: CloneRepoViewModel = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        CloneRepoViewModel.Factory().also(component::inject)
    }
) {
    CloneRepoScreen(
        onConfirmClicked = { url ->
            ResultEventBus.sendResult(KEY_CLONE_REPO, url)
            viewModel.onBackClicked()
        },
        onCancelClicked = viewModel::onBackClicked,
    )
}

@Composable
private fun CloneRepoScreen(
    onConfirmClicked: (String) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    var url by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        title = stringResource(R.string.explorer_clone_dialog_title),
        content = {
            TextField(
                inputText = url,
                onInputChanged = { value ->
                    url = value
                    isError = false
                },
                labelText = stringResource(R.string.explorer_clone_dialog_input_label),
                errorText = stringResource(R.string.explorer_clone_dialog_input_error),
                placeholderText = stringResource(UiR.string.common_https),
                error = isError,
            )
        },
        confirmButton = stringResource(R.string.explorer_clone_dialog_button_clone),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = {
            if (url.isValidUrl()) {
                onConfirmClicked(url)
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