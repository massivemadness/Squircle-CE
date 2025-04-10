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

package com.blacksquircle.ui.feature.explorer.ui

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.explorer.api.navigation.NotificationDeniedDialog
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.domain.model.Task
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewEvent
import com.blacksquircle.ui.feature.explorer.ui.task.TaskViewModel
import com.blacksquircle.ui.feature.explorer.ui.task.TaskViewState
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class TaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val task = Task(
        id = "12345",
        type = TaskType.CREATE,
        status = TaskStatus.Pending
    )

    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val taskMonitor = MutableStateFlow(task)

    @Before
    fun setup() {
        every { taskManager.monitor(task.id) } returns taskMonitor
    }

    @Test
    fun `When screen opens Then monitor task state`() = runTest {
        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = TaskViewState(
            type = task.type,
            timestamp = task.timestamp,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When task status changes Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        taskMonitor.update {
            it.copy(
                status = TaskStatus.Progress(
                    count = 1,
                    totalCount = 1,
                    details = "/storage/emulated/0/Documents/untitled.txt",
                )
            )
        }

        // Then
        val viewState = TaskViewState(
            type = task.type,
            count = 1,
            totalCount = 1,
            details = "/storage/emulated/0/Documents/untitled.txt",
            timestamp = task.timestamp,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When task fails Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()
        val exception = RuntimeException("Something went wrong")

        // When
        taskMonitor.update {
            it.copy(status = TaskStatus.Error(exception))
        }

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
    }

    @Test
    fun `When task finishes Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        taskMonitor.update {
            it.copy(status = TaskStatus.Done)
        }

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
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
    fun `When run in background clicked Then send StartService event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onRunInBackgroundClicked()

        // Then
        assertEquals(ExplorerViewEvent.StartService, viewModel.viewEvent.first())
    }

    @Test
    fun `When permission denied Then open notification denied dialog`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPermissionDenied()

        // Then
        val expected = ViewEvent.Navigation(NotificationDeniedDialog)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When permission granted Then send StartService event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPermissionGranted()

        // Then
        assertEquals(ExplorerViewEvent.StartService, viewModel.viewEvent.first())
    }

    @Test
    fun `When cancel clicked Then cancel task`() = runTest {
        // Given
        val viewModel = createViewModel()
        every { taskManager.cancel(task.id) } answers {
            taskMonitor.update {
                val exception = CancellationException()
                it.copy(status = TaskStatus.Error(exception))
            }
        }

        // When
        viewModel.onCancelClicked()

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
        verify(exactly = 1) { taskManager.cancel(task.id) }
    }

    private fun createViewModel(): TaskViewModel {
        return TaskViewModel(taskManager, task.id)
    }
}