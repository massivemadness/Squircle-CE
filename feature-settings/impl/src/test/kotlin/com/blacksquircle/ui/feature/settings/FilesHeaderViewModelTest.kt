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
import com.blacksquircle.ui.feature.settings.ui.files.FilesHeaderViewEvent
import com.blacksquircle.ui.feature.settings.ui.files.FilesHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.files.FilesHeaderViewState
import com.blacksquircle.ui.filesystem.base.model.LineBreak
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
import java.nio.charset.Charset

class FilesHeaderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val settingsManager = mockk<SettingsManager>(relaxed = true)

    @Test
    fun `When screen opens Then read settings`() = runTest {
        // Given
        every { settingsManager.encodingAutoDetect } returns false
        every { settingsManager.encodingForOpening } returns Charsets.UTF_8.name()
        every { settingsManager.encodingForSaving } returns Charsets.UTF_8.name()
        every { settingsManager.lineBreakForSaving } returns LineBreak.LF.value
        every { settingsManager.showHidden } returns true
        every { settingsManager.compactPackages } returns true
        every { settingsManager.foldersOnTop } returns true
        every { settingsManager.sortMode } returns "sort_by_name"

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = FilesHeaderViewState(
            encodingAutoDetect = false,
            encodingForOpening = Charsets.UTF_8.name(),
            encodingForSaving = Charsets.UTF_8.name(),
            encodingList = Charset.availableCharsets()
                .map(Map.Entry<String, Charset>::key),
            lineBreakForSaving = LineBreak.LF.value,
            showHidden = true,
            compactPackages = true,
            foldersOnTop = true,
            sortMode = "sort_by_name",
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
    fun `When storage access clicked Then open storage settings`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onStorageAccessClicked()

        // Then
        assertEquals(FilesHeaderViewEvent.OpenStorageSettings, viewModel.viewEvent.first())
    }

    @Test
    fun `When encoding auto detect changed Then update view state`() = runTest {
        // Given
        every { settingsManager.encodingAutoDetect } returns false andThen true

        // When
        val viewModel = createViewModel()
        viewModel.onEncodingAutoDetectChanged(true)

        // Then
        assertEquals(true, viewModel.viewState.value.encodingAutoDetect)
        verify(exactly = 1) { settingsManager.encodingAutoDetect = true }
    }

    @Test
    fun `When encoding for opening changed Then update view state`() = runTest {
        // Given
        every { settingsManager.encodingForOpening } returns
            Charsets.UTF_8.name() andThen Charsets.UTF_16.name()

        // When
        val viewModel = createViewModel()
        viewModel.onEncodingForOpeningChanged(Charsets.UTF_16.name())

        // Then
        assertEquals(Charsets.UTF_16.name(), viewModel.viewState.value.encodingForOpening)
        verify(exactly = 1) { settingsManager.encodingForOpening = Charsets.UTF_16.name() }
    }

    @Test
    fun `When encoding for saving changed Then update view state`() = runTest {
        // Given
        every { settingsManager.encodingForSaving } returns
            Charsets.UTF_8.name() andThen Charsets.UTF_16.name()

        // When
        val viewModel = createViewModel()
        viewModel.onEncodingForSavingChanged(Charsets.UTF_16.name())

        // Then
        assertEquals(Charsets.UTF_16.name(), viewModel.viewState.value.encodingForSaving)
        verify(exactly = 1) { settingsManager.encodingForSaving = Charsets.UTF_16.name() }
    }

    @Test
    fun `When linebreak for saving changed Then update view state`() = runTest {
        // Given
        every { settingsManager.lineBreakForSaving } returns
            LineBreak.LF.value andThen LineBreak.CRLF.value

        // When
        val viewModel = createViewModel()
        viewModel.onLineBreakForSavingChanged(LineBreak.CRLF.value)

        // Then
        assertEquals(LineBreak.CRLF.value, viewModel.viewState.value.lineBreakForSaving)
        verify(exactly = 1) { settingsManager.lineBreakForSaving = LineBreak.CRLF.value }
    }

    @Test
    fun `When show hidden changed Then update view state`() = runTest {
        // Given
        every { settingsManager.showHidden } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onShowHiddenChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.showHidden)
        verify(exactly = 1) { settingsManager.showHidden = false }
    }

    @Test
    fun `When compact packages changed Then update view state`() = runTest {
        // Given
        every { settingsManager.compactPackages } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onCompactPackagesChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.compactPackages)
        verify(exactly = 1) { settingsManager.compactPackages = false }
    }

    @Test
    fun `When folders on top changed Then update view state`() = runTest {
        // Given
        every { settingsManager.foldersOnTop } returns true andThen false

        // When
        val viewModel = createViewModel()
        viewModel.onFoldersOnTopChanged(false)

        // Then
        assertEquals(false, viewModel.viewState.value.foldersOnTop)
        verify(exactly = 1) { settingsManager.foldersOnTop = false }
    }

    @Test
    fun `When sort mode changed Then update view state`() = runTest {
        // Given
        every { settingsManager.sortMode } returns "sort_by_name" andThen "sort_by_date"

        // When
        val viewModel = createViewModel()
        viewModel.onSortModeChanged("sort_by_date")

        // Then
        assertEquals("sort_by_date", viewModel.viewState.value.sortMode)
        verify(exactly = 1) { settingsManager.sortMode = "sort_by_date" }
    }

    private fun createViewModel(): FilesHeaderViewModel {
        return FilesHeaderViewModel(settingsManager)
    }
}