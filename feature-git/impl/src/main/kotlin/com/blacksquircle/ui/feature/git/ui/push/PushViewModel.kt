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

package com.blacksquircle.ui.feature.git.ui.push

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.git.R
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal class PushViewModel @AssistedInject constructor(
    private val stringProvider: StringProvider,
    private val gitRepository: GitRepository,
    @Assisted private val repository: String,
) : ViewModel() {

    private val _viewState = MutableStateFlow(PushViewState())
    val viewState: StateFlow<PushViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var isForce = false

    init {
        loadCommits()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onForceClicked() {
        isForce = !isForce
        _viewState.update {
            it.copy(isForce = isForce)
        }
    }

    fun onPushClicked() {
        viewModelScope.launch {
            try {
                _viewState.update {
                    it.copy(isPushing = true)
                }

                gitRepository.push(repository, isForce)

                val message = stringProvider.getString(R.string.git_push_complete)
                _viewEvent.send(ViewEvent.Toast(message))

                _viewEvent.send(ViewEvent.PopBackStack)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewState.update {
                    it.copy(
                        isPushing = false,
                        errorMessage = e.message.orEmpty()
                    )
                }
            }
        }
    }

    private fun loadCommits() {
        viewModelScope.launch {
            try {
                val currentBranch = gitRepository.currentBranch(repository)
                val commitCount = gitRepository.commitCount(repository)
                if (commitCount == 0) {
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = stringProvider.getString(R.string.git_push_no_commits)
                        )
                    }
                    return@launch
                }

                _viewState.update {
                    it.copy(
                        currentBranch = currentBranch,
                        commitCount = commitCount,
                        isLoading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message.orEmpty()
                    )
                }
            }
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
        fun create(@Assisted repository: String): PushViewModel
    }
}