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

package com.blacksquircle.ui.feature.fonts

import android.graphics.Typeface
import com.blacksquircle.ui.core.storage.database.entity.font.FontEntity
import com.blacksquircle.ui.feature.fonts.data.mapper.FontMapper
import com.blacksquircle.ui.feature.fonts.data.model.InternalFont
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class FontMapperTests {

    private val typeface = mockk<Typeface>()

    @Test
    fun `When converting external font to FontModel Then return mapped object`() {
        // Given
        val fontEntity = FontEntity(
            fontUuid = "droid_sans_mono.ttf",
            fontName = "Droid Sans Mono",
            fontPath = "/storage/emulated/0/font.ttf",
        )
        val expected = FontModel(
            uuid = "droid_sans_mono.ttf",
            name = "Droid Sans Mono",
            path = "/storage/emulated/0/font.ttf",
            typeface = typeface,
            isExternal = true,
        )

        // When
        val actual = FontMapper.toModel(fontEntity, typeface)

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `When converting internal font to FontModel Then return mapped object`() {
        // Given
        val internalFont = InternalFont.DROID_SANS_MONO
        val expected = FontModel(
            uuid = "droid_sans_mono",
            name = "Droid Sans Mono",
            path = "file:///android_asset/fonts/droid_sans_mono.ttf",
            typeface = typeface,
            isExternal = false,
        )

        // When
        val actual = FontMapper.toModel(internalFont, typeface)

        // Then
        assertEquals(expected, actual)
    }
}