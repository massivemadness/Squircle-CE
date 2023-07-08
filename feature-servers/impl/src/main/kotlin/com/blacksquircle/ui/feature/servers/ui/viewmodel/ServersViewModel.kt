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
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.servers.domain.repository.ServersRepository
import com.blacksquircle.ui.feature.servers.ui.mvi.ServerIntent
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ServersViewModel @Inject constructor(
    private val serversRepository: ServersRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _servers = MutableStateFlow<List<ServerConfig>>(emptyList())
    val servers: StateFlow<List<ServerConfig>> = _servers.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadServers()
    }

    fun obtainEvent(event: ServerIntent) {
        when (event) {
            is ServerIntent.LoadServers -> loadServers()
            is ServerIntent.UpsertServer -> upsertServer(event)
            is ServerIntent.DeleteServer -> deleteServer(event)
        }
    }

    private fun loadServers() {
        viewModelScope.launch {
            _servers.value = serversRepository.loadServers()
        }
    }

    private fun upsertServer(event: ServerIntent.UpsertServer) {
        viewModelScope.launch {
            try {
                val serverConfig = event.serverConfig
                serversRepository.upsertServer(serverConfig)
                if (settingsManager.filesystem == serverConfig.uuid) {
                    settingsManager.remove(SettingsManager.KEY_FILESYSTEM)
                }
                loadServers()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun deleteServer(event: ServerIntent.DeleteServer) {
        viewModelScope.launch {
            try {
                val serverConfig = event.serverConfig
                serversRepository.deleteServer(serverConfig)
                if (settingsManager.filesystem == serverConfig.uuid) {
                    settingsManager.remove(SettingsManager.KEY_FILESYSTEM)
                }
                loadServers()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }
}