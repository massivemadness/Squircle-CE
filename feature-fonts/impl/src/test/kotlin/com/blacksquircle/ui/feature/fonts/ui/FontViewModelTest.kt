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

package com.blacksquircle.ui.feature.fonts.ui

import android.graphics.Typeface
import android.net.Uri
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.fonts.FontsViewEvent
import com.blacksquircle.ui.feature.fonts.ui.fonts.FontsViewModel
import com.blacksquircle.ui.feature.fonts.ui.fonts.FontsViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FontViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val fontsRepository = mockk<FontsRepository>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val typeface = mockk<Typeface>()

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
        coEvery { fontsRepository.loadFonts("") } coAnswers { delay(200); emptyList() }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = FontsViewState(
            searchQuery = "",
            fonts = emptyList(),
            isLoading = true
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has no fonts in database Then display empty state`() = runTest {
        // Given
        coEvery { fontsRepository.loadFonts("") } returns emptyList()

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = FontsViewState(
            searchQuery = "",
            fonts = emptyList(),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has fonts in database Then display font list`() = runTest {
        // Given
        val fontList = listOf(
            FontModel(
                uuid = "1",
                name = "Droid Sans Mono",
                typeface = typeface,
                isExternal = true,
            )
        )
        coEvery { fontsRepository.loadFonts("") } returns fontList

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = FontsViewState(
            searchQuery = "",
            fonts = fontList,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user typing in search bar Then update font list`() = runTest {
        // Given
        val fontList = listOf(
            FontModel(
                uuid = "1",
                name = "Droid Sans Mono",
                typeface = typeface,
                isExternal = true,
            ),
            FontModel(
                uuid = "2",
                name = "Source Code Pro",
                typeface = typeface,
                isExternal = true,
            ),
        )
        coEvery { fontsRepository.loadFonts("") } returns fontList
        coEvery { fontsRepository.loadFonts(any()) } coAnswers {
            fontList.filter { it.name.contains(firstArg<String>()) }
        }

        // When
        val viewModel = createViewModel()
        viewModel.onQueryChanged("Source")
        advanceUntilIdle()

        // Then
        val viewState = FontsViewState(
            searchQuery = "Source",
            fonts = fontList.drop(1),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user clearing search query Then reload font list`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onQueryChanged("Source")
        advanceUntilIdle()
        viewModel.onClearQueryClicked()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { fontsRepository.loadFonts("Source") }
        coVerify(exactly = 2) { fontsRepository.loadFonts("") }
    }

    @Test
    fun `When import clicked Then send font selection event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onImportClicked()

        // Then
        val expected = FontsViewEvent.ChooseFont
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When font file selected Then import font`() = runTest {
        // Given
        val fontUri = mockk<Uri>()
        val viewModel = createViewModel()

        // When
        viewModel.onFontLoaded(fontUri)

        // Then
        coVerify(exactly = 1) { fontsRepository.importFont(fontUri) }
    }

    @Test
    fun `When font selected Then save selection`() = runTest {
        // Given
        val fontModel = FontModel(
            uuid = "1",
            name = "Droid Sans Mono",
            typeface = typeface,
            isExternal = false,
        )
        val viewModel = createViewModel()

        // When
        viewModel.onSelectClicked(fontModel)

        // Then
        val viewState = FontsViewState(selectedFont = fontModel.uuid)
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 1) { fontsRepository.selectFont(fontModel) }
    }

    @Test
    fun `When font removed Then remove font`() = runTest {
        // Given
        val fontModel = FontModel(
            uuid = "1",
            name = "Droid Sans Mono",
            typeface = typeface,
            isExternal = false,
        )
        coEvery { fontsRepository.loadFonts(any()) } returns listOf(fontModel)
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onRemoveClicked(fontModel)

        // Then
        val viewState = FontsViewState(
            fonts = emptyList(),
            selectedFont = "",
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 1) { fontsRepository.removeFont(fontModel) }
    }

    private fun createViewModel(): FontsViewModel {
        return FontsViewModel(
            stringProvider = stringProvider,
            fontsRepository = fontsRepository,
            settingsManager = settingsManager,
        )
    }
}