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

package com.blacksquircle.ui.feature.themes.ui

import android.graphics.Typeface
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.provider.typeface.TypefaceProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemeRepository
import com.blacksquircle.ui.feature.themes.ui.themes.ThemesViewModel
import com.blacksquircle.ui.feature.themes.ui.themes.ThemesViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val fontsInteractor = mockk<FontsInteractor>(relaxed = true)
    private val themeRepository = mockk<ThemeRepository>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val typeface = mockk<Typeface>()

    @Before
    fun setup() {
        mockkObject(TypefaceProvider)
        every { TypefaceProvider.DEFAULT } returns typeface
        coEvery { fontsInteractor.loadFont(any()) } returns typeface
    }

    @Test
    fun `When back pressed Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onBackClicked()

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
    }

    @Test
    fun `When screen opens Then display loading state`() = runTest {
        // Given
        coEvery { themeRepository.loadThemes("") } coAnswers { delay(200); emptyList() }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = ThemesViewState(
            searchQuery = "",
            themes = emptyList(),
            isLoading = true,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has no themes in database Then display empty state`() = runTest {
        // Given
        coEvery { themeRepository.loadThemes("") } returns emptyList()

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = ThemesViewState(
            searchQuery = "",
            themes = emptyList(),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has themes in database Then display theme list`() = runTest {
        // Given
        val themeList = listOf(
            ThemeModel(
                uuid = "1",
                name = "Darcula",
                author = "Squircle CE",
                isExternal = true,
                colors = mockk()
            ),
        )
        coEvery { themeRepository.loadThemes("") } returns themeList

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = ThemesViewState(
            searchQuery = "",
            themes = themeList,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user typing in search bar Then update theme list`() = runTest {
        // Given
        val themeList = listOf(
            ThemeModel(
                uuid = "1",
                name = "Darcula",
                author = "Squircle CE",
                isExternal = true,
                colors = mockk()
            ),
            ThemeModel(
                uuid = "2",
                name = "Eclipse",
                author = "Squircle CE",
                isExternal = true,
                colors = mockk()
            ),
        )
        coEvery { themeRepository.loadThemes("") } returns themeList
        coEvery { themeRepository.loadThemes(any()) } coAnswers {
            themeList.filter { it.name.contains(firstArg<String>()) }
        }

        // When
        val viewModel = createViewModel()
        viewModel.onQueryChanged("Eclipse")
        advanceUntilIdle()

        // Then
        val viewState = ThemesViewState(
            searchQuery = "Eclipse",
            themes = themeList.drop(1),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user clearing search query Then reload theme list`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onQueryChanged("Source")
        advanceUntilIdle()
        viewModel.onClearQueryClicked()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { themeRepository.loadThemes("Source") }
        coVerify(exactly = 2) { themeRepository.loadThemes("") }
    }

    @Test
    fun `When theme selected Then save selection`() = runTest {
        // Given
        val themeModel = ThemeModel(
            uuid = "1",
            name = "Darcula",
            author = "Squircle CE",
            colors = mockk(),
            isExternal = true,
        )
        val viewModel = createViewModel()

        // When
        viewModel.onSelectClicked(themeModel)

        // Then
        val viewState = ThemesViewState(selectedTheme = themeModel.uuid)
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 1) { themeRepository.selectTheme(themeModel) }
    }

    @Test
    fun `When theme removed Then remove theme`() = runTest {
        // Given
        val themeModel = ThemeModel(
            uuid = "1",
            name = "Darcula",
            author = "Squircle CE",
            colors = mockk(),
            isExternal = true,
        )
        coEvery { themeRepository.loadThemes(any()) } returns listOf(themeModel)
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onRemoveClicked(themeModel)

        // Then
        val viewState = ThemesViewState(
            themes = emptyList(),
            selectedTheme = "",
            typeface = typeface,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 1) { themeRepository.removeTheme(themeModel) }
    }

    private fun createViewModel(): ThemesViewModel {
        return ThemesViewModel(
            stringProvider = stringProvider,
            fontsInteractor = fontsInteractor,
            themeRepository = themeRepository,
            settingsManager = settingsManager,
        )
    }
}