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

package com.blacksquircle.ui.feature.settings

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.settings.ui.codestyle.CodeHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.codestyle.CodeHeaderViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CodeHeaderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val settingsManager = mockk<SettingsManager>(relaxed = true)

    @Test
    fun `When screen opens Then read settings`() = runTest {
        // Given
        every { settingsManager.autoIndentation } returns true
        every { settingsManager.autoClosePairs } returns true
        every { settingsManager.useSpacesInsteadOfTabs } returns false
        every { settingsManager.tabWidth } returns 8

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = CodeHeaderViewState(
            autoIndentation = true,
            autoClosePairs = true,
            useSpacesInsteadOfTabs = false,
            tabWidth = 8,
        )
        assertEquals(viewState, viewModel.viewState.value)
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
    fun `When auto indentation changed Then update view state`() = runTest {
        // Given
        every { settingsManager.autoIndentation } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onAutoIndentChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.autoIndentation)
        verify(exactly = 1) { settingsManager.autoIndentation = false }
    }

    @Test
    fun `When auto close pairs changed Then update view state`() = runTest {
        // Given
        every { settingsManager.autoClosePairs } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onAutoClosePairsChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.autoClosePairs)
        verify(exactly = 1) { settingsManager.autoClosePairs = false }
    }

    @Test
    fun `When use spaces changed Then update view state`() = runTest {
        // Given
        every { settingsManager.useSpacesInsteadOfTabs } returns false andThen true

        // When
        val viewModel = createViewModel()
        viewModel.onUseSpacesChanged(true)

        // Then
        assertEquals(true, viewModel.viewState.value.useSpacesInsteadOfTabs)
        verify(exactly = 1) { settingsManager.useSpacesInsteadOfTabs = true }
    }

    @Test
    fun `When tab width changed Then update view state`() = runTest {
        // Given
        every { settingsManager.tabWidth } returns 8 andThen 4

        // When
        val viewModel = createViewModel()
        viewModel.onTabWidthChanged(4)

        // Then
        assertEquals(4, viewModel.viewState.value.tabWidth)
        verify(exactly = 1) { settingsManager.tabWidth = 4 }
    }

    private fun createViewModel(): CodeHeaderViewModel {
        return CodeHeaderViewModel(settingsManager)
    }
}