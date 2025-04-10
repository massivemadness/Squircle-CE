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

package com.blacksquircle.ui.feature.explorer.manager

import com.blacksquircle.ui.feature.explorer.data.manager.TaskAction
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskManagerTest {

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val dispatcherProvider = TestDispatcherProvider()
    private val testDispatcher = dispatcherProvider.testDispatcher

    private val taskManager = TaskManager(dispatcherProvider)

    @Test
    fun `When executing a task Then task is done`() = runTest(testDispatcher) {
        // Given
        val action: TaskAction = { update ->
            delay(100)
            update(TaskStatus.Done)
        }

        // When
        val taskId = taskManager.execute(TaskType.CREATE, action)
        val taskMonitor = taskManager.monitor(taskId)
        advanceUntilIdle()

        // Then
        val taskStatus = taskMonitor.value.status
        assertEquals(TaskStatus.Done, taskStatus)
        assertTrue(taskManager.isIdle())
    }

    @Test
    fun `When executing a task Then task is failed`() = runTest {
        // Given
        val action: TaskAction = {
            throw RuntimeException("Something went wrong")
        }

        // When
        val taskId = taskManager.execute(TaskType.RENAME, action)
        val taskMonitor = taskManager.monitor(taskId)
        advanceUntilIdle()

        // Then
        val taskStatus = taskMonitor.value.status
        assertTrue(taskStatus is TaskStatus.Error)
        assertTrue(taskManager.isIdle())
    }

    @Test
    fun `When cancelling a task Then task is removed`() = runTest {
        // Given
        val action: TaskAction = {
            delay(1000)
        }
        val taskId = taskManager.execute(TaskType.DELETE, action)
        advanceTimeBy(10)

        // When
        taskManager.cancel(taskId)

        // Then
        assertTrue(taskManager.isIdle())
    }

    @Test
    fun `When monitoring non existing task Then return Error`() = runTest {
        // Given
        val taskId = "12345"

        // When
        val task = taskManager.monitor(taskId).value

        // Then
        assertTrue(task.status is TaskStatus.Error)
        assertEquals(taskId, task.id)
    }
}