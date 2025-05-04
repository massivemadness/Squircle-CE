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

package com.blacksquircle.ui.feature.explorer.data.mapper

import com.blacksquircle.ui.core.database.entity.workspace.WorkspaceEntity
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FilesystemType
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import java.io.File

internal object WorkspaceMapper {

    fun toModel(workspaceEntity: WorkspaceEntity): WorkspaceModel {
        return WorkspaceModel(
            uuid = workspaceEntity.uuid,
            name = workspaceEntity.name,
            filesystemType = FilesystemType.of(workspaceEntity.type),
            defaultLocation = FileModel(
                fileUri = workspaceEntity.fileUri,
                filesystemUuid = workspaceEntity.filesystemUuid,
                isDirectory = true,
            )
        )
    }

    fun toModel(serverConfig: ServerConfig): WorkspaceModel {
        val scheme = serverConfig.scheme.value
        val path = serverConfig.initialDir.trim(File.separatorChar)
        val fileUri = if (path.isNotEmpty()) scheme + File.separator + path else scheme
        return WorkspaceModel(
            uuid = serverConfig.uuid,
            name = serverConfig.name,
            filesystemType = FilesystemType.SERVER,
            defaultLocation = FileModel(
                fileUri = fileUri,
                filesystemUuid = serverConfig.uuid,
                isDirectory = true,
            ),
        )
    }

    fun toEntity(workspace: WorkspaceModel): WorkspaceEntity {
        return WorkspaceEntity(
            uuid = workspace.uuid,
            name = workspace.name,
            type = workspace.filesystemType.value,
            fileUri = workspace.defaultLocation.fileUri,
            filesystemUuid = workspace.defaultLocation.filesystemUuid,
        )
    }
}