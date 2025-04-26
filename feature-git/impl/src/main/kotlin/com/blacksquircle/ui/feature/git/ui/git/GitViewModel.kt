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

package com.blacksquircle.ui.feature.git.ui.git

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog
import com.blacksquircle.ui.feature.git.api.navigation.CommitDialog
import com.blacksquircle.ui.feature.git.api.navigation.FetchDialog
import com.blacksquircle.ui.feature.git.api.navigation.PullDialog
import com.blacksquircle.ui.feature.git.api.navigation.PushDialog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class GitViewModel @AssistedInject constructor(
    @Assisted private val repository: String,
) : ViewModel() {

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onFetchClicked() {
        viewModelScope.launch {
            val screen = FetchDialog(repository)
            _viewEvent.send(ViewEvent.PopBackStack)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onPullClicked() {
        viewModelScope.launch {
            val screen = PullDialog(repository)
            _viewEvent.send(ViewEvent.PopBackStack)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onCommitClicked() {
        viewModelScope.launch {
            val screen = CommitDialog(repository)
            _viewEvent.send(ViewEvent.PopBackStack)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onPushClicked() {
        viewModelScope.launch {
            val screen = PushDialog(repository)
            _viewEvent.send(ViewEvent.PopBackStack)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onCheckoutClicked() {
        viewModelScope.launch {
            val screen = CheckoutDialog(repository)
            _viewEvent.send(ViewEvent.PopBackStack)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    class ParameterizedFactory(private val repository: String) : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelFactory: Factory

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelFactory.create(repository) as T
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted repository: String): GitViewModel
    }
}