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
import com.blacksquircle.ui.feature.settings.ui.application.AppHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.application.AppHeaderViewState
import com.blacksquircle.ui.feature.themes.api.navigation.ThemesScreen
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

class AppHeaderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val settingsManager = mockk<SettingsManager>(relaxed = true)

    @Test
    fun `When screen opens Then read settings`() = runTest {
        // Given
        every { settingsManager.fullScreenMode } returns true
        every { settingsManager.confirmExit } returns true

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = AppHeaderViewState(
            fullscreenMode = true,
            confirmExit = true,
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
    fun `When color scheme clicked Then open themes screen`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onColorSchemeClicked()

        // Then
        val expected = ViewEvent.Navigation(ThemesScreen)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When fullscreen changed Then update view state`() = runTest {
        // Given
        every { settingsManager.fullScreenMode } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onFullscreenChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.fullscreenMode)
        verify(exactly = 1) { settingsManager.fullScreenMode = false }
    }

    @Test
    fun `When confirm exit changed Then update view state`() = runTest {
        // Given
        every { settingsManager.confirmExit } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onConfirmExitChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.confirmExit)
        verify(exactly = 1) { settingsManager.confirmExit = false }
    }

    private fun createViewModel(): AppHeaderViewModel {
        return AppHeaderViewModel(settingsManager)
    }
}