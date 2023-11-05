/*
 * Copyright 2023 Squircle CE contributors.
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.appendList
import com.blacksquircle.ui.core.extensions.replaceList
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.FileSorter
import com.blacksquircle.ui.feature.explorer.data.utils.Operation
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.mvi.*
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.servers.domain.repository.ServersRepository
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.exception.PermissionException
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.blacksquircle.ui.uikit.R as UiR

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val explorerRepository: ExplorerRepository,
    private val serversRepository: ServersRepository,
) : ViewModel() {

    private val _toolbarViewState = MutableStateFlow<ToolbarViewState>(ToolbarViewState.ActionBar())
    val toolbarViewState: StateFlow<ToolbarViewState> = _toolbarViewState.asStateFlow()

    private val _explorerViewState = MutableStateFlow<ExplorerViewState>(ExplorerViewState.Loading)
    val explorerViewState: StateFlow<ExplorerViewState> = _explorerViewState.asStateFlow()

    private val _refreshState = MutableStateFlow(false)
    val refreshState: StateFlow<Boolean> = _refreshState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val _customEvent = MutableSharedFlow<ExplorerViewEvent>()
    val customEvent: SharedFlow<ExplorerViewEvent> = _customEvent.asSharedFlow()

    val filesystems = serversRepository.serverFlow
        .map { servers -> explorerRepository.loadFilesystems() + servers.map(::FilesystemModel) }
        .catch { errorState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    var filesystem: String = settingsManager.filesystem
        private set
    var viewMode: Int = settingsManager.viewMode.toInt()
        private set
    var sortMode: Int = settingsManager.sortMode.toInt()
        private set
    var showHidden: Boolean = settingsManager.showHidden
        private set
    var query: String = ""
        private set

    private val breadcrumbs = mutableListOf<FileModel>()
    private val selection = mutableListOf<FileModel>()
    private val buffer = mutableListOf<FileModel>()
    private val files = mutableListOf<FileModel>()
    private var operation = Operation.CREATE
    private var currentJob: Job? = null

    init {
        listFiles(ExplorerIntent.OpenFolder())
    }

    fun obtainEvent(event: ExplorerIntent) {
        when (event) {
            is ExplorerIntent.SearchFiles -> searchFiles(event)
            is ExplorerIntent.SelectFiles -> selectFiles(event)
            is ExplorerIntent.SelectTab -> selectTab(event)
            is ExplorerIntent.SelectFilesystem -> selectFilesystem(event)
            is ExplorerIntent.Authenticate -> authenticate(event)
            is ExplorerIntent.Refresh -> refreshList()

            is ExplorerIntent.Cut -> cutButton()
            is ExplorerIntent.Copy -> copyButton()
            is ExplorerIntent.Create -> createButton()
            is ExplorerIntent.Rename -> renameButton()
            is ExplorerIntent.Delete -> deleteButton()
            is ExplorerIntent.SelectAll -> selectAllButton()
            is ExplorerIntent.UnselectAll -> unselectAllButton()
            is ExplorerIntent.Properties -> propertiesButton()
            is ExplorerIntent.CopyPath -> copyPathButton()
            is ExplorerIntent.Compress -> compressButton()

            is ExplorerIntent.OpenFolder -> listFiles(event)
            is ExplorerIntent.OpenFileWith -> openFileAs(event)
            is ExplorerIntent.OpenFile -> openFile(event)
            is ExplorerIntent.CreateFile -> createFile(event)
            is ExplorerIntent.RenameFile -> renameFile(event)
            is ExplorerIntent.DeleteFile -> deleteFile()
            is ExplorerIntent.CutFile -> cutFile()
            is ExplorerIntent.CopyFile -> copyFile()
            is ExplorerIntent.CompressFile -> compressFile(event)
            is ExplorerIntent.ExtractFile -> extractFile(event)

            is ExplorerIntent.ShowHidden -> showHidden()
            is ExplorerIntent.HideHidden -> hideHidden()
            is ExplorerIntent.SortByName -> sortByName()
            is ExplorerIntent.SortBySize -> sortBySize()
            is ExplorerIntent.SortByDate -> sortByDate()
        }
    }

    private fun listFiles(event: ExplorerIntent.OpenFolder) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                if (!refreshState.value && query.isEmpty()) { // SwipeRefresh
                    _explorerViewState.value = ExplorerViewState.Loading
                }

                val fileTree = if (event.fileModel != null) { // Different order
                    breadcrumbs.appendList(event.fileModel)
                    refreshActionBar()
                    explorerRepository.listFiles(event.fileModel)
                } else {
                    explorerRepository.listFiles(null).also {
                        breadcrumbs.appendList(it.parent)
                        refreshActionBar()
                    }
                }
                if (fileTree.children.isNotEmpty()) {
                    _explorerViewState.value = ExplorerViewState.Files(fileTree.children)
                } else {
                    _explorerViewState.value = ExplorerViewState.Error(
                        image = UiR.drawable.ic_file_find,
                        title = stringProvider.getString(UiR.string.common_no_result),
                        subtitle = "",
                        action = ExplorerErrorAction.Undefined,
                    )
                }
                files.replaceList(fileTree.children)
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                _refreshState.value = false
            }
        }
    }

    private fun searchFiles(event: ExplorerIntent.SearchFiles) {
        viewModelScope.launch {
            query = event.query
            val searchList = files.filter { it.name.contains(query, ignoreCase = true) }
            if (searchList.isNotEmpty()) {
                _explorerViewState.value = ExplorerViewState.Files(searchList)
            } else {
                _explorerViewState.value = ExplorerViewState.Error(
                    image = UiR.drawable.ic_file_find,
                    title = stringProvider.getString(UiR.string.common_no_result),
                    subtitle = "",
                    action = ExplorerErrorAction.Undefined,
                )
            }
        }
    }

    private fun selectFiles(event: ExplorerIntent.SelectFiles) {
        viewModelScope.launch {
            selection.replaceList(event.selection)
            refreshActionBar()
        }
    }

    private fun selectTab(event: ExplorerIntent.SelectTab) {
        viewModelScope.launch {
            breadcrumbs.replaceList(breadcrumbs.take(event.position + 1))
            selection.replaceList(emptyList())
            refreshActionBar()

            listFiles(ExplorerIntent.OpenFolder(breadcrumbs.lastOrNull()))
        }
    }

    private fun selectFilesystem(event: ExplorerIntent.SelectFilesystem) {
        viewModelScope.launch {
            try {
                if (filesystem != event.filesystemUuid) {
                    filesystem = event.filesystemUuid
                    explorerRepository.selectFilesystem(filesystem)
                    breadcrumbs.replaceList(emptyList())
                    files.replaceList(emptyList())
                    initialState()
                    listFiles(ExplorerIntent.OpenFolder())
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
            }
        }
    }

    private fun authenticate(event: ExplorerIntent.Authenticate) {
        viewModelScope.launch {
            try {
                serversRepository.authenticate(filesystem, event.password)
                initialState()
                listFiles(ExplorerIntent.OpenFolder())
            } catch (e: Exception) {
                Timber.e(e, e.message)
            }
        }
    }

    private fun refreshList() {
        viewModelScope.launch {
            _refreshState.value = true
            listFiles(ExplorerIntent.OpenFolder(breadcrumbs.lastOrNull()))
        }
    }

    private fun cutButton() {
        viewModelScope.launch {
            operation = Operation.CUT
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()
        }
    }

    private fun copyButton() {
        viewModelScope.launch {
            operation = Operation.COPY
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()
        }
    }

    private fun createButton() {
        viewModelScope.launch {
            operation = Operation.CREATE
            buffer.replaceList(emptyList()) // empty buffer for Operation.CREATE
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.CreateDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun renameButton() {
        viewModelScope.launch {
            operation = Operation.RENAME
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.RenameDialog(buffer.first().name)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun deleteButton() {
        viewModelScope.launch {
            operation = Operation.DELETE
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.DeleteDialog(buffer.first().name, buffer.size)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun selectAllButton() {
        viewModelScope.launch {
            _customEvent.emit(ExplorerViewEvent.SelectAll)
        }
    }

    private fun unselectAllButton() {
        viewModelScope.launch {
            selection.replaceList(emptyList())
            refreshActionBar()
        }
    }

    private fun propertiesButton() {
        viewModelScope.launch {
            try {
                val fileModel = selection.first()
                val screen = ExplorerScreen.PropertiesDialog(fileModel)
                _viewEvent.send(ViewEvent.Navigation(screen))
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun copyPathButton() {
        viewModelScope.launch {
            val fileModel = selection.first()
            _customEvent.emit(ExplorerViewEvent.CopyPath(fileModel))
            initialState()
        }
    }

    private fun compressButton() {
        viewModelScope.launch {
            operation = Operation.COMPRESS
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.CompressDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun openFileAs(event: ExplorerIntent.OpenFileWith) {
        viewModelScope.launch {
            val fileModel = event.fileModel ?: selection.first()
            _customEvent.emit(ExplorerViewEvent.OpenFileWith(fileModel))
            initialState()
        }
    }

    private fun openFile(event: ExplorerIntent.OpenFile) {
        viewModelScope.launch {
            when (event.fileModel.type) {
                FileType.ARCHIVE -> extractFile(ExplorerIntent.ExtractFile(event.fileModel))
                FileType.DEFAULT,
                FileType.TEXT -> _customEvent.emit(ExplorerViewEvent.OpenFile(event.fileModel))
                else -> openFileAs(ExplorerIntent.OpenFileWith(event.fileModel))
            }
        }
    }

    private fun createFile(event: ExplorerIntent.CreateFile) {
        viewModelScope.launch {
            try {
                val isValid = event.fileName.isValidFileName()
                if (!isValid) {
                    _viewEvent.send(
                        ViewEvent.Toast(stringProvider.getString(R.string.message_invalid_file_name)),
                    )
                    return@launch
                }
                val parent = breadcrumbs.last()
                val child = parent.copy(
                    fileUri = parent.fileUri + "/" + event.fileName,
                    directory = event.directory,
                )
                explorerRepository.createFile(child)
                _viewEvent.send(
                    ViewEvent.Navigation(
                        ExplorerScreen.ProgressDialog(1, Operation.CREATE),
                    ),
                )
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun renameFile(event: ExplorerIntent.RenameFile) {
        viewModelScope.launch {
            try {
                val isValid = event.fileName.isValidFileName()
                if (!isValid) {
                    _viewEvent.send(
                        ViewEvent.Toast(stringProvider.getString(R.string.message_invalid_file_name)),
                    )
                    return@launch
                }

                val originalFile = buffer.first()
                val renamedFile = originalFile.copy(
                    fileUri = originalFile.fileUri.substringBeforeLast('/') + "/" + event.fileName,
                    directory = originalFile.directory,
                )
                explorerRepository.renameFile(originalFile, renamedFile)
                _viewEvent.send(
                    ViewEvent.Navigation(
                        ExplorerScreen.ProgressDialog(1, Operation.RENAME),
                    ),
                )
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun deleteFile() {
        viewModelScope.launch {
            try {
                explorerRepository.deleteFiles(buffer)
                _viewEvent.send(
                    ViewEvent.Navigation(
                        ExplorerScreen.ProgressDialog(buffer.size, Operation.DELETE),
                    ),
                )
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun cutFile() {
        viewModelScope.launch {
            try {
                explorerRepository.cutFiles(buffer, breadcrumbs.last())
                _viewEvent.send(
                    ViewEvent.Navigation(
                        ExplorerScreen.ProgressDialog(buffer.size, Operation.CUT),
                    ),
                )
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun copyFile() {
        viewModelScope.launch {
            try {
                explorerRepository.copyFiles(buffer, breadcrumbs.last())
                _viewEvent.send(
                    ViewEvent.Navigation(
                        ExplorerScreen.ProgressDialog(buffer.size, Operation.COPY),
                    ),
                )
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun compressFile(event: ExplorerIntent.CompressFile) {
        viewModelScope.launch {
            try {
                val isValid = event.fileName.isValidFileName()
                if (!isValid) {
                    _viewEvent.send(
                        ViewEvent.Toast(stringProvider.getString(R.string.message_invalid_file_name)),
                    )
                    return@launch
                }
                val parent = breadcrumbs.last()
                val child = parent.copy(parent.path + "/" + event.fileName)
                explorerRepository.compressFiles(buffer, child)
                _viewEvent.send(
                    ViewEvent.Navigation(
                        ExplorerScreen.ProgressDialog(buffer.size, Operation.COMPRESS),
                    ),
                )
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun extractFile(event: ExplorerIntent.ExtractFile) {
        viewModelScope.launch {
            try {
                explorerRepository.extractFiles(event.fileModel, breadcrumbs.last())
                _viewEvent.send(
                    ViewEvent.Navigation(
                        ExplorerScreen.ProgressDialog(-1, Operation.EXTRACT),
                    ),
                )
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                errorState(e)
            } finally {
                initialState()
            }
        }
    }

    private fun showHidden() {
        settingsManager.showHidden = true
        showHidden = true
        refreshList()
    }

    private fun hideHidden() {
        settingsManager.showHidden = false
        showHidden = false
        refreshList()
    }

    private fun sortByName() {
        sortMode = FileSorter.SORT_BY_NAME
        settingsManager.sortMode = FileSorter.SORT_BY_NAME.toString()
        refreshList()
    }

    private fun sortBySize() {
        sortMode = FileSorter.SORT_BY_SIZE
        settingsManager.sortMode = FileSorter.SORT_BY_SIZE.toString()
        refreshList()
    }

    private fun sortByDate() {
        sortMode = FileSorter.SORT_BY_DATE
        settingsManager.sortMode = FileSorter.SORT_BY_DATE.toString()
        refreshList()
    }

    private fun initialState() {
        operation = Operation.CREATE
        buffer.replaceList(emptyList())
        selection.replaceList(emptyList())
        refreshActionBar()
    }

    private fun refreshActionBar() {
        _toolbarViewState.value = ToolbarViewState.ActionBar(
            breadcrumbs = breadcrumbs.toList(),
            selection = selection.toList(),
            operation = operation,
        )
    }

    private fun errorState(e: Throwable) {
        when (e) {
            is CancellationException -> {
                _explorerViewState.value = ExplorerViewState.Loading
            }
            is PermissionException -> {
                _explorerViewState.value = ExplorerViewState.Error(
                    image = UiR.drawable.ic_file_error,
                    title = stringProvider.getString(R.string.message_access_denied),
                    subtitle = stringProvider.getString(R.string.message_access_required),
                    action = ExplorerErrorAction.RequestPermission,
                )
            }
            is AuthenticationException -> {
                if (e.authError) {
                    _explorerViewState.value = ExplorerViewState.Error(
                        image = UiR.drawable.ic_file_error,
                        title = stringProvider.getString(UiR.string.common_error_occurred),
                        subtitle = e.message.orEmpty(),
                        action = ExplorerErrorAction.EnterCredentials(e.authMethod)
                    )
                } else {
                    _explorerViewState.value = ExplorerViewState.Error(
                        image = UiR.drawable.ic_file_error,
                        title = stringProvider.getString(R.string.message_auth_required),
                        subtitle = when (e.authMethod) {
                            AuthMethod.PASSWORD -> stringProvider.getString(R.string.message_enter_password)
                            AuthMethod.KEY -> stringProvider.getString(R.string.message_enter_passphrase)
                        },
                        action = ExplorerErrorAction.EnterCredentials(e.authMethod)
                    )
                }
            }
            else -> {
                _explorerViewState.value = ExplorerViewState.Error(
                    image = UiR.drawable.ic_file_error,
                    title = stringProvider.getString(UiR.string.common_error_occurred),
                    subtitle = e.message.orEmpty(),
                    action = ExplorerErrorAction.Undefined,
                )
            }
        }
    }
}