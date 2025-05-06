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

package com.blacksquircle.ui.feature.servers.domain.repository

import android.net.Uri
import com.blacksquircle.ui.filesystem.base.model.ServerConfig

internal interface ServerRepository {

    suspend fun checkAvailability(serverConfig: ServerConfig): Long
    suspend fun saveKeyFile(fileUri: Uri): String

    suspend fun loadServers(): List<ServerConfig>
    suspend fun loadServer(uuid: String): ServerConfig
    suspend fun upsertServer(serverConfig: ServerConfig)
    suspend fun deleteServer(serverConfig: ServerConfig)
}