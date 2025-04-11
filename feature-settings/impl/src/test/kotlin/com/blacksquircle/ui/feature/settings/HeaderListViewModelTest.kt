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
import com.blacksquircle.ui.feature.settings.api.navigation.AppHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.header.HeaderListViewModel
import com.blacksquircle.ui.feature.settings.ui.header.model.PreferenceHeader
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HeaderListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

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
    fun `When header clicked Then send popBackStack event`() = runTest {
        // Given
        val header = PreferenceHeader(
            title = R.string.pref_header_application_title,
            subtitle = R.string.pref_header_application_summary,
            selected = false,
            screen = AppHeaderScreen,
        )
        val viewModel = createViewModel()

        // When
        viewModel.onHeaderClicked(header)

        // Then
        val expected = ViewEvent.Navigation(header.screen)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    private fun createViewModel(): HeaderListViewModel {
        return HeaderListViewModel()
    }
}