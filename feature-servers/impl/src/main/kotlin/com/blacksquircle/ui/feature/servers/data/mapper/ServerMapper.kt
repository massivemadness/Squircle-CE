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

package com.blacksquircle.ui.feature.servers.data.mapper

import android.os.Bundle
import com.blacksquircle.ui.core.storage.database.entity.server.ServerEntity
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerScheme

internal object ServerMapper {

    private const val KEY_UUID = "uuid"
    private const val KEY_SCHEME = "scheme"
    private const val KEY_NAME = "name"
    private const val KEY_ADDRESS = "address"
    private const val KEY_PORT = "port"
    private const val KEY_INITIAL_DIR = "initial_dir"
    private const val KEY_AUTH_METHOD = "auth_method"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_PRIVATE_KEY = "private_key"
    private const val KEY_PASSPHRASE = "passphrase"

    fun toModel(serverEntity: ServerEntity): ServerConfig {
        return ServerConfig(
            uuid = serverEntity.uuid,
            scheme = ServerScheme.of(serverEntity.scheme),
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
            scheme = serverConfig.scheme.value,
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

    fun toBundle(serverConfig: ServerConfig): Bundle {
        return Bundle().apply {
            putString(KEY_UUID, serverConfig.uuid)
            putString(KEY_SCHEME, serverConfig.scheme.value)
            putString(KEY_NAME, serverConfig.name)
            putString(KEY_ADDRESS, serverConfig.address)
            putInt(KEY_PORT, serverConfig.port)
            putString(KEY_INITIAL_DIR, serverConfig.initialDir)
            putInt(KEY_AUTH_METHOD, serverConfig.authMethod.value)
            putString(KEY_USERNAME, serverConfig.username)
            putString(KEY_PASSWORD, serverConfig.password)
            putString(KEY_PRIVATE_KEY, serverConfig.privateKey)
            putString(KEY_PASSPHRASE, serverConfig.passphrase)
        }
    }

    fun fromBundle(bundle: Bundle): ServerConfig {
        return ServerConfig(
            uuid = bundle.getString(KEY_UUID).orEmpty(),
            scheme = ServerScheme.of(bundle.getString(KEY_SCHEME).orEmpty()),
            name = bundle.getString(KEY_NAME).orEmpty(),
            address = bundle.getString(KEY_ADDRESS).orEmpty(),
            port = bundle.getInt(KEY_PORT),
            initialDir = bundle.getString(KEY_INITIAL_DIR).orEmpty(),
            authMethod = AuthMethod.of(bundle.getInt(KEY_AUTH_METHOD)),
            username = bundle.getString(KEY_USERNAME).orEmpty(),
            password = bundle.getString(KEY_PASSWORD),
            privateKey = bundle.getString(KEY_PRIVATE_KEY),
            passphrase = bundle.getString(KEY_PASSPHRASE),
        )
    }
}