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

package com.blacksquircle.ui.feature.servers.mapper

import com.blacksquircle.ui.core.database.entity.server.ServerEntity
import com.blacksquircle.ui.feature.servers.data.mapper.ServerMapper
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerMapperTests {

    @Test
    fun `When mapping ServerEntity Then return ServerConfig`() {
        // Given
        val serverEntity = ServerEntity(
            uuid = "1234567890",
            scheme = ServerType.FTP.value,
            name = "test",
            address = "192.168.21.97",
            port = 21,
            initialDir = "",
            authMethod = AuthMethod.PASSWORD.ordinal,
            username = "username",
            password = "password",
            keyId = null,
            passphrase = "test",
        )
        val expected = ServerConfig(
            uuid = "1234567890",
            scheme = ServerType.FTP,
            name = "test",
            address = "192.168.21.97",
            port = 21,
            initialDir = "",
            authMethod = AuthMethod.PASSWORD,
            username = "username",
            password = "password",
            keyId = null,
            passphrase = "test",
        )

        // When
        val actual = ServerMapper.toModel(serverEntity)

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `When mapping ServerConfig Then return ServerEntity`() {
        // Given
        val serverConfig = ServerConfig(
            uuid = "1234567890",
            scheme = ServerType.FTP,
            name = "test",
            address = "192.168.21.97",
            port = 21,
            initialDir = "",
            authMethod = AuthMethod.PASSWORD,
            username = "username",
            password = "password",
            keyId = null,
            passphrase = "test",
        )
        val expected = ServerEntity(
            uuid = "1234567890",
            scheme = ServerType.FTP.value,
            name = "test",
            address = "192.168.21.97",
            port = 21,
            initialDir = "",
            authMethod = AuthMethod.PASSWORD.ordinal,
            username = "username",
            password = "password",
            keyId = null,
            passphrase = "test",
        )

        // When
        val actual = ServerMapper.toEntity(serverConfig)

        // Then
        assertEquals(expected, actual)
    }
}