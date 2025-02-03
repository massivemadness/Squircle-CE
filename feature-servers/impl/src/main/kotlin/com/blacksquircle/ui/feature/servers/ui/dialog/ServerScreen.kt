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

package com.blacksquircle.ui.feature.servers.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PasswordAction
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerAddress
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerAuthMethod
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerFolder
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerKeyFile
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerName
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerPassphrase
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerPassword
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerScheme
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.ServerUsername
import com.blacksquircle.ui.feature.servers.ui.viewmodel.ServerViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileServer

@Composable
internal fun ServerScreen(
    viewModel: ServerViewModel,
    navController: NavController,
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ServerScreen(
        viewState = viewState,
        onSchemeChanged = viewModel::onSchemeChanged,
        onNameChanged = viewModel::onNameChanged,
        onAddressChanged = viewModel::onAddressChanged,
        onPortChanged = viewModel::onPortChanged,
        onUsernameChanged = viewModel::onUsernameChanged,
        onAuthMethodChanged = viewModel::onAuthMethodChanged,
        onPasswordActionChanged = viewModel::onPasswordActionChanged,
        onPassphraseActionChanged = viewModel::onPassphraseActionChanged,
        onKeyFileChanged = viewModel::onKeyFileChanged,
        onChooseFileClicked = viewModel::onChooseFileClicked,
        onPasswordChanged = viewModel::onPasswordChanged,
        onPassphraseChanged = viewModel::onPassphraseChanged,
        onInitialDirChanged = viewModel::onInitialDirChanged,
        onSaveClicked = viewModel::onSaveClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        onCancelClicked = navController::popBackStack,
    )
}

@Composable
private fun ServerScreen(
    viewState: ServerViewState,
    onSchemeChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onPortChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onAuthMethodChanged: (String) -> Unit,
    onPasswordActionChanged: (String) -> Unit,
    onPassphraseActionChanged: (String) -> Unit,
    onKeyFileChanged: (String) -> Unit,
    onChooseFileClicked: () -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPassphraseChanged: (String) -> Unit,
    onInitialDirChanged: (String) -> Unit,
    onSaveClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
) {
    AlertDialog(
        title = if (viewState.isEditMode) {
            stringResource(R.string.pref_edit_server_title)
        } else {
            stringResource(R.string.pref_add_server_title)
        },
        content = {
            Column {
                ServerScheme(
                    scheme = viewState.scheme.value,
                    onSchemeChanged = onSchemeChanged,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(8.dp))

                ServerName(
                    name = viewState.name,
                    onNameChanged = onNameChanged,
                    isError = viewState.invalidName,
                )

                Spacer(Modifier.height(8.dp))

                ServerAddress(
                    address = viewState.address,
                    onAddressChanged = onAddressChanged,
                    port = viewState.port,
                    onPortChanged = onPortChanged,
                    scheme = viewState.scheme,
                    isError = viewState.invalidAddress,
                )

                Spacer(Modifier.height(8.dp))

                ServerUsername(
                    username = viewState.username,
                    onUsernameChanged = onUsernameChanged,
                )

                Spacer(Modifier.height(8.dp))

                if (viewState.scheme == FileServer.SFTP) {
                    ServerAuthMethod(
                        authMethod = viewState.authMethod,
                        onAuthMethodChanged = onAuthMethodChanged,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(8.dp))

                    when (viewState.authMethod) {
                        AuthMethod.PASSWORD -> {
                            PasswordAction(
                                passwordAction = viewState.passwordAction,
                                onPasswordActionChanged = onPasswordActionChanged,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        AuthMethod.KEY -> {
                            ServerKeyFile(
                                keyFile = viewState.privateKey,
                                onKeyFileChanged = onKeyFileChanged,
                                onChooseFileClicked = onChooseFileClicked,
                            )

                            Spacer(Modifier.height(8.dp))

                            PassphraseAction(
                                passphraseAction = viewState.passphraseAction,
                                onPassphraseActionChanged = onPassphraseActionChanged,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                } else {
                    PasswordAction(
                        passwordAction = viewState.passwordAction,
                        onPasswordActionChanged = onPasswordActionChanged,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(8.dp))

                if (viewState.authMethod == AuthMethod.PASSWORD &&
                    viewState.passwordAction == PasswordAction.SAVE_PASSWORD
                ) {
                    ServerPassword(
                        password = viewState.password,
                        onPasswordChanged = onPasswordChanged,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (viewState.authMethod == AuthMethod.KEY &&
                    viewState.passphraseAction == PassphraseAction.SAVE_PASSPHRASE
                ) {
                    ServerPassphrase(
                        passphrase = viewState.passphrase,
                        onPassphraseChanged = onPassphraseChanged,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                ServerFolder(
                    initialDir = viewState.initialDir,
                    onInitialDirChanged = onInitialDirChanged,
                )
            }
        },
        confirmButton = stringResource(com.blacksquircle.ui.ds.R.string.common_save),
        onConfirmClicked = onSaveClicked,
        dismissButton = if (viewState.isEditMode) {
            stringResource(com.blacksquircle.ui.ds.R.string.common_delete)
        } else {
            stringResource(android.R.string.cancel)
        },
        onDismissClicked = {
            if (viewState.isEditMode) {
                onDeleteClicked()
            } else {
                onCancelClicked()
            }
        },
        onDismiss = onCancelClicked
    )
}

@Preview
@Composable
private fun ServerScreenPreview() {
    SquircleTheme {
        ServerScreen(
            viewState = ServerViewState(),
            onSchemeChanged = {},
            onNameChanged = {},
            onAddressChanged = {},
            onPortChanged = {},
            onUsernameChanged = {},
            onAuthMethodChanged = {},
            onPasswordActionChanged = {},
            onPassphraseActionChanged = {},
            onKeyFileChanged = {},
            onChooseFileClicked = {},
            onPasswordChanged = {},
            onPassphraseChanged = {},
            onInitialDirChanged = {},
        )
    }
}