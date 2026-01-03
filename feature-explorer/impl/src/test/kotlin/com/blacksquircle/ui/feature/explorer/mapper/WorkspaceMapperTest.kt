/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.explorer.mapper

import com.blacksquircle.ui.core.database.entity.workspace.WorkspaceEntity
import com.blacksquircle.ui.feature.explorer.data.mapper.WorkspaceMapper
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceType
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkspaceMapperTest {

    @Test
    fun `When mapping WorkspaceEntity Then return WorkspaceModel`() {
        // Given
        val workspaceEntity = WorkspaceEntity(
            uuid = "12345",
            name = "Custom",
            type = WorkspaceType.CUSTOM.value,
            fileUri = "file:///storage/emulated/0/Documents",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
        )

        // When
        val actual = WorkspaceMapper.toModel(workspaceEntity)

        // Then
        val expected = WorkspaceModel(
            uuid = "12345",
            name = "Custom",
            type = WorkspaceType.CUSTOM,
            defaultLocation = FileModel(
                fileUri = "file:///storage/emulated/0/Documents",
                filesystemUuid = LocalFilesystem.LOCAL_UUID,
                isDirectory = true,
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `When mapping WorkspaceModel Then return WorkspaceEntity`() {
        // Given
        val workspaceModel = WorkspaceModel(
            uuid = "12345",
            name = "Custom",
            type = WorkspaceType.CUSTOM,
            defaultLocation = FileModel(
                fileUri = "file:///storage/emulated/0/Documents",
                filesystemUuid = LocalFilesystem.LOCAL_UUID,
                isDirectory = true,
            ),
        )

        // When
        val actual = WorkspaceMapper.toEntity(workspaceModel)

        // Then
        val expected = WorkspaceEntity(
            uuid = "12345",
            name = "Custom",
            type = WorkspaceType.CUSTOM.value,
            fileUri = "file:///storage/emulated/0/Documents",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `When mapping ServerConfig Then return WorkspaceModel`() {
        // Given
        val serverConfig = ServerConfig(
            uuid = "12345",
            scheme = ServerType.SFTP,
            name = "Custom",
            address = "192.168.0.1",
            port = 22,
            initialDir = "Documents",
            authMethod = AuthMethod.PASSWORD,
            username = "test",
            password = null,
            keyId = null,
            passphrase = null,
        )

        // When
        val actual = WorkspaceMapper.toModel(serverConfig)

        // Then
        val expected = WorkspaceModel(
            uuid = "12345",
            name = "Custom",
            type = WorkspaceType.SERVER,
            defaultLocation = FileModel(
                fileUri = "sftp:///Documents",
                filesystemUuid = "12345",
                isDirectory = true,
            ),
        )
        assertEquals(expected, actual)
    }
}