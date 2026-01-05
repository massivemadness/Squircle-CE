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

import android.net.Uri
import app.cash.turbine.test
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.createFontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsAction
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsState
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.middleware.FontsMiddleware
import com.blacksquircle.ui.navigation.api.Navigator
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FontsMiddlewareTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val fontsRepository = mockk<FontsRepository>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val navigator = mockk<Navigator>(relaxed = true)

    private val fontsMiddleware = FontsMiddleware(
        fontsRepository = fontsRepository,
        settingsManager = settingsManager,
        navigator = navigator
    )

    private val state = MutableStateFlow(FontsState())
    private val actions = MutableSharedFlow<FontsAction>()

    @Test
    fun `When screen opens Then load fonts`() = runTest {
        // Given
        val fonts = listOf(
            createFontModel(uuid = "1"),
            createFontModel(uuid = "2"),
            createFontModel(uuid = "3"),
        )
        val selectedUuid = fonts.first().uuid

        coEvery { fontsRepository.loadFonts("") } returns fonts
        every { settingsManager.fontType } returns selectedUuid

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(FontsAction.Init)

            // Then
            assertEquals(FontsAction.CommandAction.FontsLoaded(fonts, selectedUuid), awaitItem())
            coVerify(exactly = 1) { fontsRepository.loadFonts("") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When back clicked Then return to previous screen`() = runTest {
        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(FontsAction.UiAction.OnBackClicked)

            // Then
            verify(exactly = 1) { navigator.goBack() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When select clicked Then update selected uuid`() = runTest {
        // Given
        val fontModel = createFontModel(uuid = "1")

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(FontsAction.UiAction.OnSelectClicked(fontModel))

            // Then
            assertEquals(FontsAction.CommandAction.FontSelected(fontModel), awaitItem())
            coVerify(exactly = 1) { fontsRepository.selectFont(fontModel) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When remove clicked Then remove font`() = runTest {
        // Given
        val fontModel = createFontModel(uuid = "2")
        every { settingsManager.fontType } returns "1"

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(FontsAction.UiAction.OnRemoveClicked(fontModel))

            // Then
            assertEquals(FontsAction.CommandAction.FontRemoved(fontModel, "1"), awaitItem())
            coVerify(exactly = 1) { fontsRepository.removeFont(fontModel) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When font file selected Then import font and refresh list`() = runTest {
        // Given
        val fontUri = mockk<Uri>()

        fontsMiddleware.bind(state, actions).test {
            // When
            actions.emit(FontsAction.UiAction.OnImportFont(fontUri))

            // Then
            assertEquals(FontsAction.CommandAction.FontImported, awaitItem())
            assertEquals(FontsAction.Init, awaitItem())
            coVerify(exactly = 1) { fontsRepository.importFont(fontUri) }

            cancelAndIgnoreRemainingEvents()
        }
    }
}