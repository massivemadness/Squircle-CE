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

package com.blacksquircle.ui.feature.changelog

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.changelog.domain.model.ReleaseModel
import com.blacksquircle.ui.feature.changelog.domain.repository.ChangelogRepository
import com.blacksquircle.ui.feature.changelog.ui.changelog.ChangeLogViewModel
import com.blacksquircle.ui.feature.changelog.ui.changelog.ChangeLogViewState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChangeLogViewModelTest {

    private val changelogRepository = mockk<ChangelogRepository>()

    @Test
    fun `When screen opens Then load changelog`() = runTest {
        // Given
        val changelog = emptyList<ReleaseModel>()
        coEvery { changelogRepository.loadChangelog() } returns changelog

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = ChangeLogViewState(changelog)
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 1) { changelogRepository.loadChangelog() }
    }

    @Test
    fun `When back pressed Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onBackClicked()

        // Then
        val expected = ViewEvent.PopBackStack
        assertEquals(expected, viewModel.viewEvent.first())
    }

    private fun createViewModel(): ChangeLogViewModel {
        return ChangeLogViewModel(changelogRepository)
    }
}