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
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentConverterTest {

    @Test
    fun `convert FileModel to DocumentModel`() {
        val fileModel = FileModel(
            path = "/mnt/Test.txt",
            size = 0L,
            lastModified = 1L,
            isFolder = false,
            isHidden = false
        )
        val documentModel = DocumentModel(
            uuid = "0",
            path = "/mnt/Test.txt",
            modified = false,
            position = 0,
            scrollX = 0,
            scrollY = 0,
            selectionStart = 0,
            selectionEnd = 0
        )
        val convert = DocumentConverter.toModel(fileModel)

        assertEquals(documentModel.name, convert.name)
        assertEquals(documentModel.path, convert.path)
        assertEquals(documentModel.modified, convert.modified)
        assertEquals(documentModel.position, convert.position)
        assertEquals(documentModel.scrollX, convert.scrollX)
        assertEquals(documentModel.scrollY, convert.scrollY)
        assertEquals(documentModel.selectionStart, convert.selectionStart)
        assertEquals(documentModel.selectionEnd, convert.selectionEnd)
    }

    @Test
    fun `convert DocumentEntity to DocumentModel`() {
        val documentEntity = DocumentEntity(
            uuid = "0",
            path = "/mnt/Test.txt",
            modified = true,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10
        )
        val documentModel = DocumentModel(
            uuid = "0",
            path = "/mnt/Test.txt",
            modified = true,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10
        )
        val convert = DocumentConverter.toModel(documentEntity)

        assertEquals(documentModel.name, convert.name)
        assertEquals(documentModel.path, convert.path)
        assertEquals(documentModel.modified, convert.modified)
        assertEquals(documentModel.position, convert.position)
        assertEquals(documentModel.scrollX, convert.scrollX)
        assertEquals(documentModel.scrollY, convert.scrollY)
        assertEquals(documentModel.selectionStart, convert.selectionStart)
        assertEquals(documentModel.selectionEnd, convert.selectionEnd)
    }

    @Test
    fun `convert DocumentModel to DocumentEntity`() {
        val documentModel = DocumentModel(
            uuid = "0",
            path = "/mnt/Test.txt",
            modified = false,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10
        )
        val documentEntity = DocumentEntity(
            uuid = "0",
            path = "/mnt/Test.txt",
            modified = false,
            position = 10,
            scrollX = 0,
            scrollY = 50,
            selectionStart = 8,
            selectionEnd = 10
        )
        val convert = DocumentConverter.toEntity(documentModel)

        assertEquals(documentEntity.path, convert.path)
        assertEquals(documentEntity.modified, convert.modified)
        assertEquals(documentEntity.position, convert.position)
        assertEquals(documentEntity.scrollX, convert.scrollX)
        assertEquals(documentEntity.scrollY, convert.scrollY)
        assertEquals(documentEntity.selectionStart, convert.selectionStart)
        assertEquals(documentEntity.selectionEnd, convert.selectionEnd)
    }
}