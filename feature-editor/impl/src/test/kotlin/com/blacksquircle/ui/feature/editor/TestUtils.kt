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

package com.blacksquircle.ui.feature.editor

import com.blacksquircle.ui.core.database.entity.document.DocumentEntity
import com.blacksquircle.ui.feature.editor.data.model.LanguageScope
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem

internal fun createDocument(
    uuid: String,
    fileName: String,
    position: Int = 0,
    modified: Boolean = false,
): DocumentModel {
    return DocumentModel(
        uuid = uuid,
        fileUri = "file:///storage/emulated/0/$fileName",
        filesystemUuid = LocalFilesystem.LOCAL_UUID,
        language = LanguageScope.TEXT,
        modified = modified,
        position = position,
        scrollX = 0,
        scrollY = 0,
        selectionStart = 0,
        selectionEnd = 0,
    )
}

internal fun createDocumentEntity(
    uuid: String,
    fileName: String,
    position: Int = 0,
    modified: Boolean = false,
): DocumentEntity {
    return DocumentEntity(
        uuid = uuid,
        fileUri = "file:///storage/emulated/0/$fileName",
        filesystemUuid = LocalFilesystem.LOCAL_UUID,
        language = LanguageScope.TEXT,
        modified = modified,
        position = position,
        scrollX = 0,
        scrollY = 0,
        selectionStart = 0,
        selectionEnd = 0,
    )
}