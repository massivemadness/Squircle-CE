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

package com.blacksquircle.ui.feature.editor.data.mapper

import com.blacksquircle.ui.core.database.entity.document.DocumentEntity
import com.blacksquircle.ui.feature.editor.data.model.FileAssociation
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import java.util.*

internal object DocumentMapper {

    fun toModel(documentModel: DocumentModel): FileModel {
        return FileModel(
            fileUri = documentModel.fileUri,
            filesystemUuid = documentModel.filesystemUuid,
            size = 0,
            lastModified = 0,
            isDirectory = false,
        )
    }

    fun toModel(fileModel: FileModel, position: Int): DocumentModel {
        return DocumentModel(
            uuid = UUID.randomUUID().toString(),
            fileUri = fileModel.fileUri,
            filesystemUuid = fileModel.filesystemUuid,
            language = FileAssociation.guessLanguage(fileModel.extension),
            modified = false,
            position = position,
            scrollX = 0,
            scrollY = 0,
            selectionStart = 0,
            selectionEnd = 0,
        )
    }

    fun toModel(documentEntity: DocumentEntity): DocumentModel {
        return DocumentModel(
            uuid = documentEntity.uuid,
            fileUri = documentEntity.fileUri,
            filesystemUuid = documentEntity.filesystemUuid,
            language = documentEntity.language,
            modified = documentEntity.modified,
            position = documentEntity.position,
            scrollX = documentEntity.scrollX,
            scrollY = documentEntity.scrollY,
            selectionStart = documentEntity.selectionStart,
            selectionEnd = documentEntity.selectionEnd,
        )
    }

    fun toEntity(documentModel: DocumentModel): DocumentEntity {
        return DocumentEntity(
            uuid = documentModel.uuid,
            fileUri = documentModel.fileUri,
            filesystemUuid = documentModel.filesystemUuid,
            language = documentModel.language,
            modified = documentModel.modified,
            position = documentModel.position,
            scrollX = documentModel.scrollX,
            scrollY = documentModel.scrollY,
            selectionStart = documentModel.selectionStart,
            selectionEnd = documentModel.selectionEnd,
        )
    }
}