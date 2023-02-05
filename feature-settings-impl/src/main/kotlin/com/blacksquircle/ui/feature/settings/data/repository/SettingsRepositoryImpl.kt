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

package com.blacksquircle.ui.feature.settings.data.repository

import com.blacksquircle.ui.core.data.converter.ServerConverter
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.settings.domain.SettingsRepository
import com.blacksquircle.ui.filesystem.base.model.ServerModel
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
) : SettingsRepository {

    override suspend fun fetchServers(): List<ServerModel> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.serverDao().loadAll()
                .map(ServerConverter::toModel)
        }
    }

    override suspend fun upsertServer(serverModel: ServerModel) {
        withContext(dispatcherProvider.io()) {
            val entity = ServerConverter.toEntity(serverModel)
            appDatabase.serverDao().insert(entity)
        }
    }

    override suspend fun deleteServer(serverModel: ServerModel) {
        withContext(dispatcherProvider.io()) {
            val entity = ServerConverter.toEntity(serverModel)
            appDatabase.serverDao().delete(entity)
        }
    }

    override suspend fun resetKeyboardPreset() {
        withContext(dispatcherProvider.io()) {
            settingsManager.remove(SettingsManager.KEY_KEYBOARD_PRESET)
        }
    }
}