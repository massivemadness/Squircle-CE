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

package com.blacksquircle.ui.feature.fonts.ui.middleware

import app.cash.turbine.test
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.createFontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsAction
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsState
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.middleware.FontsSearchMiddleware
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FontsSearchMiddlewareTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val fontsRepository = mockk<FontsRepository>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)

    private val fontsMiddleware = FontsSearchMiddleware(
        fontsRepository = fontsRepository,
        settingsManager = settingsManager,
    )

    private val state = MutableStateFlow(FontsState())
    private val actions = MutableSharedFlow<FontsAction>()

    @Test
    fun `When user typing in search bar Then update font list`() = runTest {
        // Given
        val query = "qwerty"
        val fonts = listOf(
            createFontModel(uuid = "1"),
            createFontModel(uuid = "2"),
        )
        val selectedUuid = fonts.last().uuid

        coEvery { fontsRepository.loadFonts(query) } returns fonts
        every { settingsManager.fontType } returns selectedUuid

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(FontsAction.UiAction.OnQueryChanged(query))

            // Then
            assertEquals(FontsAction.CommandAction.FontsLoaded(fonts, selectedUuid), awaitItem())
            coVerify(exactly = 1) { fontsRepository.loadFonts(query) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When user clearing search query Then reload font list`() = runTest {
        // Given
        val query = "qwerty"
        val fonts = listOf(
            createFontModel(uuid = "1"),
            createFontModel(uuid = "2"),
        )
        val selectedUuid = fonts.last().uuid

        coEvery { fontsRepository.loadFonts(query) } returns fonts
        every { settingsManager.fontType } returns selectedUuid

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(FontsAction.UiAction.OnQueryChanged(query))
            actions.emit(FontsAction.UiAction.OnClearQueryClicked)

            // Then
            coVerify(exactly = 1) { fontsRepository.loadFonts(query) }
            coVerify(exactly = 1) { fontsRepository.loadFonts("") }

            cancelAndIgnoreRemainingEvents()
        }
    }
}