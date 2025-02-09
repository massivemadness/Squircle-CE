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

package com.blacksquircle.ui.feature.explorer.data.manager

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.explorer.domain.model.Task
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal typealias TaskAction = suspend (suspend (TaskStatus) -> Unit) -> Unit

internal class TaskManager(dispatcherProvider: DispatcherProvider) {

    private val jobs = ConcurrentHashMap<String, Job>()
    private val tasks = ConcurrentHashMap<String, MutableStateFlow<Task>>()
    private val scope = CoroutineScope(dispatcherProvider.io() + SupervisorJob())

    fun execute(taskType: TaskType, action: TaskAction): String {
        val taskId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val taskFlow = tasks.getOrPut(taskId) {
            val initialValue = Task(taskId, taskType, TaskStatus.Pending, timestamp)
            MutableStateFlow(initialValue)
        }
        jobs[taskId] = scope.launch {
            try {
                action { status ->
                    taskFlow.value = Task(taskId, taskType, status, timestamp)
                }
                taskFlow.value = Task(taskId, taskType, TaskStatus.Done, timestamp)
            } catch (e: Exception) {
                Timber.e(e, e.message)
                taskFlow.value = Task(taskId, taskType, TaskStatus.Error(e), timestamp)
            } finally {
                jobs.remove(taskId)
                tasks.remove(taskId)
            }
        }
        return taskId
    }

    fun monitor(taskId: String): StateFlow<Task> {
        return tasks.getOrElse(taskId) {
            val exception = IllegalArgumentException("Task with id $taskId not found")
            val initialValue = Task(taskId, TaskType.CREATE, TaskStatus.Error(exception))
            MutableStateFlow(initialValue)
        }.asStateFlow()
    }

    fun cancel(taskId: String) {
        jobs[taskId]?.cancel()
        jobs.remove(taskId)
        tasks.remove(taskId)
    }

    fun isIdle(): Boolean {
        return jobs.isEmpty() || tasks.isEmpty()
    }
}