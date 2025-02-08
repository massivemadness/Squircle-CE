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

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.blacksquircle.ui.feature.themes.ui.fragment.ThemesViewState
import com.blacksquircle.ui.feature.themes.ui.viewmodel.ThemesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeUiStateTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val themesRepository = mockk<ThemesRepository>()
    private val settingsManager = mockk<SettingsManager>()

    @Before
    fun setup() {
        every { settingsManager.fontType } returns ""
        every { settingsManager.colorScheme } returns ""
    }

    @Test
    fun `When opening the screen Then display loading state`() = runTest {
        // Given
        coEvery { themesRepository.loadThemes("") } coAnswers { delay(200); emptyList() }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = ThemesViewState(
            query = "",
            themes = emptyList(),
            isLoading = true,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has no themes in database Then display empty state`() = runTest {
        // Given
        coEvery { themesRepository.loadThemes("") } returns emptyList()

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = ThemesViewState(
            query = "",
            themes = emptyList(),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has themes in database Then display theme list`() = runTest {
        val themeList = listOf(
            ThemeModel(
                uuid = "1",
                name = "Darcula",
                author = "Squircle CE",
                isExternal = true,
                colorScheme = mockk()
            ),
        )

        // Given
        coEvery { themesRepository.loadThemes("") } returns themeList

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = ThemesViewState(
            query = "",
            themes = themeList,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user types in search bar Then update theme list`() = runTest {
        val themeList = listOf(
            ThemeModel(
                uuid = "1",
                name = "Darcula",
                author = "Squircle CE",
                isExternal = true,
                colorScheme = mockk()
            ),
            ThemeModel(
                uuid = "2",
                name = "Eclipse",
                author = "Squircle CE",
                isExternal = true,
                colorScheme = mockk()
            ),
        )

        // Given
        coEvery { themesRepository.loadThemes("") } returns themeList
        coEvery { themesRepository.loadThemes(any()) } coAnswers {
            themeList.filter { it.name.contains(firstArg<String>()) }
        }

        // When
        val viewModel = createViewModel()
        viewModel.onQueryChanged("Eclipse")
        advanceUntilIdle()

        // Then
        val viewState = ThemesViewState(
            query = "Eclipse",
            themes = themeList.drop(1),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    private fun createViewModel(): ThemesViewModel {
        return ThemesViewModel(
            stringProvider = stringProvider,
            themesRepository = themesRepository,
            settingsManager = settingsManager,
        )
    }
}