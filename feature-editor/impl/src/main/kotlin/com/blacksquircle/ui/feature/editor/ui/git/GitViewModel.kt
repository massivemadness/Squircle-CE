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

package com.blacksquircle.ui.feature.editor.ui.git

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import com.blacksquircle.ui.feature.editor.domain.repository.GitRepository

internal class GitViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val gitRepository: GitRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(GitViewState(
        branch = "",
        commitText = "",
        isLoading = false,
        showCommitDialog = false,
        showBranchDialog = false
    ))
    val viewState: StateFlow<GitViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var counter: Int = 1

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onFetchClicked(repoPath: String) {
        viewModelScope.launch {
            // todo
        }
    }

    fun onPullClicked(repoPath: String) {
        viewModelScope.launch {
            // todo
        }
    }

    fun onCommitClicked(repoPath: String) {
        viewModelScope.launch {
            // todo
        }
    }

    fun onPushClicked(repoPath: String) {
        viewModelScope.launch {
            // todo
        }
    }

    fun onCheckoutClicked(repoPath: String) {
        viewModelScope.launch {
            // todo
        }
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<GitViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}