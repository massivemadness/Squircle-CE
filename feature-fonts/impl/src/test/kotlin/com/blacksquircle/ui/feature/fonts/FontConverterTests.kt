/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.fonts

import com.blacksquircle.ui.core.storage.database.entity.font.FontEntity
import com.blacksquircle.ui.feature.fonts.data.converter.FontConverter
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import org.junit.Assert.assertEquals
import org.junit.Test

class FontConverterTests {

    @Test
    fun `convert FontEntity to FontModel`() {
        val fontEntity = FontEntity(
            fontUuid = "droid_sans_mono.ttf",
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
        )
        val fontModel = FontModel(
            fontUuid = "droid_sans_mono.ttf",
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            isExternal = true,
        )

        assertEquals(fontModel, FontConverter.toModel(fontEntity))
    }

    @Test
    fun `convert FontModel to FontEntity`() {
        val fontEntity = FontEntity(
            fontUuid = "droid_sans_mono.ttf",
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            supportLigatures = false,
        )
        val fontModel = FontModel(
            fontUuid = "droid_sans_mono.ttf",
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
            isExternal = true,
        )

        assertEquals(fontEntity, FontConverter.toEntity(fontModel))
    }
}