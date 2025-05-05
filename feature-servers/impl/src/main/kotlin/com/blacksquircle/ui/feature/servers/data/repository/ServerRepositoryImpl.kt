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

import android.content.Context
import android.net.Uri
import com.blacksquircle.ui.core.database.dao.server.ServerDao
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.servers.api.factory.ServerFactory
import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import com.blacksquircle.ui.feature.servers.data.mapper.ServerMapper
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

internal class ServerRepositoryImpl(
    private val serverFactory: ServerFactory,
    private val settingsManager: SettingsManager,
    private val dispatcherProvider: DispatcherProvider,
    private val serverDao: ServerDao,
    private val context: Context,
) : ServerRepository {

    override suspend fun checkAvailability(serverConfig: ServerConfig): Long {
        return withContext(dispatcherProvider.io()) {
            val filesystem = serverFactory.create(serverConfig)

            val startTime = System.currentTimeMillis()
            filesystem.ping()
            val endTime = System.currentTimeMillis()

            endTime - startTime
        }
    }

    override suspend fun saveKeyFile(fileUri: Uri): String {
        return withContext(dispatcherProvider.io()) {
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val keyUuid = UUID.randomUUID().toString()
                val keyFile = File(Directories.keysDir(context), keyUuid)
                if (!keyFile.exists()) {
                    keyFile.createNewFile()
                    inputStream.copyTo(keyFile.outputStream())
                }
                return@withContext keyUuid
            }
            throw IOException("Unable to open file: $fileUri")
        }
    }

    override suspend fun loadServers(): List<ServerConfig> {
        return withContext(dispatcherProvider.io()) {
            serverDao.loadAll().map(ServerMapper::toModel)
        }
    }

    override suspend fun loadServer(uuid: String): ServerConfig {
        return withContext(dispatcherProvider.io()) {
            val serverEntity = serverDao.load(uuid)
            ServerMapper.toModel(serverEntity)
        }
    }

    override suspend fun upsertServer(serverConfig: ServerConfig) {
        withContext(dispatcherProvider.io()) {
            ServerCredentials.remove(serverConfig.uuid)
            if (settingsManager.workspace == serverConfig.uuid) {
                settingsManager.remove(SettingsManager.KEY_WORKSPACE)
            }
            val entity = ServerMapper.toEntity(serverConfig)
            serverDao.insert(entity)
        }
    }

    override suspend fun deleteServer(serverConfig: ServerConfig) {
        withContext(dispatcherProvider.io()) {
            if (settingsManager.workspace == serverConfig.uuid) {
                settingsManager.remove(SettingsManager.KEY_WORKSPACE)
            }
            ServerCredentials.remove(serverConfig.uuid)
            serverDao.delete(serverConfig.uuid)
        }
    }
}