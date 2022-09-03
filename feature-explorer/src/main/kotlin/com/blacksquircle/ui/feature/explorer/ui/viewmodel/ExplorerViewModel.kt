/*
 * Copyright 2022 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.explorer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.replaceList
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.viewstate.DirectoryViewState
import com.blacksquircle.ui.feature.explorer.ui.viewstate.ExplorerViewState
import com.blacksquircle.ui.filesystem.base.exception.DirectoryExpectedException
import com.blacksquircle.ui.filesystem.base.exception.RestrictedException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val explorerRepository: ExplorerRepository,
) : ViewModel() {

    private val _explorerViewState = MutableStateFlow<ExplorerViewState>(ExplorerViewState.Stub)
    val explorerViewState: StateFlow<ExplorerViewState> = _explorerViewState.asStateFlow()

    private val _directoryViewState = MutableStateFlow<DirectoryViewState>(DirectoryViewState.Stub)
    val directoryViewState: StateFlow<DirectoryViewState> = _directoryViewState.asStateFlow()

    private val _refreshState = MutableStateFlow(false)
    val refreshState: StateFlow<Boolean> = _refreshState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val breadcrumbs = mutableListOf<FileModel>()
    private val selection = mutableListOf<FileModel>()

    init {
        fetchFiles(null)
    }

    fun fetchFiles(fileModel: FileModel?) = viewModelScope.launch {
        try {
            if (!refreshState.value) { // SwipeRefresh
                _directoryViewState.value = DirectoryViewState.Loading
            }
            val (parent, children) = explorerRepository.fetchFiles(fileModel)
            if (breadcrumbs.contains(parent)) {
                breadcrumbs.replaceList(
                    collection = breadcrumbs.subList(
                        fromIndex = 0,
                        toIndex = breadcrumbs.indexOf(parent) + 1
                    )
                )
            } else {
                breadcrumbs += parent
            }
            _explorerViewState.value = ExplorerViewState.Breadcrumbs(
                breadcrumbs = breadcrumbs.toList(),
                selection = selection.toList(),
            )
            if (children.isNotEmpty()) {
                _directoryViewState.value = DirectoryViewState.Files(children)
            } else {
                _directoryViewState.value = DirectoryViewState.Empty
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
            when (e) {
                is RestrictedException -> {
                    _explorerViewState.value = ExplorerViewState.Breadcrumbs(
                        breadcrumbs = breadcrumbs.toList(),
                        selection = selection.toList(),
                    )
                    _directoryViewState.value = DirectoryViewState.Restricted
                }
                is DirectoryExpectedException -> {
                    _viewEvent.send(
                        ViewEvent.Toast(
                            stringProvider.getString(R.string.message_directory_expected)
                        )
                    )
                }
                else -> {
                    _viewEvent.send(
                        ViewEvent.Toast(
                            stringProvider.getString(R.string.message_unknown_exception)
                        )
                    )
                }
            }
        }
    }

    fun refresh(fileModel: FileModel?) {
        _refreshState.value = true
        fetchFiles(fileModel).invokeOnCompletion {
            _refreshState.value = false
        }
    }

    companion object {
        private const val TAG = "ExplorerViewModel"
    }
}