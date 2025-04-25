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

package com.blacksquircle.ui.feature.git.ui.checkout

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

internal class CheckoutViewModel @AssistedInject constructor(
    private val stringProvider: StringProvider,
    private val gitRepository: GitRepository,
    @Assisted private val repository: String,
) : ViewModel() {

    private val _viewState = MutableStateFlow(CheckoutViewState())
    val viewState: StateFlow<CheckoutViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadBranches()
    }

    fun loadBranches(){
        viewModelScope.launch {
            val listOfBranches = gitRepository.getListOfBranches(repository)
            val branch = gitRepository.getBranch(repository)
            _viewState.update {
                it.copy(checkoutBranches = listOfBranches, checkoutBranch = branch, isLoading = false, showListOfBranches = true)
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onNewBranchClicked() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(showListOfBranches = false, showBranchInput = true)
            }
        }
    }

    fun onInputChanged(input: String) {
        _viewState.update {
            it.copy(checkoutBranch = input)
        }
    }

    fun onCheckoutClicked() {
        viewModelScope.launch {
            try {
                _viewState.update {
                    it.copy(showBranchInput = false, isLoading = true)
                }

                gitRepository.checkout(repository, _viewState.value.checkoutBranch)

                val message = stringProvider.getString(R.string.git_checkout_dialog_complete)
                _viewEvent.send(ViewEvent.Toast(message))

                _viewEvent.send(ViewEvent.PopBackStack)
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
        fun create(@Assisted repositoryPath: String): CheckoutViewModel
    }
}