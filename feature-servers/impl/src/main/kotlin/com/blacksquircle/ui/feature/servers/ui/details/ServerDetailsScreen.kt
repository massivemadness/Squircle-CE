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

package com.blacksquircle.ui.feature.servers.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.MimeType
import com.blacksquircle.ui.core.contract.rememberOpenFileContract
import com.blacksquircle.ui.core.effect.ResultEventBus
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.api.navigation.ServerDetailsRoute
import com.blacksquircle.ui.feature.servers.internal.ServersComponent
import com.blacksquircle.ui.feature.servers.ui.details.compose.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.details.compose.PasswordAction
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerAddress
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerAuthMethod
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerFolder
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerKeyFile
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerName
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerPassphrase
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerPassword
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerScheme
import com.blacksquircle.ui.feature.servers.ui.details.compose.ServerUsername
import com.blacksquircle.ui.feature.servers.ui.list.KEY_DELETE
import com.blacksquircle.ui.feature.servers.ui.list.KEY_SAVE
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerType
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ServerDetailsScreen(
    navArgs: ServerDetailsRoute,
    viewModel: ServerDetailsViewModel = daggerViewModel { context ->
        val component = ServersComponent.buildOrGet(context)
        ServerDetailsViewModel.ParameterizedFactory(navArgs.serverId).also(component::inject)
    },
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ServerDetailsScreen(
        viewState = viewState,
        onSchemeChanged = viewModel::onSchemeChanged,
        onNameChanged = viewModel::onNameChanged,
        onAddressChanged = viewModel::onAddressChanged,
        onPortChanged = viewModel::onPortChanged,
        onUsernameChanged = viewModel::onUsernameChanged,
        onAuthMethodChanged = viewModel::onAuthMethodChanged,
        onPasswordActionChanged = viewModel::onPasswordActionChanged,
        onPassphraseActionChanged = viewModel::onPassphraseActionChanged,
        onChooseFileClicked = viewModel::onChooseFileClicked,
        onPasswordChanged = viewModel::onPasswordChanged,
        onPassphraseChanged = viewModel::onPassphraseChanged,
        onInitialDirChanged = viewModel::onInitialDirChanged,
        onSaveClicked = viewModel::onSaveClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        onCancelClicked = viewModel::onCancelClicked,
    )

    val openFileContract = rememberOpenFileContract { result ->
        when (result) {
            is ContractResult.Success -> viewModel.onKeyFileSelected(result.uri)
            is ContractResult.Canceled -> Unit
        }
    }

    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ServerDetailsViewEvent.SendSaveResult -> {
                    ResultEventBus.sendResult(KEY_SAVE, Unit)
                }
                is ServerDetailsViewEvent.SendDeleteResult -> {
                    ResultEventBus.sendResult(KEY_DELETE, Unit)
                }
                is ServerDetailsViewEvent.ChooseFile -> {
                    openFileContract.launch(arrayOf(MimeType.OCTET_STREAM, MimeType.PEM))
                }
            }
        }
    }
}

@Composable
private fun ServerDetailsScreen(
    viewState: ServerDetailsViewState,
    onSchemeChanged: (String) -> Unit = {},
    onNameChanged: (String) -> Unit = {},
    onAddressChanged: (String) -> Unit = {},
    onPortChanged: (String) -> Unit = {},
    onUsernameChanged: (String) -> Unit = {},
    onAuthMethodChanged: (String) -> Unit = {},
    onPasswordActionChanged: (String) -> Unit = {},
    onPassphraseActionChanged: (String) -> Unit = {},
    onChooseFileClicked: () -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onPassphraseChanged: (String) -> Unit = {},
    onInitialDirChanged: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
) {
    AlertDialog(
        title = if (viewState.isEditMode) {
            stringResource(R.string.servers_edit_dialog_title)
        } else {
            stringResource(R.string.servers_add_dialog_title)
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

                if (viewState.scheme == ServerType.SFTP) {
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
                                keyId = viewState.keyId,
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
        confirmButton = stringResource(UiR.string.common_save),
        onConfirmClicked = onSaveClicked,
        dismissButton = if (viewState.isEditMode) {
            stringResource(UiR.string.common_delete)
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
    )
}

@PreviewLightDark
@Composable
private fun ServerDetailsScreenPreview() {
    PreviewBackground {
        ServerDetailsScreen(
            viewState = ServerDetailsViewState(),
        )
    }
}