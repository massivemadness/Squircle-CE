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

package com.blacksquircle.ui.feature.servers.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.servers.api.navigation.ServerDetailsRoute
import com.blacksquircle.ui.feature.servers.domain.model.ServerStatus
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import com.blacksquircle.ui.feature.servers.ui.list.model.ServerModel
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.navigation.api.Navigator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.cancellation.CancellationException

internal class ServerListViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ServerListViewState())
    val viewState: StateFlow<ServerListViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadServers()
    }

    fun onBackClicked() {
        navigator.goBack()
    }

    fun onServerClicked(serverConfig: ServerConfig) {
        val screen = ServerDetailsRoute(serverId = serverConfig.uuid)
        navigator.navigate(screen)
    }

    fun onCreateClicked() {
        val screen = ServerDetailsRoute(serverId = null)
        navigator.navigate(screen)
    }

    fun loadServers() {
        viewModelScope.launch {
            try {
                val servers = serverRepository.loadServers().map { config ->
                    ServerModel(
                        config = config,
                        status = ServerStatus.Checking,
                    )
                }
                _viewState.value = ServerListViewState(servers)

                servers.forEach { server ->
                    checkAvailability(server.config)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun checkAvailability(serverConfig: ServerConfig) {
        viewModelScope.launch {
            try {
                val latency = serverRepository.checkAvailability(serverConfig)
                _viewState.update { state ->
                    state.copy(
                        servers = state.servers.map { server ->
                            if (server.config.uuid == serverConfig.uuid) {
                                server.copy(status = ServerStatus.Available(latency))
                            } else {
                                server
                            }
                        }
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewState.update { state ->
                    state.copy(
                        servers = state.servers.map { server ->
                            if (server.config.uuid == serverConfig.uuid) {
                                server.copy(status = ServerStatus.Unavailable(e.message.orEmpty()))
                            } else {
                                server
                            }
                        }
                    )
                }
            }
        }
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<ServerListViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}