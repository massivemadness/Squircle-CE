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

package com.blacksquircle.ui.feature.explorer.ui.workspace

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.explorer.data.utils.guessFilePath
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewState
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

internal class WorkspaceViewModel @Inject constructor(
    private val explorerRepository: ExplorerRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ExplorerViewState())
    val viewState: StateFlow<ExplorerViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onLocalDirectoryClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onInternalStorageClicked() {
        viewModelScope.launch {
            _viewEvent.send(WorkspaceViewEvent.SelectFolder)
        }
    }

    fun onRemoteServerClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onFolderSelected(fileUri: Uri) {
        viewModelScope.launch {
            try {
                val absolutePath = fileUri.guessFilePath()
                    ?: throw FileNotFoundException(fileUri.toString())
                explorerRepository.createWorkspace(absolutePath)
                _viewEvent.send(ViewEvent.PopBackStack)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<WorkspaceViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}