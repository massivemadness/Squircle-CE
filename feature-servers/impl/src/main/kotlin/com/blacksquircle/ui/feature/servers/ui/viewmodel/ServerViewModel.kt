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

package com.blacksquircle.ui.feature.servers.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PasswordAction
import com.blacksquircle.ui.feature.servers.ui.dialog.ServerState
import com.blacksquircle.ui.feature.servers.ui.navigation.ServerViewEvent
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.FileServer
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel(assistedFactory = ServerViewModel.Factory::class)
internal class ServerViewModel @AssistedInject constructor(
    @Assisted val serverData: String?,
) : ViewModel() {

    private val _viewState = MutableStateFlow(initialViewState())
    val viewState: StateFlow<ServerState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val isEditMode: Boolean
        get() = !serverData.isNullOrEmpty()

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

    fun onKeyFileChanged(privateKey: String) {
        _viewState.update {
            it.copy(privateKey = privateKey)
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
        viewModelScope.launch {
            val isServerNameValid = viewState.value.name.isNotBlank()
            val isServerAddressValid = viewState.value.address.isNotBlank()
            _viewState.update {
                it.copy(
                    invalidName = !isServerNameValid,
                    invalidAddress = !isServerAddressValid,
                )
            }
            if (isServerNameValid && isServerAddressValid) {
                val serverConfig = viewState.value.toServerConfig()
                _viewEvent.send(ServerViewEvent.SendSaveResult(serverConfig))
            }
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            val serverConfig = viewState.value.toServerConfig()
            _viewEvent.send(ServerViewEvent.SendDeleteResult(serverConfig))
        }
    }

    private fun initialViewState(): ServerState {
        return if (isEditMode) {
            val serverConfig = Gson().fromJson(serverData, ServerConfig::class.java)
            ServerState(
                isEditMode = true,
                uuid = serverConfig.uuid,
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
        } else {
            ServerState(
                isEditMode = false,
                uuid = UUID.randomUUID().toString(),
                scheme = FileServer.FTP,
                name = "",
                address = "",
                port = "",
                initialDir = "",
                authMethod = AuthMethod.PASSWORD,
                username = "",
                password = "",
                privateKey = "",
                passphrase = "",
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted serverData: String?): ServerViewModel
    }
}