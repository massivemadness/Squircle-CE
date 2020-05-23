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

import com.lightteam.modpeide.domain.model.font.FontModel
import com.lightteam.modpeide.database.entity.font.FontEntity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class FontConverterTest {

    @Test
    fun `convert FontEntity to FontModel`() {
        val fontEntity = FontEntity(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
            isExternal = false,
            isPaid = false
        )
        val fontModel = FontModel(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
            isExternal = false,
            isPaid = false
        )
        val convert = FontConverter.toModel(fontEntity)

        assertEquals(fontModel.fontName, convert.fontName)
        assertEquals(fontModel.fontPath, convert.fontPath)
        assertEquals(fontModel.supportLigatures, convert.supportLigatures)
        assertEquals(fontModel.isExternal, convert.isExternal)
        assertEquals(fontModel.isPaid, convert.isPaid)
    }

    @Test
    fun `convert FontModel to FontEntity`() {
        val fontEntity = FontEntity(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
            isExternal = false,
            isPaid = false
        )
        val fontModel = FontModel(
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
            isExternal = false,
            isPaid = false
        )
        val convert = FontConverter.toEntity(fontModel)

        assertEquals(fontEntity.fontName, convert.fontName)
        assertEquals(fontEntity.fontPath, convert.fontPath)
        assertEquals(fontEntity.supportLigatures, convert.supportLigatures)
        assertEquals(fontEntity.isExternal, convert.isExternal)
        assertEquals(fontEntity.isPaid, convert.isPaid)
    }
}