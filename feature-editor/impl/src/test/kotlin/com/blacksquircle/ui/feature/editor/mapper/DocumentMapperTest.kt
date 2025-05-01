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

package com.blacksquircle.ui.feature.editor.mapper

import com.blacksquircle.ui.core.database.entity.document.DocumentEntity
import com.blacksquircle.ui.feature.editor.data.mapper.DocumentMapper
import com.blacksquircle.ui.feature.editor.data.model.LanguageScope
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentMapperTest {

    @Test
    fun `When mapping FileModel Then return DocumentModel`() {
        // Given
        val fileModel = FileModel(
            fileUri = "file:///storage/emulated/0/Test.txt",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            size = 0L,
            lastModified = 1L,
            isDirectory = false,
        )
        val expected = DocumentModel(
            uuid = "0",
            fileUri = "file:///storage/emulated/0/Test.txt",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            language = LanguageScope.TEXT,
            modified = false,
            position = 0,
            scrollX = 0,
            scrollY = 0,
            selectionStart = 0,
            selectionEnd = 0,
        )

        // When
        val actual = DocumentMapper.toModel(fileModel, position = 0)

        // Then
        // assertEquals(expected.uuid, actual.uuid)
        assertEquals(expected.fileUri, actual.fileUri)
        assertEquals(expected.filesystemUuid, actual.filesystemUuid)
        assertEquals(expected.language, actual.language)
        assertEquals(expected.modified, actual.modified)
        assertEquals(expected.position, actual.position)
        assertEquals(expected.scrollX, actual.scrollX)
        assertEquals(expected.scrollY, actual.scrollY)
        assertEquals(expected.selectionStart, actual.selectionStart)
        assertEquals(expected.selectionEnd, actual.selectionEnd)
    }

    @Test
    fun `When mapping DocumentEntity Then return DocumentModel`() {
        // Given
        val documentEntity = DocumentEntity(
            uuid = "0",
            fileUri = "file:///storage/emulated/0/Test.txt",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            language = LanguageScope.TEXT,
            modified = true,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10,
        )
        val expected = DocumentModel(
            uuid = "0",
            fileUri = "file:///storage/emulated/0/Test.txt",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            language = LanguageScope.TEXT,
            modified = true,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10,
        )

        // When
        val actual = DocumentMapper.toModel(documentEntity)

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `When mapping DocumentModel Then return DocumentEntity`() {
        // Given
        val documentModel = DocumentModel(
            uuid = "0",
            fileUri = "file:///storage/emulated/0/Test.txt",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            language = LanguageScope.TEXT,
            modified = false,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10,
        )
        val expected = DocumentEntity(
            uuid = "0",
            fileUri = "file:///storage/emulated/0/Test.txt",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            language = LanguageScope.TEXT,
            modified = false,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10,
        )

        // When
        val actual = DocumentMapper.toEntity(documentModel)

        // Then
        assertEquals(expected, actual)
    }
}