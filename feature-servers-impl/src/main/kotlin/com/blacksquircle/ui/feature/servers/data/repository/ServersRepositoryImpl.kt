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

package com.blacksquircle.ui.feature.servers.data.repository

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.database.AppDatabase
import com.blacksquircle.ui.feature.servers.data.converter.ServerConverter
import com.blacksquircle.ui.feature.servers.domain.repository.ServersRepository
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ServersRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val appDatabase: AppDatabase,
) : ServersRepository {

    override val serverFlow = appDatabase.serverDao().flow()
        .map { it.map(ServerConverter::toModel) }
        .flowOn(dispatcherProvider.io())

    private val credentials = HashMap<String, String>()

    override suspend fun authenticate(uuid: String, password: String) {
        withContext(dispatcherProvider.io()) {
            credentials[uuid] = password
        }
    }

    override suspend fun loadServers(): List<ServerConfig> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.serverDao().loadAll()
                .map(ServerConverter::toModel)
        }
    }

    override suspend fun loadServer(uuid: String): ServerConfig {
        return withContext(dispatcherProvider.io()) {
            val serverEntity = appDatabase.serverDao().load(uuid)
            val serverConfig = ServerConverter.toModel(serverEntity)
            when (serverConfig.authMethod) {
                AuthMethod.PASSWORD -> serverConfig.copy(
                    password = credentials[uuid] ?: serverConfig.password
                )
                AuthMethod.KEY -> serverConfig.copy(
                    passphrase = credentials[uuid] ?: serverConfig.passphrase
                )
            }
        }
    }

    override suspend fun upsertServer(serverConfig: ServerConfig) {
        withContext(dispatcherProvider.io()) {
            credentials.remove(serverConfig.uuid)
            val entity = ServerConverter.toEntity(serverConfig)
            appDatabase.serverDao().insert(entity)
        }
    }

    override suspend fun deleteServer(serverConfig: ServerConfig) {
        withContext(dispatcherProvider.io()) {
            credentials.remove(serverConfig.uuid)
            val entity = ServerConverter.toEntity(serverConfig)
            appDatabase.serverDao().delete(entity)
        }
    }
}