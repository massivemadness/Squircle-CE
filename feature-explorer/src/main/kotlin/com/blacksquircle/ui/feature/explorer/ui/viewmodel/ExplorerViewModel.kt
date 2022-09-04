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
import kotlinx.coroutines.Job
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

    private val _openFileEvent = Channel<FileModel>(Channel.BUFFERED)
    val openFileEvent: Flow<FileModel> = _openFileEvent.receiveAsFlow()

    private val _openFileAsEvent = Channel<FileModel>(Channel.BUFFERED)
    val openFileAsEvent: Flow<FileModel> = _openFileAsEvent.receiveAsFlow()

    private val breadcrumbs = mutableListOf<FileModel>()
    private val selection = mutableListOf<FileModel>()
    private val buffer = mutableListOf<FileModel>()
    private var query = ""

    init {
        obtainEvent(ExplorerEvent.ListFiles())
    }

    fun obtainEvent(event: ExplorerEvent) {
        when (event) {
            is ExplorerEvent.ListFiles -> listFiles(event.parent)
            is ExplorerEvent.SearchFiles -> listFiles(breadcrumbs.lastOrNull())
                .also { query = event.query }
            is ExplorerEvent.SelectFiles -> {
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs,
                    selection = selection.replaceList(event.selection),
                    buffer = buffer,
                )
            }

            is ExplorerEvent.Refresh -> {
                _refreshState.value = true
                listFiles(event.parent).invokeOnCompletion {
                    _refreshState.value = false
                }
            }
            is ExplorerEvent.OpenFile -> viewModelScope.launch {
                _openFileEvent.send(event.fileModel)
            }
            is ExplorerEvent.OpenFileAs -> viewModelScope.launch {
                _openFileAsEvent.send(event.fileModel ?: selection.first())
            }

            is ExplorerEvent.Cut -> Unit
            is ExplorerEvent.Copy -> Unit
            is ExplorerEvent.Paste -> Unit
            is ExplorerEvent.Delete -> Unit
            is ExplorerEvent.SelectAll -> Unit
            is ExplorerEvent.Rename -> Unit
            is ExplorerEvent.Properties -> Unit
            is ExplorerEvent.CopyPath -> Unit
            is ExplorerEvent.Zip -> Unit

            is ExplorerEvent.ShowHidden -> Unit
            is ExplorerEvent.HideHidden -> Unit
            is ExplorerEvent.SortByDate -> Unit
            is ExplorerEvent.SortByName -> Unit
            is ExplorerEvent.SortBySize -> Unit
        }
    }

    fun handleOnBackPressed(): Boolean {
        return when {
            selection.isNotEmpty() -> {
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs,
                    selection = selection.replaceList(emptyList()),
                    buffer = buffer,
                )
                true
            }
            breadcrumbs.size > 1 -> {
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs - breadcrumbs.last(),
                    selection = selection,
                    buffer = buffer,
                )
                true
            }
            else -> false
        }
    }

    private fun listFiles(fileModel: FileModel?): Job {
        return viewModelScope.launch {
            try {
                if (!refreshState.value && query.isEmpty()) { // SwipeRefresh
                    _directoryViewState.value = DirectoryViewState.Loading
                }
                val (parent, children) = explorerRepository.listFiles(fileModel)
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
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs,
                    selection = selection,
                    buffer = buffer,
                )
                val filtered = children.filter { it.name.contains(query, ignoreCase = true) }
                if (filtered.isNotEmpty()) {
                    _directoryViewState.value = DirectoryViewState.Files(filtered)
                } else {
                    _directoryViewState.value = DirectoryViewState.Empty
                }
            } catch (e: Throwable) {
                Log.e(TAG, e.message, e)
                handleError(e)
            }
        }
    }

    private suspend fun handleError(e: Throwable) {
        when (e) {
            is RestrictedException -> {
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs,
                    selection = selection,
                    buffer = buffer,
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

    companion object {
        private const val TAG = "ExplorerViewModel"
    }
}