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

package com.blacksquircle.ui.feature.servers.data.mapper

import com.blacksquircle.ui.core.database.entity.server.ServerEntity
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType

internal object ServerMapper {

    fun toModel(serverEntity: ServerEntity): ServerConfig {
        return ServerConfig(
            uuid = serverEntity.uuid,
            scheme = ServerType.of(serverEntity.scheme),
            name = serverEntity.name,
            address = serverEntity.address,
            port = serverEntity.port,
            initialDir = serverEntity.initialDir,
            authMethod = AuthMethod.entries[serverEntity.authMethod],
            username = serverEntity.username,
            password = serverEntity.password,
            keyId = serverEntity.keyId,
            passphrase = serverEntity.passphrase,
        )
    }

    fun toEntity(serverConfig: ServerConfig): ServerEntity {
        return ServerEntity(
            uuid = serverConfig.uuid,
            scheme = serverConfig.scheme.value,
            name = serverConfig.name,
            address = serverConfig.address,
            port = serverConfig.port,
            initialDir = serverConfig.initialDir,
            authMethod = serverConfig.authMethod.ordinal,
            username = serverConfig.username,
            password = serverConfig.password,
            keyId = serverConfig.keyId,
            passphrase = serverConfig.passphrase,
        )
    }
}