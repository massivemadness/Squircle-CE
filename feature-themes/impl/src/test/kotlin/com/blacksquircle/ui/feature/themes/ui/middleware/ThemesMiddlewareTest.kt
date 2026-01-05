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
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.themes.createThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemeRepository
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesAction
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesState
import com.blacksquircle.ui.feature.themes.ui.themes.store.middleware.ThemesMiddleware
import com.blacksquircle.ui.navigation.api.Navigator
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ThemesMiddlewareTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val fontsInteractor = mockk<FontsInteractor>(relaxed = true)
    private val themeRepository = mockk<ThemeRepository>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val navigator = mockk<Navigator>(relaxed = true)
    private val typeface = mockk<Typeface>()

    private val fontsMiddleware = ThemesMiddleware(
        fontsInteractor = fontsInteractor,
        themeRepository = themeRepository,
        settingsManager = settingsManager,
        navigator = navigator
    )

    private lateinit var state: MutableStateFlow<ThemesState>
    private lateinit var actions: MutableSharedFlow<ThemesAction>

    @Before
    fun setup() {
        mockkObject(TypefaceProvider)
        every { TypefaceProvider.DEFAULT } returns typeface
        coEvery { fontsInteractor.loadFont(any()) } returns typeface

        state = MutableStateFlow(ThemesState())
        actions = MutableSharedFlow()
    }

    @Test
    fun `When screen opens Then load themes`() = runTest {
        // Given
        val themes = listOf(
            createThemeModel(uuid = "1"),
            createThemeModel(uuid = "2"),
            createThemeModel(uuid = "3"),
        )
        val selectedUuid = themes.first().uuid

        coEvery { themeRepository.loadThemes("") } returns themes
        every { settingsManager.editorTheme } returns selectedUuid

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(ThemesAction.OnInit)

            // Then
            val expected = ThemesAction.OnThemesLoaded(themes, selectedUuid, state.value.typeface)
            assertEquals(expected, awaitItem())
            coVerify(exactly = 1) { themeRepository.loadThemes("") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When back clicked Then return to previous screen`() = runTest {
        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(ThemesAction.OnBackClicked)

            // Then
            verify(exactly = 1) { navigator.goBack() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When select clicked Then update selected uuid`() = runTest {
        // Given
        val themeModel = createThemeModel(uuid = "1")

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(ThemesAction.OnSelectClicked(themeModel))

            // Then
            assertEquals(ThemesAction.OnThemeSelected(themeModel), awaitItem())
            coVerify(exactly = 1) { themeRepository.selectTheme(themeModel) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When remove clicked Then remove theme`() = runTest {
        // Given
        val themeModel = createThemeModel(uuid = "2")
        every { settingsManager.editorTheme } returns "1"

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(ThemesAction.OnRemoveClicked(themeModel))

            // Then
            assertEquals(ThemesAction.OnThemeRemoved(themeModel, "1"), awaitItem())
            coVerify(exactly = 1) { themeRepository.removeTheme(themeModel) }

            cancelAndIgnoreRemainingEvents()
        }
    }
}