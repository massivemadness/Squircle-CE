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

package com.blacksquircle.ui.feature.servers.data.interactor

import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig

internal class ServerInteractorImpl(
    private val serverRepository: ServerRepository,
) : ServerInteractor {

    override suspend fun authenticate(uuid: String, credentials: String) {
        ServerCredentials.put(uuid, credentials)
    }

    override suspend fun loadServers(): List<ServerConfig> {
        return serverRepository.loadServers()
    }

    override suspend fun loadServer(uuid: String): ServerConfig {
        val serverConfig = serverRepository.loadServer(uuid)
        val authorizedConfig = when (serverConfig.authMethod) {
            AuthMethod.PASSWORD -> serverConfig.copy(
                password = ServerCredentials.get(uuid) ?: serverConfig.password
            )
            AuthMethod.KEY -> serverConfig.copy(
                passphrase = ServerCredentials.get(uuid) ?: serverConfig.passphrase
            )
        }
        return authorizedConfig
    }
}