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
import com.blacksquircle.ui.feature.explorer.data.utils.BufferType
import com.blacksquircle.ui.feature.explorer.data.utils.replaceList
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.viewstate.DirectoryViewState
import com.blacksquircle.ui.feature.explorer.ui.viewstate.ExplorerViewState
import com.blacksquircle.ui.filesystem.base.exception.DirectoryExpectedException
import com.blacksquircle.ui.filesystem.base.exception.RestrictedException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
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
    private var bufferType = BufferType.NONE
    private var query = ""

    init {
        listFiles(null)
    }

    fun obtainEvent(event: ExplorerEvent) = viewModelScope.launch {
        when (event) {
            is ExplorerEvent.ListFiles -> listFiles(event.parent)
            is ExplorerEvent.SearchFiles -> searchFiles(event)
            is ExplorerEvent.SelectFiles -> selectList(event)

            is ExplorerEvent.Refresh -> refreshList(event)
            is ExplorerEvent.OpenFileAs -> openFileAs(event)
            is ExplorerEvent.OpenFile -> openFile(event)

            is ExplorerEvent.CreateFile -> createFile(event)
            is ExplorerEvent.DeleteFile -> deleteFile(event)
            is ExplorerEvent.RenameFile -> renameFile(event)
            is ExplorerEvent.CompressFile -> compressFile(event)

            is ExplorerEvent.Cut -> cutFile()
            is ExplorerEvent.Copy -> copyFile()
            is ExplorerEvent.Paste -> pasteFile()
            is ExplorerEvent.Create -> createFile()
            is ExplorerEvent.Delete -> deleteFile()
            is ExplorerEvent.Rename -> renameFile()
            is ExplorerEvent.SelectAll -> selectAll()
            is ExplorerEvent.Properties -> properties()
            is ExplorerEvent.CopyPath -> copyPath()
            is ExplorerEvent.Compress -> compressFile()

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
                    bufferType = bufferType,
                )
                true
            }
            breadcrumbs.size > 1 -> {
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs - breadcrumbs.last(),
                    selection = selection,
                    bufferType = bufferType,
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
                    bufferType = bufferType,
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

    private fun searchFiles(event: ExplorerEvent.SearchFiles) {
        listFiles(breadcrumbs.lastOrNull()).also {
            query = event.query
        }
    }

    private fun selectList(event: ExplorerEvent.SelectFiles) {
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            selection = selection.replaceList(event.selection),
            bufferType = bufferType,
        )
    }

    private fun refreshList(event: ExplorerEvent.Refresh) {
        _refreshState.value = true
        listFiles(event.parent).invokeOnCompletion {
            _refreshState.value = false
        }
    }

    private suspend fun openFileAs(event: ExplorerEvent.OpenFileAs) {
        _openFileAsEvent.send(event.fileModel ?: selection.first())
    }

    private suspend fun openFile(event: ExplorerEvent.OpenFile) {
        _openFileEvent.send(event.fileModel)
    }

    // TODO move to WorkManager
    private suspend fun createFile(event: ExplorerEvent.CreateFile) {
        val isValid = event.fileName.isValidFileName()
        if (!isValid) {
            _viewEvent.send(ViewEvent.Toast(
                stringProvider.getString(R.string.message_invalid_file_name)
            ))
            return
        }
        val parent = breadcrumbs.last()
        val child = parent.copy(
            path = parent.path + "/${event.fileName}",
            isFolder = event.isFolder
        )
        // TODO create file
        _viewEvent.send(ViewEvent.Toast("create ${child.path}"))
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.NONE.also { type ->
                buffer.replaceList(emptyList())
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
    }

    // TODO move to WorkManager
    private suspend fun deleteFile(event: ExplorerEvent.DeleteFile) {
        // TODO delete files in buffer
        buffer.forEach {
            _viewEvent.send(ViewEvent.Toast("delete ${it.path}"))
        }
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.NONE.also { type ->
                buffer.replaceList(emptyList())
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
    }

    // TODO move to WorkManager
    private suspend fun renameFile(event: ExplorerEvent.RenameFile) {
        val isValid = event.newName.isValidFileName()
        if (!isValid) {
            _viewEvent.send(ViewEvent.Toast(
                stringProvider.getString(R.string.message_invalid_file_name)
            ))
            return
        }
        // TODO rename files in buffer
        buffer.forEach {
            _viewEvent.send(ViewEvent.Toast("rename ${it.name} to ${event.newName}"))
        }
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.NONE.also { type ->
                buffer.replaceList(emptyList())
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
    }

    // TODO move to WorkManager
    private suspend fun compressFile(event: ExplorerEvent.CompressFile) {
        // TODO compress files in buffer
        buffer.forEach {
            _viewEvent.send(ViewEvent.Toast("compress ${it.name} to ${event.archiveName}"))
        }
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.NONE.also { type ->
                buffer.replaceList(emptyList())
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
    }

    private fun cutFile() {
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.CUT.also { type ->
                buffer.replaceList(selection)
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
    }

    private fun copyFile() {
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.COPY.also { type ->
                buffer.replaceList(selection)
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
    }

    // TODO move to WorkManager
    private suspend fun pasteFile() {
        when (bufferType) {
            BufferType.COPY -> {
                // TODO copy buffer to current folder
                buffer.forEach {
                    _viewEvent.send(ViewEvent.Toast("copy ${it.name} to ${breadcrumbs.last().path}"))
                }
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs,
                    bufferType = BufferType.NONE.also { type ->
                        buffer.replaceList(emptyList())
                        bufferType = type
                    },
                    selection = selection.replaceList(emptyList()),
                )
            }
            BufferType.CUT -> {
                // TODO copy to current folder then delete
                buffer.forEach {
                    _viewEvent.send(ViewEvent.Toast("copy and delete ${it.name} to ${breadcrumbs.last().path}"))
                }
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs,
                    bufferType = BufferType.NONE.also { type ->
                        buffer.replaceList(emptyList())
                        bufferType = type
                    },
                    selection = selection.replaceList(emptyList()),
                )
            }
            else -> Unit
        }
    }

    private suspend fun createFile() {
        _viewEvent.send(ViewEvent.Navigation(ExplorerScreen.CreateDialog))
    }

    private suspend fun deleteFile() {
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.DELETE.also { type ->
                buffer.replaceList(selection)
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
        val screen = ExplorerScreen.DeleteDialog(buffer.first().name, buffer.size)
        _viewEvent.send(ViewEvent.Navigation(screen))
    }

    private suspend fun renameFile() {
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.RENAME.also { type ->
                buffer.replaceList(selection)
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
        val screen = ExplorerScreen.RenameDialog(buffer.first().name)
        _viewEvent.send(ViewEvent.Navigation(screen))
    }

    private suspend fun selectAll() {
    }

    private suspend fun properties() {
    }

    private suspend fun copyPath() {
    }

    private fun compressFile() {
        _explorerViewState.value = ExplorerViewState.Data(
            breadcrumbs = breadcrumbs,
            bufferType = BufferType.COMPRESS.also { type ->
                buffer.replaceList(selection)
                bufferType = type
            },
            selection = selection.replaceList(emptyList()),
        )
    }

    private suspend fun handleError(e: Throwable) {
        when (e) {
            is RestrictedException -> {
                _explorerViewState.value = ExplorerViewState.Data(
                    breadcrumbs = breadcrumbs,
                    selection = selection,
                    bufferType = bufferType,
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