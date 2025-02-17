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

package com.blacksquircle.ui.feature.explorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.utils.fileComparator
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.model.ViewMode
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerViewEvent
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerViewState
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.BreadcrumbState
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.servers.api.interactor.ServersInteractor
import com.blacksquircle.ui.filesystem.base.exception.AuthRequiredException
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.exception.EncryptedArchiveException
import com.blacksquircle.ui.filesystem.base.exception.FileAlreadyExistsException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.exception.InvalidArchiveException
import com.blacksquircle.ui.filesystem.base.exception.PermissionException
import com.blacksquircle.ui.filesystem.base.exception.SplitArchiveException
import com.blacksquircle.ui.filesystem.base.exception.UnsupportedArchiveException
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
import com.blacksquircle.ui.ds.R as UiR

internal class ExplorerViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val taskManager: TaskManager,
    private val editorInteractor: EditorInteractor,
    private val explorerRepository: ExplorerRepository,
    private val serversInteractor: ServersInteractor,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ExplorerViewState())
    val viewState: StateFlow<ExplorerViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var selectedFilesystem: String
        get() = settingsManager.filesystem
        set(value) {
            settingsManager.filesystem = value
        }
    private var showHidden: Boolean
        get() = settingsManager.showHidden
        set(value) {
            settingsManager.showHidden = value
        }
    private var sortMode: SortMode
        get() = SortMode.of(settingsManager.sortMode)
        set(value) {
            settingsManager.sortMode = value.value
        }
    private val viewMode: ViewMode
        get() = ViewMode.of(settingsManager.viewMode)

    private var taskType: TaskType = TaskType.CREATE
    private var taskBuffer: List<FileModel> = emptyList()
    private var selectedFiles: List<FileModel> = emptyList()
    private var breadcrumbs: List<BreadcrumbState> = emptyList()
    private var selectionBreadcrumb: Int = -1
    private var searchQuery: String = ""
    private var currentJob: Job? = null

    init {
        loadFilesystems()
    }

    fun onBackClicked() {
        if (selectedFiles.isNotEmpty()) {
            selectedFiles = emptyList()
            _viewState.update {
                it.copy(selectedFiles = selectedFiles)
            }
        } else {
            viewModelScope.launch {
                _viewEvent.send(ViewEvent.PopBackStack())
            }
        }
    }

    fun onFilesystemSelected(filesystem: String) {
        if (filesystem == CREATE_SERVER_UUID) {
            viewModelScope.launch {
                _viewEvent.send(ViewEvent.Navigation(Screen.Server))
            }
            return
        }
        if (selectedFilesystem == filesystem) {
            return
        }

        selectedFilesystem = filesystem
        breadcrumbs = emptyList()
        selectionBreadcrumb = -1

        _viewState.update {
            it.copy(
                selectedFilesystem = selectedFilesystem,
                breadcrumbs = breadcrumbs,
                selectedBreadcrumb = selectionBreadcrumb,
            )
        }
        loadFiles()
    }

    fun onFilesystemAdded() {
        loadFilesystems()
    }

    fun onQueryChanged(query: String) {
        searchQuery = query
        _viewState.update {
            it.copy(
                breadcrumbs = breadcrumbs.mapSelected { state ->
                    state.copy(fileList = state.fileList.applyFilter())
                },
                searchQuery = searchQuery,
            )
        }
    }

    fun onClearQueryClicked() {
        searchQuery = ""
        _viewState.update {
            it.copy(
                breadcrumbs = breadcrumbs.mapSelected { state ->
                    state.copy(fileList = state.fileList.applyFilter())
                },
                searchQuery = searchQuery,
            )
        }
    }

    fun onShowHiddenClicked() {
        showHidden = !showHidden
        _viewState.update {
            it.copy(
                breadcrumbs = breadcrumbs.mapSelected { state ->
                    state.copy(fileList = state.fileList.applyFilter())
                },
                showHidden = showHidden,
            )
        }
    }

    fun onSortModeSelected(value: SortMode) {
        sortMode = value
        _viewState.update {
            it.copy(
                breadcrumbs = breadcrumbs.mapSelected { state ->
                    state.copy(fileList = state.fileList.applyFilter())
                },
                sortMode = sortMode,
            )
        }
    }

    fun onHomeClicked() {
        loadFiles()
    }

    fun onBreadcrumbClicked(breadcrumb: BreadcrumbState) {
        loadFiles(breadcrumb.fileModel)
    }

    fun onFileClicked(fileModel: FileModel) {
        if (selectedFiles.isNotEmpty()) {
            onFileSelected(fileModel)
        } else if (fileModel.directory) {
            loadFiles(fileModel)
        } else {
            viewModelScope.launch {
                when (fileModel.type) {
                    FileType.ARCHIVE -> extractFiles(fileModel)
                    FileType.DEFAULT,
                    FileType.TEXT -> {
                        editorInteractor.openFile(fileModel)
                        _viewEvent.send(ViewEvent.PopBackStack())
                    }

                    else -> onOpenWithClicked(fileModel)
                }
            }
        }
    }

    fun onFileSelected(fileModel: FileModel) {
        val index = selectedFiles.indexOf { it.fileUri == fileModel.fileUri }
        if (index == -1) {
            selectedFiles += fileModel
        } else {
            selectedFiles -= fileModel
        }
        _viewState.update {
            it.copy(selectedFiles = selectedFiles)
        }
    }

    fun onRefreshClicked() {
        val breadcrumb = breadcrumbs.getOrNull(selectionBreadcrumb)
        loadFiles(breadcrumb?.fileModel)
    }

    fun onCreateClicked() {
        viewModelScope.launch {
            taskType = TaskType.CREATE
            taskBuffer = emptyList()
            selectedFiles = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedFiles = selectedFiles,
                )
            }

            val screen = ExplorerScreen.CreateDialogScreen
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onCopyClicked() {
    }

    fun onPasteClicked() {
    }

    fun onDeleteClicked() {
    }

    fun onCutClicked() {
    }

    fun onSelectAllClicked() {
    }

    fun onOpenWithClicked(fileModel: FileModel? = null) {
    }

    fun onRenameClicked() {
        viewModelScope.launch {
            taskType = TaskType.RENAME
            taskBuffer = listOf(selectedFiles.first())
            selectedFiles = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedFiles = selectedFiles,
                )
            }

            val screen = ExplorerScreen.RenameDialogScreen(taskBuffer.first().name)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onPropertiesClicked() {
    }

    fun onCopyPathClicked() {
    }

    fun onCompressClicked() {
    }

    // region ERROR_ACTION

    fun onErrorActionClicked(errorAction: ErrorAction) {
        viewModelScope.launch {
            when (errorAction) {
                ErrorAction.REQUEST_PERMISSIONS -> {
                    _viewEvent.send(ExplorerViewEvent.RequestPermission)
                }

                ErrorAction.ENTER_PASSWORD -> {
                    val screen = ExplorerScreen.AuthDialogScreen(AuthMethod.PASSWORD)
                    _viewEvent.send(ViewEvent.Navigation(screen))
                }

                ErrorAction.ENTER_PASSPHRASE -> {
                    val screen = ExplorerScreen.AuthDialogScreen(AuthMethod.KEY)
                    _viewEvent.send(ViewEvent.Navigation(screen))
                }

                ErrorAction.UNDEFINED -> Unit
            }
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            val screen = ExplorerScreen.StorageDeniedScreen
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onPermissionGranted() {
        loadFiles()
    }

    fun onCredentialsEntered(credentials: String) {
        viewModelScope.launch {
            try {
                serversInteractor.authenticate(selectedFilesystem, credentials)
                loadFiles()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
            }
        }
    }

    // endregion

    fun createFile(fileName: String, isFolder: Boolean) {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack()) // close dialog

            val isValid = fileName.isValidFileName()
            if (!isValid) {
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_invalid_file_name)),
                )
                return@launch
            }

            val parent = breadcrumbs[selectionBreadcrumb].fileModel
            val taskId = explorerRepository.createFile(parent, fileName, isFolder)
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> onTaskFinished()
                    else -> Unit
                }
            }
        }
    }

    fun renameFile(fileName: String) {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack()) // close dialog

            val isValid = fileName.isValidFileName()
            if (!isValid) {
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_invalid_file_name)),
                )
                return@launch
            }

            val fileModel = taskBuffer.first()
            val taskId = explorerRepository.renameFile(fileModel, fileName)
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> onTaskFinished()
                    else -> Unit
                }
            }
        }
    }

    private fun extractFiles(fileModel: FileModel) {
    }

    private fun loadFiles(parent: FileModel? = null) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            var isRefreshing = false

            /** Check if [parent] is already added to breadcrumbs */
            val existingIndex = breadcrumbs.indexOf { it.fileModel?.fileUri == parent?.fileUri }
            if (existingIndex != -1) {
                if (existingIndex == selectionBreadcrumb) {
                    /** Refresh current tab */
                    isRefreshing = true
                } else {
                    /** Select existing tab */
                    selectionBreadcrumb = existingIndex

                    /**
                     * When autoRefresh=true it means that coroutine was cancelled and we have to
                     * reload file list. Otherwise skip loading and show files immediately.
                     */
                    if (breadcrumbs[existingIndex].autoRefresh) {
                        isRefreshing = true
                    } else {
                        _viewState.update {
                            it.copy(
                                breadcrumbs = breadcrumbs.mapSelected { state ->
                                    state.copy(fileList = state.fileList.applyFilter())
                                },
                                selectedBreadcrumb = selectionBreadcrumb,
                                isLoading = false,
                            )
                        }
                        return@launch // early return
                    }
                }
            }

            val updatedState = if (isRefreshing) {
                /** Refresh current directory, don't open a new tab */
                breadcrumbs[selectionBreadcrumb]
            } else {
                /**
                 * Remove all tabs after the selected one, insert empty tree at the end.
                 * Set autoRefresh=true to refresh list in case when [CancellationException] occurs.
                 */
                val newState = BreadcrumbState(
                    fileModel = parent,
                    fileList = emptyList(),
                    errorState = null,
                    autoRefresh = true,
                )
                val fromIndex = 0
                val toIndex = if (selectionBreadcrumb > -1) selectionBreadcrumb + 1 else 0
                breadcrumbs = breadcrumbs.subList(fromIndex, toIndex) + newState
                selectionBreadcrumb = breadcrumbs.size - 1
                newState
            }

            try {
                _viewState.update {
                    it.copy(
                        breadcrumbs = breadcrumbs,
                        selectedBreadcrumb = selectionBreadcrumb,
                        isLoading = true,
                    )
                }

                /** Load files, update directory */
                val fileList = explorerRepository.listFiles(parent)

                breadcrumbs = breadcrumbs.mapSelected {
                    updatedState.copy(
                        fileList = fileList,
                        errorState = null,
                        autoRefresh = false,
                    )
                }

                _viewState.update {
                    it.copy(
                        breadcrumbs = breadcrumbs.mapSelected { state ->
                            state.copy(fileList = state.fileList.applyFilter())
                        },
                        selectedBreadcrumb = selectionBreadcrumb,
                        isLoading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)

                /** Clear list and show error */
                breadcrumbs = breadcrumbs.mapSelected {
                    updatedState.copy(
                        fileList = emptyList(),
                        errorState = errorState(e),
                        autoRefresh = false,
                    )
                }

                _viewState.update {
                    it.copy(
                        breadcrumbs = breadcrumbs,
                        selectedBreadcrumb = selectionBreadcrumb,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun loadFilesystems() {
        viewModelScope.launch {
            try {
                val defaultFilesystems = listOf(
                    FilesystemModel(
                        uuid = LocalFilesystem.LOCAL_UUID,
                        title = stringProvider.getString(R.string.storage_local),
                    ),
                    FilesystemModel(
                        uuid = RootFilesystem.ROOT_UUID,
                        title = stringProvider.getString(R.string.storage_root),
                    ),
                )
                val serverFilesystems = serversInteractor.loadServers().map { config ->
                    FilesystemModel(
                        uuid = config.uuid,
                        title = config.name
                    )
                }
                val addServer = FilesystemModel(
                    uuid = CREATE_SERVER_UUID,
                    title = stringProvider.getString(R.string.storage_add),
                )
                _viewState.update {
                    it.copy(
                        filesystems = defaultFilesystems + serverFilesystems + addServer,
                        selectedFilesystem = selectedFilesystem,
                        searchQuery = searchQuery,
                        showHidden = showHidden,
                        sortMode = sortMode,
                        viewMode = viewMode,
                    )
                }
                if (breadcrumbs.isEmpty()) {
                    loadFiles()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
            }
        }
    }

    private fun errorState(e: Exception): ErrorState {
        return when (e) {
            is PermissionException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(R.string.message_access_denied),
                subtitle = stringProvider.getString(R.string.message_access_required),
                action = ErrorAction.REQUEST_PERMISSIONS,
            )

            is AuthRequiredException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(R.string.message_auth_required),
                subtitle = when (e.authMethod) {
                    AuthMethod.PASSWORD -> stringProvider.getString(R.string.message_enter_password)
                    AuthMethod.KEY -> stringProvider.getString(R.string.message_enter_passphrase)
                },
                action = when (e.authMethod) {
                    AuthMethod.PASSWORD -> ErrorAction.ENTER_PASSWORD
                    AuthMethod.KEY -> ErrorAction.ENTER_PASSPHRASE
                }
            )

            is AuthenticationException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_error_occurred),
                subtitle = when (e.authMethod) {
                    AuthMethod.PASSWORD -> stringProvider.getString(R.string.message_enter_password)
                    AuthMethod.KEY -> stringProvider.getString(R.string.message_enter_passphrase)
                },
                action = when (e.authMethod) {
                    AuthMethod.PASSWORD -> ErrorAction.ENTER_PASSWORD
                    AuthMethod.KEY -> ErrorAction.ENTER_PASSPHRASE
                }
            )

            else -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_error_occurred),
                subtitle = e.message.orEmpty(),
                action = ErrorAction.UNDEFINED,
            )
        }
    }

    private suspend fun onTaskFinished() {
        val message = stringProvider.getString(R.string.message_done)
        _viewEvent.send(ViewEvent.Toast(message))
        onRefreshClicked()
    }

    private suspend fun onTaskFailed(e: Exception) {
        when (e) {
            is FileNotFoundException -> {
                val message = stringProvider.getString(R.string.message_file_not_found)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            is FileAlreadyExistsException -> {
                val message = stringProvider.getString(R.string.message_file_already_exists)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            is UnsupportedArchiveException -> {
                val message = stringProvider.getString(R.string.message_unsupported_archive)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            is EncryptedArchiveException -> {
                val message = stringProvider.getString(R.string.message_encrypted_archive)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            is SplitArchiveException -> {
                val message = stringProvider.getString(R.string.message_split_archive)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            is InvalidArchiveException -> {
                val message = stringProvider.getString(R.string.message_invalid_archive)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            is UnsupportedOperationException -> {
                val message = stringProvider.getString(R.string.message_operation_not_supported)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            is CancellationException -> {
                val message = stringProvider.getString(R.string.message_operation_cancelled)
                _viewEvent.send(ViewEvent.Toast(message))
            }

            else -> {
                _viewEvent.send(ViewEvent.Toast(e.message.toString()))
            }
        }
        onRefreshClicked()
    }

    private fun List<BreadcrumbState>.mapSelected(
        predicate: (BreadcrumbState) -> BreadcrumbState
    ): List<BreadcrumbState> {
        return mapIndexed { index, state ->
            if (index == selectionBreadcrumb) {
                predicate(state)
            } else {
                state
            }
        }
    }

    private fun List<FileModel>.applyFilter(): List<FileModel> {
        return filter { it.name.contains(searchQuery, ignoreCase = true) }
            .filter { if (it.isHidden) showHidden else true }
            .sortedWith(fileComparator(settingsManager.sortMode))
            .sortedBy { it.directory != settingsManager.foldersOnTop }
    }

    companion object {
        private const val CREATE_SERVER_UUID = "create_server"
    }

    /*
    private val breadcrumbs = mutableListOf<FileModel>()
    private val selection = mutableListOf<FileModel>()
    private val buffer = mutableListOf<FileModel>()
    private val files = mutableListOf<FileModel>()
    private var taskType = TaskType.CREATE
    private var currentJob: Job? = null

    private fun cutButton() {
        viewModelScope.launch {
            taskType = TaskType.CUT
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()
        }
    }

    private fun copyButton() {
        viewModelScope.launch {
            taskType = TaskType.COPY
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()
        }
    }

    private fun createButton() {
        viewModelScope.launch {
            taskType = TaskType.CREATE
            buffer.replaceList(emptyList()) // empty buffer for Operation.CREATE
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.CreateDialogScreen
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun renameButton() {
        viewModelScope.launch {
            taskType = TaskType.RENAME
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.RenameDialogScreen(buffer.first().name)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun deleteButton() {
        viewModelScope.launch {
            taskType = TaskType.DELETE
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.DeleteDialogScreen(buffer.first().name, buffer.size)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun selectAllButton() {
        viewModelScope.launch {
            _viewEvent.send(ExplorerViewEvent.SelectAll)
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
                val screen = ExplorerScreen.PropertiesDialogScreen(fileModel)
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
            _viewEvent.send(ExplorerViewEvent.CopyPath(fileModel))
            initialState()
        }
    }

    private fun compressButton() {
        viewModelScope.launch {
            taskType = TaskType.COMPRESS
            buffer.replaceList(selection)
            selection.replaceList(emptyList())
            refreshActionBar()

            val screen = ExplorerScreen.CompressDialogScreen
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    private fun openFileAs(event: ExplorerIntent.OpenFileWith) {
        viewModelScope.launch {
            val fileModel = event.fileModel ?: selection.first()
            _viewEvent.send(ExplorerViewEvent.OpenFileWith(fileModel))
            _viewEvent.send(ViewEvent.PopBackStack())
            initialState()
        }
    }

    private fun openFile(event: ExplorerIntent.OpenFile) {
        viewModelScope.launch {
            when (event.fileModel.type) {
                FileType.ARCHIVE -> extractFile(ExplorerIntent.ExtractFile(event.fileModel))
                FileType.DEFAULT,
                FileType.TEXT -> {
                    editorInteractor.openFile(event.fileModel)
                    _viewEvent.send(ViewEvent.PopBackStack())
                }
                else -> openFileAs(ExplorerIntent.OpenFileWith(event.fileModel))
            }
        }
    }

    private fun createFile(event: ExplorerIntent.CreateFile) {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack()) // close dialog

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

            val taskId = explorerRepository.createFile(child)
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))
            initialState()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> handleTaskError(status.exception)
                    is TaskStatus.Done -> handleTaskDone()
                    else -> Unit
                }
            }
        }
    }

    private fun renameFile(event: ExplorerIntent.RenameFile) {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack()) // close dialog

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

            val taskId = explorerRepository.renameFile(originalFile, renamedFile)
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))
            initialState()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> handleTaskError(status.exception)
                    is TaskStatus.Done -> handleTaskDone()
                    else -> Unit
                }
            }
        }
    }

    private fun deleteFile() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack()) // close dialog

            val taskId = explorerRepository.deleteFiles(buffer.toList())
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))
            initialState()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> handleTaskError(status.exception)
                    is TaskStatus.Done -> handleTaskDone()
                    else -> Unit
                }
            }
        }
    }

    private fun cutFile() {
        viewModelScope.launch {
            val taskId = explorerRepository.cutFiles(buffer.toList(), breadcrumbs.last())
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))
            initialState()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> handleTaskError(status.exception)
                    is TaskStatus.Done -> handleTaskDone()
                    else -> Unit
                }
            }
        }
    }

    private fun copyFile() {
        viewModelScope.launch {
            val taskId = explorerRepository.copyFiles(buffer.toList(), breadcrumbs.last())
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))
            initialState()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> handleTaskError(status.exception)
                    is TaskStatus.Done -> handleTaskDone()
                    else -> Unit
                }
            }
        }
    }

    private fun compressFile(event: ExplorerIntent.CompressFile) {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack()) // close dialog

            val isValid = event.fileName.isValidFileName()
            if (!isValid) {
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_invalid_file_name)),
                )
                return@launch
            }
            val parent = breadcrumbs.last()
            val child = parent.copy(parent.path + "/" + event.fileName)

            val taskId = explorerRepository.compressFiles(buffer.toList(), child)
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))
            initialState()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> handleTaskError(status.exception)
                    is TaskStatus.Done -> handleTaskDone()
                    else -> Unit
                }
            }
        }
    }

    private fun extractFile(event: ExplorerIntent.ExtractFile) {
        viewModelScope.launch {
            val taskId = explorerRepository.extractFiles(event.fileModel, breadcrumbs.last())
            val screen = ExplorerScreen.TaskDialogScreen(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))
            initialState()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> handleTaskError(status.exception)
                    is TaskStatus.Done -> handleTaskDone()
                    else -> Unit
                }
            }
        }
    }

    private fun initialState() {
        taskType = TaskType.CREATE
        buffer.replaceList(emptyList())
        selection.replaceList(emptyList())
        refreshActionBar()
    }

    private fun refreshActionBar() {
        _toolbarViewState.value = ToolbarViewState.ActionBar(
            breadcrumbs = breadcrumbs.toList(),
            selection = selection.toList(),
            taskType = taskType,
        )
    }*/
}