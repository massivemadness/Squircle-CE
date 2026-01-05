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

package com.blacksquircle.ui.application.update

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.R
import com.blacksquircle.ui.core.effect.ResultEventBus
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.internal.AppComponent

internal const val KEY_INSTALL_UPDATE = "KEY_UPDATE"

@Composable
internal fun UpdateScreen(
    viewModel: UpdateViewModel = daggerViewModel { context ->
        val component = AppComponent.buildOrGet(context)
        UpdateViewModel.Factory().also(component::inject)
    }
) {
    UpdateScreen(
        onConfirmClicked = {
            ResultEventBus.sendResult(KEY_INSTALL_UPDATE, Unit)
            viewModel.onCloseClicked()
        },
        onCancelClicked = viewModel::onCloseClicked,
    )
}

@Composable
private fun UpdateScreen(
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
) {
    AlertDialog(
        title = stringResource(R.string.app_update_dialog_title),
        content = {
            Text(
                text = stringResource(R.string.app_update_dialog_message),
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
            )
        },
        confirmButton = stringResource(R.string.app_update_dialog_button_install),
        dismissButton = stringResource(R.string.app_update_dialog_button_later),
        onConfirmClicked = onConfirmClicked,
        onDismissClicked = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun UpdateScreenPreview() {
    PreviewBackground {
        UpdateScreen()
    }
}