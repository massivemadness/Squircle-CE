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
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import com.blacksquircle.ui.feature.git.ui.checkout.CheckoutViewModel
import com.blacksquircle.ui.feature.git.ui.checkout.CheckoutViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CheckoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

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
    fun `When screen opens Then load branches`() = runTest {
        // Given
        val branches = listOf("master", "develop")
        coEvery { gitRepository.currentBranch(any()) } returns branches[0]
        coEvery { gitRepository.branchList(any()) } returns branches

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = CheckoutViewState(
            currentBranch = branches[0],
            branchList = branches,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { gitRepository.currentBranch(any()) }
        coVerify(exactly = 1) { gitRepository.branchList(any()) }
    }

    @Test
    fun `When branch selected Then update current branch`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onBranchSelected("develop")

        // Then
        val viewState = CheckoutViewState(
            currentBranch = "develop",
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When branch name changed Then update branch name`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onNewBranchClicked()
        viewModel.onBranchNameChanged("develop")

        // Then
        val viewState = CheckoutViewState(
            newBranchName = "develop",
            isNewBranch = true,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When new branch clicked Then toggle new branch flag`() = runTest {
        // Given
        coEvery { gitRepository.currentBranch(any()) } returns "master"
        val viewModel = createViewModel()

        // When
        viewModel.onNewBranchClicked()

        // Then
        val viewState = CheckoutViewState(
            currentBranch = "master",
            isNewBranch = true,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When checkout clicked Then checkout branch`() = runTest {
        // Given
        coEvery { gitRepository.currentBranch(any()) } returns "master"
        val viewModel = createViewModel()

        // When
        viewModel.onBranchSelected("develop")
        viewModel.onCheckoutClicked()

        // Then
        val viewState = CheckoutViewState(
            currentBranch = "develop",
            isChecking = true,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { gitRepository.checkout(any(), "develop") }
    }

    @Test
    fun `When checkout clicked Then checkout new branch`() = runTest {
        // Given
        coEvery { gitRepository.currentBranch(any()) } returns "master"
        val viewModel = createViewModel()

        // When
        viewModel.onNewBranchClicked()
        viewModel.onBranchNameChanged("develop")
        viewModel.onCheckoutClicked()

        // Then
        val viewState = CheckoutViewState(
            currentBranch = "master",
            isNewBranch = true,
            newBranchName = "develop",
            isChecking = true,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { gitRepository.checkoutNew(any(), "develop", "master") }
    }

    private fun createViewModel(): CheckoutViewModel {
        return CheckoutViewModel(
            gitRepository = gitRepository,
            repository = REPOSITORY,
        )
    }

    companion object {
        private const val REPOSITORY = "/.git"
    }
}