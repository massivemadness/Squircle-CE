/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.data.converter

import com.lightteam.filesystem.model.FileModel
import com.lightteam.modpeide.database.entity.document.DocumentEntity
import com.lightteam.modpeide.domain.editor.DocumentModel
import java.util.*

object DocumentConverter {

    fun toModel(documentModel: DocumentModel): FileModel {
        return FileModel(
            name = documentModel.name,
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
            name = fileModel.name,
            path = fileModel.path,
            scrollX = 0,
            scrollY = 0,
            selectionStart = 0,
            selectionEnd = 0
        )
    }

    fun toModel(entity: DocumentEntity): DocumentModel {
        return DocumentModel(
            uuid = entity.uuid,
            name = entity.name,
            path = entity.path,
            scrollX = entity.scrollX,
            scrollY = entity.scrollY,
            selectionStart = entity.selectionStart,
            selectionEnd = entity.selectionEnd
        )
    }

    fun toEntity(model: DocumentModel): DocumentEntity {
        return DocumentEntity(
            uuid = model.uuid,
            name = model.name,
            path = model.path,
            scrollX = model.scrollX,
            scrollY = model.scrollY,
            selectionStart = model.selectionStart,
            selectionEnd = model.selectionEnd
        )
    }
}