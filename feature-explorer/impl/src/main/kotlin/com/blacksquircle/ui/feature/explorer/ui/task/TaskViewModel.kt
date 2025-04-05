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

package com.blacksquircle.ui.feature.explorer.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.map
import com.blacksquircle.ui.core.extensions.onEach
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.explorer.api.navigation.NotificationDeniedDialog
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TaskViewModel @AssistedInject constructor(
    private val taskManager: TaskManager,
    @Assisted private val taskId: String,
) : ViewModel() {

    /** `viewEvent` must be declared before `viewState` to avoid crash on initialization */
    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    val viewState = taskManager.monitor(taskId)
        .onEach(viewModelScope) { task ->
            if (task.isFinished) {
                _viewEvent.send(ViewEvent.PopBackStack)
            }
        }
        .map(viewModelScope) { task ->
            TaskViewState(
                type = task.type,
                count = (task.status as? TaskStatus.Progress)?.count ?: -1,
                totalCount = (task.status as? TaskStatus.Progress)?.totalCount ?: -1,
                details = (task.status as? TaskStatus.Progress)?.details.orEmpty(),
                timestamp = task.timestamp,
            )
        }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onRunInBackgroundClicked() {
        viewModelScope.launch {
            _viewEvent.send(ExplorerViewEvent.StartService)
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            val screen = NotificationDeniedDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            _viewEvent.send(ExplorerViewEvent.StartService)
        }
    }

    fun onCancelClicked() {
        taskManager.cancel(taskId)
    }

    class ParameterizedFactory(private val taskId: String) : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelFactory: Factory

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelFactory.create(taskId) as T
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted taskId: String): TaskViewModel
    }
}