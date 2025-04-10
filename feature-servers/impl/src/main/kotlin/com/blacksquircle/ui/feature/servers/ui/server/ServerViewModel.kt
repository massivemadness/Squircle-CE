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

package com.blacksquircle.ui.feature.servers.ui.server

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import com.blacksquircle.ui.feature.servers.ui.server.compose.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.server.compose.PasswordAction
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerType
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
import javax.inject.Inject

internal class ServerViewModel @AssistedInject constructor(
    private val serverRepository: ServerRepository,
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
        val serverType = ServerType.of(scheme)
        _viewState.update {
            it.copy(
                scheme = serverType,
                authMethod = if (serverType != ServerType.SFTP) {
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

    fun onKeyFileSelected(fileUri: Uri) {
        viewModelScope.launch {
            try {
                val keyId = serverRepository.saveKeyFile(fileUri)
                _viewState.update {
                    it.copy(keyId = keyId)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
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
                    val serverConfig = viewState.value.toConfig(serverId)
                    serverRepository.upsertServer(serverConfig)
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
                val serverConfig = viewState.value.toConfig(serverId)
                serverRepository.deleteServer(serverConfig)
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
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    private fun loadServer() {
        if (!isEditMode) {
            return
        }
        viewModelScope.launch {
            try {
                val serverConfig = serverRepository.loadServer(serverId.orEmpty())
                _viewState.value = ServerViewState.create(serverConfig)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    class ParameterizedFactory(private val serverId: String?) : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelFactory: Factory

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelFactory.create(serverId) as T
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted serverId: String?): ServerViewModel
    }
}