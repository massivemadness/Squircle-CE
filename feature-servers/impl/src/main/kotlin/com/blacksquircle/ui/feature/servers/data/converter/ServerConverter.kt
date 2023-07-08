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

package com.blacksquircle.ui.feature.servers.data.converter

import com.blacksquircle.ui.core.storage.database.entity.server.ServerEntity
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig

object ServerConverter {

    fun toModel(serverEntity: ServerEntity): ServerConfig {
        return ServerConfig(
            uuid = serverEntity.uuid,
            scheme = serverEntity.scheme,
            name = serverEntity.name,
            address = serverEntity.address,
            port = serverEntity.port,
            initialDir = serverEntity.initialDir,
            authMethod = AuthMethod.of(serverEntity.authMethod),
            username = serverEntity.username,
            password = serverEntity.password,
            privateKey = serverEntity.privateKey,
            passphrase = serverEntity.passphrase,
        )
    }

    fun toEntity(serverConfig: ServerConfig): ServerEntity {
        return ServerEntity(
            uuid = serverConfig.uuid,
            scheme = serverConfig.scheme,
            name = serverConfig.name,
            address = serverConfig.address,
            port = serverConfig.port,
            initialDir = serverConfig.initialDir,
            authMethod = serverConfig.authMethod.value,
            username = serverConfig.username,
            password = serverConfig.password,
            privateKey = serverConfig.privateKey,
            passphrase = serverConfig.passphrase,
        )
    }
}