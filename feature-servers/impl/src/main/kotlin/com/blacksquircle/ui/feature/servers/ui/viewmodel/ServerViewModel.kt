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

package com.blacksquircle.ui.feature.servers.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.servers.domain.ServersRepository
import com.blacksquircle.ui.feature.servers.ui.dialog.ServerViewState
import com.blacksquircle.ui.feature.servers.ui.dialog.ServerViewState.Companion.DEFAULT_FTP_PORT
import com.blacksquircle.ui.feature.servers.ui.dialog.ServerViewState.Companion.DEFAULT_SFTP_PORT
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PasswordAction
import com.blacksquircle.ui.feature.servers.ui.navigation.ServerViewEvent
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileServer
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

internal class ServerViewModel @AssistedInject constructor(
    private val serversRepository: ServersRepository,
    @Assisted private val serverId: String?,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ServerViewState(isEditMode = isEditMode))
    val viewState: StateFlow<ServerViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val isEditMode: Boolean
        get() = !serverId.isNullOrEmpty()

    init {
        loadServer()
    }

    fun onSchemeChanged(scheme: String) {
        val fileServer = FileServer.of(scheme)
        _viewState.update {
            it.copy(
                scheme = fileServer,
                authMethod = if (fileServer != FileServer.SFTP) {
                    AuthMethod.PASSWORD
                } else {
                    it.authMethod
                }
            )
        }
    }

    fun onNameChanged(name: String) {
        _viewState.update {
            it.copy(
                name = name,
                invalidName = false,
            )
        }
    }

    fun onAddressChanged(address: String) {
        _viewState.update {
            it.copy(
                address = address,
                invalidAddress = false,
            )
        }
    }

    fun onPortChanged(port: String) {
        _viewState.update {
            it.copy(port = port)
        }
    }

    fun onUsernameChanged(username: String) {
        _viewState.update {
            it.copy(username = username)
        }
    }

    fun onAuthMethodChanged(authMethod: String) {
        _viewState.update {
            it.copy(authMethod = AuthMethod.of(authMethod))
        }
    }

    fun onKeyFileChanged(filePath: String) {
        _viewState.update {
            it.copy(privateKey = filePath)
        }
    }

    fun onKeyFileSelected(filePath: String) {
        _viewState.update {
            it.copy(privateKey = filePath)
        }
    }

    fun onChooseFileClicked() {
        viewModelScope.launch {
            _viewEvent.send(ServerViewEvent.ChooseFile)
        }
    }

    fun onPasswordActionChanged(passwordAction: String) {
        _viewState.update {
            it.copy(passwordAction = PasswordAction.of(passwordAction))
        }
    }

    fun onPassphraseActionChanged(passphraseAction: String) {
        _viewState.update {
            it.copy(passphraseAction = PassphraseAction.of(passphraseAction))
        }
    }

    fun onPasswordChanged(password: String) {
        _viewState.update {
            it.copy(password = password)
        }
    }

    fun onPassphraseChanged(passphrase: String) {
        _viewState.update {
            it.copy(passphrase = passphrase)
        }
    }

    fun onInitialDirChanged(initialDir: String) {
        _viewState.update {
            it.copy(initialDir = initialDir)
        }
    }

    fun onSaveClicked() {
        val isServerNameValid = viewState.value.name.isNotBlank()
        val isServerAddressValid = viewState.value.address.isNotBlank()
        _viewState.update {
            it.copy(
                invalidName = !isServerNameValid,
                invalidAddress = !isServerAddressValid,
            )
        }
        if (isServerNameValid && isServerAddressValid) {
            viewModelScope.launch {
                try {
                    val serverConfig = viewState.value.toServerConfig()
                    serversRepository.upsertServer(serverConfig)
                    _viewEvent.send(ServerViewEvent.SendSaveResult)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Timber.e(e, e.message)
                    _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
                }
            }
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            try {
                val serverConfig = viewState.value.toServerConfig()
                serversRepository.deleteServer(serverConfig)
                _viewEvent.send(ServerViewEvent.SendDeleteResult)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack())
        }
    }

    private fun loadServer() {
        if (!isEditMode) {
            return
        }
        viewModelScope.launch {
            try {
                val serverConfig = serversRepository.loadServer(serverId.orEmpty())
                _viewState.update {
                    it.copy(
                        scheme = serverConfig.scheme,
                        name = serverConfig.name,
                        address = serverConfig.address,
                        port = serverConfig.port.toString(),
                        initialDir = serverConfig.initialDir,
                        passwordAction = if (serverConfig.password.isNullOrEmpty()) {
                            PasswordAction.ASK_FOR_PASSWORD
                        } else {
                            PasswordAction.SAVE_PASSWORD
                        },
                        passphraseAction = if (serverConfig.passphrase.isNullOrEmpty()) {
                            PassphraseAction.ASK_FOR_PASSPHRASE
                        } else {
                            PassphraseAction.SAVE_PASSPHRASE
                        },
                        authMethod = serverConfig.authMethod,
                        username = serverConfig.username,
                        password = serverConfig.password.orEmpty(),
                        privateKey = serverConfig.privateKey.orEmpty(),
                        passphrase = serverConfig.passphrase.orEmpty(),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun ServerViewState.toServerConfig(): ServerConfig {
        return ServerConfig(
            uuid = serverId ?: UUID.randomUUID().toString(),
            scheme = scheme,
            name = name,
            address = address,
            port = port.toIntOrNull() ?: when (scheme) {
                FileServer.FTP,
                FileServer.FTPS,
                FileServer.FTPES -> DEFAULT_FTP_PORT

                FileServer.SFTP -> DEFAULT_SFTP_PORT
            },
            initialDir = initialDir,
            authMethod = authMethod,
            username = username,
            password = if (
                authMethod == AuthMethod.PASSWORD &&
                passwordAction == PasswordAction.SAVE_PASSWORD
            ) {
                password
            } else {
                null
            },
            privateKey = if (authMethod == AuthMethod.KEY) privateKey else null,
            passphrase = if (
                authMethod == AuthMethod.KEY &&
                passphraseAction == PassphraseAction.SAVE_PASSPHRASE
            ) {
                passphrase
            } else {
                null
            },
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted serverId: String?): ServerViewModel
    }
}