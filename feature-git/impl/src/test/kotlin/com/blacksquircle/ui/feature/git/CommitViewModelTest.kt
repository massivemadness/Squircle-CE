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
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import com.blacksquircle.ui.feature.git.ui.commit.CommitViewModel
import com.blacksquircle.ui.feature.git.ui.commit.CommitViewState
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

class CommitViewModelTest {

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
    fun `When screen opens Then load git changes`() = runTest {
        // Given
        val changesList = listOf(createGitChange())
        coEvery { gitRepository.changesList(any()) } returns changesList

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = CommitViewState(
            changesList = changesList,
            selectedChanges = changesList,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When commit message changed Then update commit message`() = runTest {
        // Given
        val viewModel = createViewModel()
        val message = "Create untitled.txt"

        // When
        viewModel.onCommitMessageChanged(message)

        // Then
        val viewState = CommitViewState(
            commitMessage = message,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When amend clicked Then toggle amend flag`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onAmendClicked()

        // Then
        val viewState = CommitViewState(
            isAmend = true,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When selected change clicked Then remove it from list`() = runTest {
        // Given
        val changesList = listOf(
            createGitChange("file.txt"),
            createGitChange("untitled.txt"),
        )
        coEvery { gitRepository.changesList(any()) } returns changesList
        val viewModel = createViewModel()

        // When
        viewModel.onChangeSelected(changesList[0])

        // Then
        val viewState = CommitViewState(
            changesList = changesList,
            selectedChanges = listOf(changesList[1]),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When unselected change clicked Then add it to list`() = runTest {
        // Given
        val changesList = listOf(
            createGitChange("file.txt"),
            createGitChange("untitled.txt"),
        )
        coEvery { gitRepository.changesList(any()) } returns changesList
        val viewModel = createViewModel()

        // When
        viewModel.onChangeSelected(changesList[0]) // remove
        viewModel.onChangeSelected(changesList[0]) // add

        // Then
        val viewState = CommitViewState(
            changesList = changesList,
            selectedChanges = listOf(changesList[1], changesList[0]),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When commit clicked Then commit changes`() = runTest {
        // Given
        val changesList = listOf(
            createGitChange("file.txt"),
            createGitChange("untitled.txt"),
        )
        coEvery { gitRepository.changesList(any()) } returns changesList
        val viewModel = createViewModel()

        // When
        viewModel.onChangeSelected(changesList[0])
        viewModel.onCommitMessageChanged("Initial commit")
        viewModel.onCommitClicked()

        // Then
        val viewState = CommitViewState(
            changesList = changesList,
            selectedChanges = listOf(changesList[1]),
            commitMessage = "Initial commit",
            isLoading = false,
            isCommitting = true,
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) {
            gitRepository.commit(
                repository = any(),
                changes = listOf(changesList[1]),
                message = "Initial commit",
                isAmend = false
            )
        }
    }

    private fun createViewModel(): CommitViewModel {
        return CommitViewModel(
            stringProvider = stringProvider,
            gitRepository = gitRepository,
            repository = REPOSITORY,
        )
    }

    companion object {
        private const val REPOSITORY = "/.git"
    }
}