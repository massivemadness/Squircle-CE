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

package com.blacksquircle.ui.feature.git

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog
import com.blacksquircle.ui.feature.git.api.navigation.CommitDialog
import com.blacksquircle.ui.feature.git.api.navigation.FetchDialog
import com.blacksquircle.ui.feature.git.api.navigation.PullDialog
import com.blacksquircle.ui.feature.git.api.navigation.PushDialog
import com.blacksquircle.ui.feature.git.ui.git.GitViewModel
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GitViewModelTest {

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
    fun `When fetch clicked Then open fetch dialog`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onFetchClicked()

        // Then
        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val event = ViewEvent.Navigation(FetchDialog(REPOSITORY))
        assertTrue(event in viewEvents)
    }

    @Test
    fun `When pull clicked Then open pull dialog`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPullClicked()

        // Then
        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val event = ViewEvent.Navigation(PullDialog(REPOSITORY))
        assertTrue(event in viewEvents)
    }

    @Test
    fun `When commit clicked Then open commit dialog`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onCommitClicked()

        // Then
        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val event = ViewEvent.Navigation(CommitDialog(REPOSITORY))
        assertTrue(event in viewEvents)
    }

    @Test
    fun `When push clicked Then open push dialog`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPushClicked()

        // Then
        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val event = ViewEvent.Navigation(PushDialog(REPOSITORY))
        assertTrue(event in viewEvents)
    }

    @Test
    fun `When checkout clicked Then open checkout dialog`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onCheckoutClicked()

        // Then
        val viewEvents = mutableListOf<ViewEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.viewEvent.toList(viewEvents)
        }

        val event = ViewEvent.Navigation(CheckoutDialog(REPOSITORY))
        assertTrue(event in viewEvents)
    }

    private fun createViewModel(): GitViewModel {
        return GitViewModel(REPOSITORY)
    }

    companion object {
        private const val REPOSITORY = "/.git"
    }
}