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

import com.blacksquircle.ui.core.database.entity.path.PathEntity
import com.blacksquircle.ui.filesystem.base.model.FileModel

internal object FileMapper {

    fun toEntity(fileModel: FileModel): PathEntity {
        return PathEntity(
            filesystemUuid = fileModel.filesystemUuid,
            fileUri = fileModel.fileUri,
        )
    }

    fun toModel(entity: PathEntity): FileModel {
        return FileModel(
            filesystemUuid = entity.filesystemUuid,
            fileUri = entity.fileUri,
        )
    }
}