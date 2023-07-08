/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.fonts

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.mvi.FontIntent
import com.blacksquircle.ui.feature.fonts.ui.mvi.FontsViewState
import com.blacksquircle.ui.feature.fonts.ui.viewmodel.FontsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FontUiStateTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val fontsRepository = mockk<FontsRepository>()

    @Test
    fun `When opening the screen Then display loading state`() = runTest {
        // Given
        coEvery { fontsRepository.loadFonts() } coAnswers { delay(200); emptyList() }

        // When
        val viewModel = fontsViewModel()
        viewModel.obtainEvent(FontIntent.LoadFonts)

        // Then
        val fontsViewState = FontsViewState.Loading
        assertEquals(fontsViewState, viewModel.fontsState.value)
    }

    @Test
    fun `When user has no fonts in database Then display empty state`() = runTest {
        // Given
        coEvery { fontsRepository.loadFonts() } returns emptyList()

        // When
        val viewModel = fontsViewModel()
        viewModel.obtainEvent(FontIntent.LoadFonts)

        // Then
        val fontsViewState = FontsViewState.Empty("")
        assertEquals(fontsViewState, viewModel.fontsState.value)
    }

    @Test
    fun `When user has fonts in database Then display font list`() = runTest {
        val fontList = listOf(
            FontModel(
                fontUuid = "1",
                fontName = "Droid Sans Mono",
                fontPath = "/android_asset/droid_sans_mono.ttf",
                isExternal = true,
            ),
        )

        // Given
        coEvery { fontsRepository.loadFonts() } returns fontList

        // When
        val viewModel = fontsViewModel()
        viewModel.obtainEvent(FontIntent.LoadFonts)

        // Then
        val fontsViewState = FontsViewState.Data("", fontList)
        assertEquals(fontsViewState, viewModel.fontsState.value)
    }

    @Test
    fun `When user types in search bar Then update font list`() = runTest {
        val fontList = listOf(
            FontModel(
                fontUuid = "1",
                fontName = "Droid Sans Mono",
                fontPath = "/android_asset/droid_sans_mono.ttf",
                isExternal = true,
            ),
            FontModel(
                fontUuid = "2",
                fontName = "Source Code Pro",
                fontPath = "/android_asset/source_code_pro.ttf",
                isExternal = true,
            ),
        )

        // Given
        coEvery { fontsRepository.loadFonts() } returns fontList
        coEvery { fontsRepository.loadFonts(any()) } coAnswers {
            fontList.filter { it.fontName.contains(firstArg<String>()) }
        }

        // When
        val viewModel = fontsViewModel()
        viewModel.obtainEvent(FontIntent.SearchFonts("Source"))

        // Then
        val fontsViewState = FontsViewState.Data("Source", fontList.drop(1))
        assertEquals(fontsViewState, viewModel.fontsState.value)
    }

    private fun fontsViewModel(): FontsViewModel {
        return FontsViewModel(
            stringProvider = stringProvider,
            fontsRepository = fontsRepository
        )
    }
}