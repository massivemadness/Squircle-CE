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

import com.blacksquircle.ui.data.storage.database.entity.font.FontEntity
import com.blacksquircle.ui.domain.model.fonts.FontModel
import org.junit.Assert.assertEquals
import org.junit.Test

class FontConverterTest {

    @Test
    fun `convert FontEntity to FontModel`() {
        val fontEntity = FontEntity(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false
        )
        val fontModel = FontModel(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
            isExternal = true
        )
        val convert = FontConverter.toModel(fontEntity)

        assertEquals(fontModel.fontName, convert.fontName)
        assertEquals(fontModel.fontPath, convert.fontPath)
        assertEquals(fontModel.supportLigatures, convert.supportLigatures)
        assertEquals(fontModel.isExternal, convert.isExternal)
    }

    @Test
    fun `convert FontModel to FontEntity`() {
        val fontEntity = FontEntity(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false
        )
        val fontModel = FontModel(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
            isExternal = true
        )
        val convert = FontConverter.toEntity(fontModel)

        assertEquals(fontEntity.fontName, convert.fontName)
        assertEquals(fontEntity.fontPath, convert.fontPath)
        assertEquals(fontEntity.supportLigatures, convert.supportLigatures)
    }
}