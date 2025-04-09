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

package com.blacksquircle.ui.feature.explorer.utils

import com.blacksquircle.ui.core.database.entity.path.PathEntity
import com.blacksquircle.ui.feature.explorer.data.mapper.FileMapper
import com.blacksquircle.ui.filesystem.base.model.FileModel
import org.junit.Assert.assertEquals
import org.junit.Test

class FileMapperTest {

    @Test
    fun `When mapping PathEntity Then return FileModel`() {
        // Given
        val pathEntity = PathEntity(
            filesystemUuid = "12345",
            fileUri = "file:///storage/emulated/0/Documents/untitled.txt",
        )
        val expected = FileModel(
            filesystemUuid = "12345",
            fileUri = "file:///storage/emulated/0/Documents/untitled.txt",
        )

        // When
        val actual = FileMapper.toModel(pathEntity)

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `When mapping FileModel Then return PathEntity`() {
        // Given
        val fileModel = FileModel(
            filesystemUuid = "12345",
            fileUri = "file:///storage/emulated/0/Documents/untitled.txt",
        )
        val expected = PathEntity(
            filesystemUuid = "12345",
            fileUri = "file:///storage/emulated/0/Documents/untitled.txt",
        )

        // When
        val actual = FileMapper.toEntity(fileModel)

        // Then
        assertEquals(expected, actual)
    }
}