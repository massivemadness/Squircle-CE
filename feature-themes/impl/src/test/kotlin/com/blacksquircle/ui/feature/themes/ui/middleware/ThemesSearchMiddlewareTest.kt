/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.themes.ui.middleware

import android.graphics.Typeface
import app.cash.turbine.test
import com.blacksquircle.ui.core.provider.typeface.TypefaceProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.themes.createThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemeRepository
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesAction
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesState
import com.blacksquircle.ui.feature.themes.ui.themes.store.middleware.ThemesSearchMiddleware
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ThemesSearchMiddlewareTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val themeRepository = mockk<ThemeRepository>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val typeface = mockk<Typeface>()

    private val fontsMiddleware = ThemesSearchMiddleware(
        themeRepository = themeRepository,
        settingsManager = settingsManager,
    )

    private lateinit var state: MutableStateFlow<ThemesState>
    private lateinit var actions: MutableSharedFlow<ThemesAction>

    @Before
    fun setup() {
        mockkObject(TypefaceProvider)
        every { TypefaceProvider.DEFAULT } returns typeface

        state = MutableStateFlow(ThemesState())
        actions = MutableSharedFlow()
    }

    @Test
    fun `When user typing in search bar Then update theme list`() = runTest {
        // Given
        val query = "qwerty"
        val themes = listOf(
            createThemeModel(uuid = "1"),
            createThemeModel(uuid = "2"),
        )
        val selectedUuid = themes.last().uuid

        coEvery { themeRepository.loadThemes(query) } returns themes
        every { settingsManager.editorTheme } returns selectedUuid

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(ThemesAction.UiAction.OnQueryChanged(query))

            // Then
            val expected = ThemesAction.CommandAction.ThemesLoaded(themes, selectedUuid, state.value.typeface)
            assertEquals(expected, awaitItem())
            coVerify(exactly = 1) { themeRepository.loadThemes(query) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When user clearing search query Then reload theme list`() = runTest {
        // Given
        val query = "qwerty"
        val themes = listOf(
            createThemeModel(uuid = "1"),
            createThemeModel(uuid = "2"),
        )
        val selectedUuid = themes.last().uuid

        coEvery { themeRepository.loadThemes(query) } returns themes
        every { settingsManager.editorTheme } returns selectedUuid

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(ThemesAction.UiAction.OnQueryChanged(query))
            actions.emit(ThemesAction.UiAction.OnClearQueryClicked)

            // Then
            coVerify(exactly = 1) { themeRepository.loadThemes(query) }
            coVerify(exactly = 1) { themeRepository.loadThemes("") }

            cancelAndIgnoreRemainingEvents()
        }
    }
}