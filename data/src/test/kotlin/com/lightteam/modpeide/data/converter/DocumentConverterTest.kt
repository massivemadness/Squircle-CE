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
import com.lightteam.modpeide.data.model.entity.DocumentEntity
import com.lightteam.modpeide.domain.editor.DocumentModel
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class DocumentConverterTest {

    @Test
    fun `convert FileModel to DocumentModel`() {
        val fileModel = FileModel(
            "Test",
            "/mnt/test",
            0L,
            1L,
            isFolder = false,
            isHidden = false
        )
        val documentModel = DocumentModel(
            "0",
            "Test",
            "/mnt/test",
            0,
            0,
            0,
            0
        )
        val convert = DocumentConverter.toModel(fileModel)

        assertEquals(documentModel.name, convert.name)
        assertEquals(documentModel.path, convert.path)
        assertEquals(documentModel.scrollX, convert.scrollX)
        assertEquals(documentModel.scrollY, convert.scrollY)
        assertEquals(documentModel.selectionStart, convert.selectionStart)
        assertEquals(documentModel.selectionEnd, convert.selectionEnd)
    }

    @Test
    fun `convert DocumentEntity to DocumentModel`() {
        val documentEntity = DocumentEntity(
            "0",
            "Test",
            "/mnt/test",
            0,
            50,
            8,
            10
        )
        val documentModel = DocumentModel(
            "0",
            "Test",
            "/mnt/test",
            0,
            50,
            8,
            10
        )
        val convert = DocumentConverter.toModel(documentEntity)

        assertEquals(documentModel.name, convert.name)
        assertEquals(documentModel.path, convert.path)
        assertEquals(documentModel.scrollX, convert.scrollX)
        assertEquals(documentModel.scrollY, convert.scrollY)
        assertEquals(documentModel.selectionStart, convert.selectionStart)
        assertEquals(documentModel.selectionEnd, convert.selectionEnd)
    }

    @Test
    fun `convert DocumentModel to DocumentEntity`() {
        val documentModel = DocumentModel(
            "0",
            "Test",
            "/mnt/test",
            0,
            50,
            8,
            10
        )
        val documentEntity = DocumentEntity(
            "0",
            "Test",
            "/mnt/test",
            0,
            50,
            8,
            10
        )
        val convert = DocumentConverter.toEntity(documentModel)

        assertEquals(documentEntity.name, convert.name)
        assertEquals(documentEntity.path, convert.path)
        assertEquals(documentEntity.scrollX, convert.scrollX)
        assertEquals(documentEntity.scrollY, convert.scrollY)
        assertEquals(documentEntity.selectionStart, convert.selectionStart)
        assertEquals(documentEntity.selectionEnd, convert.selectionEnd)
    }
}