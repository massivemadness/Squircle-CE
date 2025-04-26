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

package com.blacksquircle.ui

import android.content.Intent
import android.net.Uri
import com.blacksquircle.ui.application.MainViewModel
import com.blacksquircle.ui.application.MainViewState
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_EDITOR_THEME
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_FULLSCREEN_MODE
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.themes.api.interactor.ThemeInteractor
import com.blacksquircle.ui.feature.themes.api.model.ColorScheme
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val themeInteractor = mockk<ThemeInteractor>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)

    private val colorScheme = mockk<ColorScheme>(relaxed = true)

    @Test
    fun `When screen opens Then subscribe to preference changes`() = runTest {
        // When
        createViewModel() // init {}

        // Then
        verify(exactly = 1) { settingsManager.registerListener(KEY_EDITOR_THEME, any()) }
        verify(exactly = 1) { settingsManager.registerListener(KEY_FULLSCREEN_MODE, any()) }
    }

    @Test
    fun `When screen opens Then load settings`() = runTest {
        // Given
        every { settingsManager.editorTheme } returns "darcula"
        every { settingsManager.fullScreenMode } returns true

        coEvery { themeInteractor.loadTheme(any()) } coAnswers {
            delay(200)
            colorScheme
        }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = MainViewState(
            isLoading = true,
            colorScheme = null,
            fullscreenMode = true,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When empty intent received Then do nothing`() = runTest {
        // Given
        val intent = mockk<Intent>().apply {
            every { data } returns null
        }
        val viewModel = createViewModel()

        // When
        viewModel.onNewIntent(intent)

        // Then
        coVerify(exactly = 0) { editorInteractor.openFileUri(any()) }
    }

    @Test
    fun `When intent with data received Then open file uri`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val intent = mockk<Intent>().apply {
            every { data } returns uri
        }
        val viewModel = createViewModel()

        // When
        viewModel.onNewIntent(intent)

        // Then
        coVerify(exactly = 1) { editorInteractor.openFileUri(uri) }
    }

    private fun createViewModel(): MainViewModel {
        return MainViewModel(
            settingsManager = settingsManager,
            themeInteractor = themeInteractor,
            editorInteractor = editorInteractor,
        )
    }
}