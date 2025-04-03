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

package com.blacksquircle.ui.feature.explorer.ui.auth

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.AuthDialog
import com.blacksquircle.ui.feature.explorer.ui.explorer.ARG_USER_INPUT
import com.blacksquircle.ui.feature.explorer.ui.explorer.KEY_AUTHENTICATION
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun AuthScreen(
    navArgs: AuthDialog,
    navController: NavController,
) {
    AuthScreen(
        authMethod = navArgs.authMethod,
        onConfirmClicked = { credentials ->
            sendNavigationResult(
                key = KEY_AUTHENTICATION,
                result = bundleOf(ARG_USER_INPUT to credentials)
            )
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        },
    )
}

@Composable
private fun AuthScreen(
    authMethod: AuthMethod,
    onConfirmClicked: (String) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    var credentials by rememberSaveable {
        mutableStateOf("")
    }
    AlertDialog(
        title = stringResource(R.string.dialog_title_authentication),
        content = {
            TextField(
                inputText = credentials,
                onInputChanged = { credentials = it },
                labelText = when (authMethod) {
                    AuthMethod.PASSWORD -> stringResource(R.string.hint_enter_password)
                    AuthMethod.KEY -> stringResource(R.string.hint_enter_passphrase)
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password,
                ),
                visualTransformation = PasswordVisualTransformation(),
            )
        },
        confirmButton = stringResource(UiR.string.common_continue),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = { onConfirmClicked(credentials) },
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun AuthScreenPreview() {
    PreviewBackground {
        AuthScreen(
            authMethod = AuthMethod.PASSWORD,
        )
    }
}