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

package com.blacksquircle.ui.feature.themes

import android.content.Context
import com.blacksquircle.ui.core.database.dao.theme.ThemeDao
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_EDITOR_THEME
import com.blacksquircle.ui.core.tests.TestDispatcherProvider
import com.blacksquircle.ui.feature.themes.api.interactor.ThemesInteractor
import com.blacksquircle.ui.feature.themes.data.repository.ThemesRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class ThemesRepositoryImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val themesInteractor = mockk<ThemesInteractor>(relaxed = true)
    private val themeDao = mockk<ThemeDao>(relaxUnitFun = true)
    private val context = mockk<Context>()

    private val themesRepository = ThemesRepositoryImpl(
        dispatcherProvider = dispatcherProvider,
        settingsManager = settingsManager,
        themesInteractor = themesInteractor,
        themeDao = themeDao,
        context = context
    )

    @Before
    fun setup() {
        mockkObject(Directories)
        every { Directories.themesDir(context) } returns mockk<File>().apply {
            every { path } returns ""
        }
    }

    @Test
    fun `When loading themes Then load from assets and database`() = runTest {
        // Given
        val themeEntity = createThemeEntity(name = "Custom Theme")
        coEvery { themeDao.loadAll() } returns listOf(themeEntity)

        // When
        val themes = themesRepository.loadThemes("")

        // Then
        assert(themes.isNotEmpty())
        coVerify(exactly = 1) { themeDao.loadAll() }
    }

    @Test
    fun `When loading with query Then filter out by name`() = runTest {
        // Given
        val themeEntity = createThemeEntity(name = "Custom Theme")
        val themeModel = createThemeModel(name = "Custom Theme")
        coEvery { themeDao.loadAll() } returns listOf(themeEntity)

        // When
        val themes = themesRepository.loadThemes("Custom Theme")

        // Then
        val expected = listOf(themeModel)
        assertEquals(expected, themes)
    }

    @Test
    fun `When select theme Then update selected theme`() = runTest {
        // Given
        val themeModel = createThemeModel()

        // When
        themesRepository.selectTheme(themeModel)

        // Then
        verify(exactly = 1) { settingsManager.editorTheme = themeModel.uuid }
    }

    @Test
    fun `When remove theme Then delete from database`() = runTest {
        // Given
        val themeModel = createThemeModel()
        every { settingsManager.editorTheme } returns "different"

        // When
        themesRepository.removeTheme(themeModel)

        // Then
        coVerify(exactly = 1) { themeDao.delete(themeModel.uuid) }
        verify(exactly = 0) { settingsManager.remove(KEY_EDITOR_THEME) }
    }

    @Test
    fun `When remove selected theme Then reset selected theme`() = runTest {
        // Given
        val themeModel = createThemeModel()
        every { settingsManager.editorTheme } returns themeModel.uuid

        // When
        themesRepository.removeTheme(themeModel)

        // Then
        coVerify(exactly = 1) { themeDao.delete(themeModel.uuid) }
        verify(exactly = 1) { settingsManager.remove(KEY_EDITOR_THEME) }
    }
}