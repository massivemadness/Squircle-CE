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
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.feature.servers.domain.repository.ServersRepository
import com.blacksquircle.ui.feature.servers.ui.fragment.CloudViewState
import com.blacksquircle.ui.feature.servers.ui.navigation.ServersScreen
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
internal class CloudViewModel @Inject constructor(
    private val serversRepository: ServersRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(CloudViewState())
    val viewState: StateFlow<CloudViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadServers()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack())
        }
    }

    fun onServerClicked(serverConfig: ServerConfig) {
        viewModelScope.launch {
            val screen = ServersScreen.EditServer(serverConfig.uuid)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onAddServerClicked() {
        viewModelScope.launch {
            val screen = Screen.AddServer
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun loadServers() {
        viewModelScope.launch {
            try {
                val servers = serversRepository.loadServers()
                _viewState.value = CloudViewState(servers)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }
}