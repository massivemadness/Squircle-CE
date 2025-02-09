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

package com.blacksquircle.ui.feature.servers.data.repository

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.database.AppDatabase
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import com.blacksquircle.ui.feature.servers.data.mapper.ServerMapper
import com.blacksquircle.ui.feature.servers.domain.ServersRepository
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ServersRepositoryImpl(
    private val settingsManager: SettingsManager,
    private val dispatcherProvider: DispatcherProvider,
    private val appDatabase: AppDatabase,
) : ServersRepository {

    override val serverFlow = appDatabase.serverDao().flow()
        .map { it.map(ServerMapper::toModel) }
        .flowOn(dispatcherProvider.io())

    override suspend fun authenticate(uuid: String, credentials: String) {
        withContext(dispatcherProvider.io()) {
            ServerCredentials.put(uuid, credentials)
        }
    }

    override suspend fun loadServers(): List<ServerConfig> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.serverDao().loadAll()
                .map(ServerMapper::toModel)
        }
    }

    override suspend fun loadServer(uuid: String): ServerConfig {
        return withContext(dispatcherProvider.io()) {
            val serverEntity = appDatabase.serverDao().load(uuid)
            val serverConfig = ServerMapper.toModel(serverEntity)
            when (serverConfig.authMethod) {
                AuthMethod.PASSWORD -> serverConfig.copy(
                    password = ServerCredentials.get(uuid)
                        ?: serverConfig.password
                )
                AuthMethod.KEY -> serverConfig.copy(
                    passphrase = ServerCredentials.get(uuid)
                        ?: serverConfig.passphrase
                )
            }
        }
    }

    override suspend fun upsertServer(serverConfig: ServerConfig) {
        withContext(dispatcherProvider.io()) {
            ServerCredentials.remove(serverConfig.uuid)
            val entity = ServerMapper.toEntity(serverConfig)
            appDatabase.serverDao().insert(entity)
            if (settingsManager.filesystem == serverConfig.uuid) {
                settingsManager.remove(SettingsManager.KEY_FILESYSTEM)
            }
        }
    }

    override suspend fun deleteServer(serverConfig: ServerConfig) {
        withContext(dispatcherProvider.io()) {
            ServerCredentials.remove(serverConfig.uuid)
            appDatabase.serverDao().delete(serverConfig.uuid)
            if (settingsManager.filesystem == serverConfig.uuid) {
                settingsManager.remove(SettingsManager.KEY_FILESYSTEM)
            }
        }
    }
}