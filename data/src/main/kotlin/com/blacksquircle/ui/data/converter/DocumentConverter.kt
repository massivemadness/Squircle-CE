/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.data.converter

import com.blacksquircle.ui.data.storage.database.entity.document.DocumentEntity
import com.blacksquircle.ui.domain.model.editor.DocumentModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import java.util.*

object DocumentConverter {

    fun toModel(documentModel: DocumentModel): FileModel {
        return FileModel(
            path = documentModel.path,
            size = 0,
            lastModified = 0,
            isFolder = false,
            isHidden = documentModel.name.startsWith(".")
        )
    }

    fun toModel(fileModel: FileModel): DocumentModel {
        return DocumentModel(
            uuid = UUID.randomUUID().toString(),
            path = fileModel.path,
            modified = false,
            position = 0,
            scrollX = 0,
            scrollY = 0,
            selectionStart = 0,
            selectionEnd = 0
        )
    }

    fun toModel(documentEntity: DocumentEntity): DocumentModel {
        return DocumentModel(
            uuid = documentEntity.uuid,
            path = documentEntity.path,
            modified = documentEntity.modified,
            position = documentEntity.position,
            scrollX = documentEntity.scrollX,
            scrollY = documentEntity.scrollY,
            selectionStart = documentEntity.selectionStart,
            selectionEnd = documentEntity.selectionEnd
        )
    }

    fun toEntity(documentModel: DocumentModel): DocumentEntity {
        return DocumentEntity(
            uuid = documentModel.uuid,
            path = documentModel.path,
            modified = documentModel.modified,
            position = documentModel.position,
            scrollX = documentModel.scrollX,
            scrollY = documentModel.scrollY,
            selectionStart = documentModel.selectionStart,
            selectionEnd = documentModel.selectionEnd
        )
    }
}