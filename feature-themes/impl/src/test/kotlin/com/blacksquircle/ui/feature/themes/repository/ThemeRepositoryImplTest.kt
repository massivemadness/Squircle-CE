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

package com.blacksquircle.ui.feature.themes.repository

import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_EDITOR_THEME
import com.blacksquircle.ui.feature.themes.api.interactor.ThemeInteractor
import com.blacksquircle.ui.feature.themes.createThemeModel
import com.blacksquircle.ui.feature.themes.data.mapper.ThemeMapper
import com.blacksquircle.ui.feature.themes.data.model.AssetsTheme
import com.blacksquircle.ui.feature.themes.data.repository.ThemeRepositoryImpl
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeRepositoryImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val themeInteractor = mockk<ThemeInteractor>(relaxed = true)

    private val themesRepository = ThemeRepositoryImpl(
        dispatcherProvider = dispatcherProvider,
        settingsManager = settingsManager,
        themeInteractor = themeInteractor,
    )

    @Test
    fun `When loading themes Then load from assets and database`() = runTest {
        // Given
        val themeList = AssetsTheme.entries.map(ThemeMapper::toModel)

        // When
        val themes = themesRepository.loadThemes("")

        // Then
        assertEquals(themeList, themes)
    }

    @Test
    fun `When loading with query Then filter out by name`() = runTest {
        // Given
        val themeList = AssetsTheme.entries.map(ThemeMapper::toModel).take(1)

        // When
        val themes = themesRepository.loadThemes("Darcula")

        // Then
        assertEquals(themeList, themes)
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
        // coVerify(exactly = 1) { themeDao.delete(themeModel.uuid) }
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
        // coVerify(exactly = 1) { themeDao.delete(themeModel.uuid) }
        verify(exactly = 1) { settingsManager.remove(KEY_EDITOR_THEME) }
    }
}