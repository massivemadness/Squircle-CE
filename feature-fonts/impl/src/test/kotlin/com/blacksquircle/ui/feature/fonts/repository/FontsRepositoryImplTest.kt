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

package com.blacksquircle.ui.feature.fonts.repository

import android.content.Context
import android.graphics.Typeface
import com.blacksquircle.ui.core.database.dao.font.FontDao
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_FONT_TYPE
import com.blacksquircle.ui.feature.fonts.createFontEntity
import com.blacksquircle.ui.feature.fonts.createFontModel
import com.blacksquircle.ui.feature.fonts.data.repository.FontsRepositoryImpl
import com.blacksquircle.ui.feature.fonts.data.utils.createTypefaceFromPath
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class FontsRepositoryImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val fontDao = mockk<FontDao>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val typeface = mockk<Typeface>()

    private val fontsRepository = FontsRepositoryImpl(
        dispatcherProvider = dispatcherProvider,
        settingsManager = settingsManager,
        fontDao = fontDao,
        context = context
    )

    @Before
    fun setup() {
        mockkStatic(Context::createTypefaceFromPath)
        every { any<Context>().createTypefaceFromPath(any()) } returns typeface

        mockkObject(Directories)
        every { Directories.fontsDir(context) } returns mockk<File>().apply {
            every { path } returns ""
        }
    }

    @Test
    fun `When loading fonts Then load from assets and database`() = runTest {
        // Given
        val fontEntity = createFontEntity()
        coEvery { fontDao.loadAll() } returns listOf(fontEntity)

        // When
        val fonts = fontsRepository.loadFonts("")

        // Then
        assert(fonts.isNotEmpty())
        coVerify(exactly = 1) { fontDao.loadAll() }
    }

    @Test
    fun `When loading with query Then filter out by name`() = runTest {
        // Given
        val fontEntity = createFontEntity(name = "Custom Font")
        val fontModel = createFontModel(
            name = "Custom Font",
            typeface = typeface,
        )
        coEvery { fontDao.loadAll() } returns listOf(fontEntity)

        // When
        val fonts = fontsRepository.loadFonts("Custom Font")

        // Then
        val expected = listOf(fontModel)
        assertEquals(expected, fonts)
    }

    @Test
    fun `When select font Then update selected font`() = runTest {
        // Given
        val fontModel = createFontModel()

        // When
        fontsRepository.selectFont(fontModel)

        // Then
        verify(exactly = 1) { settingsManager.fontType = fontModel.uuid }
    }

    @Test
    fun `When remove font Then delete from database`() = runTest {
        // Given
        val fontModel = createFontModel()
        every { settingsManager.fontType } returns "different"

        // When
        fontsRepository.removeFont(fontModel)

        // Then
        coVerify(exactly = 1) { fontDao.delete(fontModel.uuid) }
        verify(exactly = 0) { settingsManager.remove(KEY_FONT_TYPE) }
    }

    @Test
    fun `When remove selected font Then reset selected font`() = runTest {
        // Given
        val fontModel = createFontModel()
        every { settingsManager.fontType } returns fontModel.uuid

        // When
        fontsRepository.removeFont(fontModel)

        // Then
        coVerify(exactly = 1) { fontDao.delete(fontModel.uuid) }
        verify(exactly = 1) { settingsManager.remove(KEY_FONT_TYPE) }
    }
}