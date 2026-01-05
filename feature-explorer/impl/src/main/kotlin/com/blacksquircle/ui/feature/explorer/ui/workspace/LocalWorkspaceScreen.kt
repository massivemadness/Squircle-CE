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

package com.blacksquircle.ui.feature.explorer.ui.workspace

import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent

@Composable
internal fun LocalWorkspaceScreen(
    viewModel: LocalWorkspaceViewModel = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        LocalWorkspaceViewModel.Factory().also(component::inject)
    }
) {
    LocalWorkspaceScreen(
        onConfirmClicked = viewModel::onConfirmClicked,
        onCancelClicked = viewModel::onBackClicked,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
            }
        }
    }
}

@Composable
private fun LocalWorkspaceScreen(
    onConfirmClicked: (String) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    var filePath by rememberSaveable {
        val externalStorage = Environment.getExternalStorageDirectory()
        mutableStateOf(externalStorage.absolutePath)
    }

    AlertDialog(
        title = stringResource(R.string.explorer_workspace_local_storage_title),
        content = {
            TextField(
                inputText = filePath,
                onInputChanged = { filePath = it },
                labelText = stringResource(R.string.explorer_workspace_local_dialog_input_label),
            )
        },
        confirmButton = stringResource(R.string.explorer_workspace_button_add),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = { onConfirmClicked(filePath) },
        onDismissClicked = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun LocalWorkspaceScreenPreview() {
    PreviewBackground {
        LocalWorkspaceScreen()
    }
}