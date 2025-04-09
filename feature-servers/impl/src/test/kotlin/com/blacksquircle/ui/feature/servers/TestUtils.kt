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

package com.blacksquircle.ui.feature.servers

import com.blacksquircle.ui.core.database.entity.server.ServerEntity
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType

internal fun createServerConfig(
    uuid: String = "1",
    scheme: ServerType = ServerType.FTP,
    name: String = "Server",
    address: String = "192.168.1.1",
    port: Int = 21,
    initialDir: String = "/",
    authMethod: AuthMethod = AuthMethod.PASSWORD,
    username: String = "username",
    password: String? = null,
    keyId: String? = null,
    passphrase: String? = null,
): ServerConfig {
    return ServerConfig(
        uuid = uuid,
        scheme = scheme,
        name = name,
        address = address,
        port = port,
        initialDir = initialDir,
        authMethod = authMethod,
        username = username,
        password = password,
        keyId = keyId,
        passphrase = passphrase,
    )
}

internal fun createServerEntity(
    uuid: String = "1",
    scheme: ServerType = ServerType.FTP,
    name: String = "Server",
    address: String = "192.168.1.1",
    port: Int = 21,
    initialDir: String = "/",
    authMethod: AuthMethod = AuthMethod.PASSWORD,
    username: String = "username",
    password: String? = null,
    keyId: String? = null,
    passphrase: String? = null,
): ServerEntity {
    return ServerEntity(
        uuid = uuid,
        scheme = scheme.value,
        name = name,
        address = address,
        port = port,
        initialDir = initialDir,
        authMethod = authMethod.ordinal,
        username = username,
        password = password,
        keyId = keyId,
        passphrase = passphrase,
    )
}