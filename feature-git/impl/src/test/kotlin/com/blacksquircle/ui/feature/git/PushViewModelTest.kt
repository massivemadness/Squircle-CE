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
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.git.domain.exception.GitException
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import com.blacksquircle.ui.feature.git.ui.push.PushViewModel
import com.blacksquircle.ui.feature.git.ui.push.PushViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PushViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val gitRepository = mockk<GitRepository>(relaxed = true)

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
    fun `When screen opens Then load branch and commits`() = runTest {
        // Given
        val branch = "master"
        coEvery { gitRepository.currentBranch(any()) } returns branch
        coEvery { gitRepository.commitCount(any()) } returns 2

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = PushViewState(
            currentBranch = branch,
            commitCount = 2,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { gitRepository.currentBranch(any()) }
        coVerify(exactly = 1) { gitRepository.commitCount(any()) }
    }

    @Test
    fun `When user has no local commits Then display error`() = runTest {
        // Given
        coEvery { gitRepository.commitCount(any()) } returns 0
        every { stringProvider.getString(any()) } returns "error message"

        // When
        val viewModel = createViewModel() // init {}

        // Then
        assertEquals(true, viewModel.viewState.value.isError)
    }

    @Test
    fun `When force clicked Then toggle force flag`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onForceClicked()

        // Then
        assertEquals(true, viewModel.viewState.value.isForce)
    }

    @Test
    fun `When push clicked Then push commits`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPushClicked()

        // Then
        assertEquals(true, viewModel.viewState.value.isPushing)

        coVerify(exactly = 1) { gitRepository.push(REPOSITORY, false) }
    }

    @Test
    fun `When push with force clicked Then force push commits`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onForceClicked()
        viewModel.onPushClicked()

        // Then
        assertEquals(true, viewModel.viewState.value.isPushing)

        coVerify(exactly = 1) { gitRepository.push(REPOSITORY, true) }
    }

    @Test
    fun `When push failed Then display error message`() = runTest {
        // Given
        val viewModel = createViewModel()
        coEvery { gitRepository.push(any(), any()) } throws GitException("Error")

        // When
        viewModel.onPushClicked()

        // Then
        assertEquals(true, viewModel.viewState.value.isError)
    }

    private fun createViewModel(): PushViewModel {
        return PushViewModel(
            stringProvider = stringProvider,
            gitRepository = gitRepository,
            repository = REPOSITORY,
        )
    }

    companion object {
        private const val REPOSITORY = "/.git"
    }
}