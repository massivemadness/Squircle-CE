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

package com.blacksquircle.ui.feature.explorer.ui.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_COMPACT_PACKAGES
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_FOLDERS_ON_TOP
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_SHOW_HIDDEN_FILES
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_SORT_MODE
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.AddWorkspaceDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.AuthDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.CloneRepoDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.CompressDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.CreateDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteWorkspaceDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.PropertiesDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.RenameDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.StorageDeniedDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.TaskDialog
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderOptions
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.data.node.ensureCommonParentKey
import com.blacksquircle.ui.feature.explorer.data.node.findNodeByKey
import com.blacksquircle.ui.feature.explorer.data.node.findParentKey
import com.blacksquircle.ui.feature.explorer.data.node.removeNode
import com.blacksquircle.ui.feature.explorer.data.node.updateNode
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.feature.servers.api.navigation.ServerDialog
import com.blacksquircle.ui.filesystem.base.exception.AuthRequiredException
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.exception.EncryptedArchiveException
import com.blacksquircle.ui.filesystem.base.exception.FileAlreadyExistsException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.exception.InvalidArchiveException
import com.blacksquircle.ui.filesystem.base.exception.SplitArchiveException
import com.blacksquircle.ui.filesystem.base.exception.UnsupportedArchiveException
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.filesystem.base.model.FilesystemType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
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
import javax.inject.Provider
import com.blacksquircle.ui.ds.R as UiR

internal class ExplorerViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
    private val taskManager: TaskManager,
    private val editorInteractor: EditorInteractor,
    private val explorerRepository: ExplorerRepository,
    private val serverInteractor: ServerInteractor,
    private val asyncNodeBuilder: AsyncNodeBuilder,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ExplorerViewState())
    val viewState: StateFlow<ExplorerViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val cache = HashMap<NodeKey, List<FileNode>>(128)
    private var selectedNodes: List<FileNode> = emptyList()
    private var taskType: TaskType = TaskType.CREATE
    private var taskBuffer: List<FileNode> = emptyList()
    private var workspaces: List<WorkspaceModel> = emptyList()
    private var selectedWorkspace: WorkspaceModel? = null
    private var searchQuery: String = ""

    private var showHidden = settingsManager.showHidden
    private var foldersOnTop = settingsManager.foldersOnTop
    private var compactPackages = settingsManager.compactPackages
    private var sortMode = SortMode.of(settingsManager.sortMode)

    init {
        loadWorkspaces()
        registerOnPreferenceChangeListeners()
    }

    override fun onCleared() {
        super.onCleared()
        unregisterOnPreferenceChangeListeners()
    }

    fun onBackClicked() {
        if (selectedNodes.isNotEmpty()) {
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(selectedNodes = selectedNodes)
            }
        } else {
            viewModelScope.launch {
                _viewEvent.send(ViewEvent.PopBackStack)
            }
        }
    }

    fun onWorkspaceClicked(workspace: WorkspaceModel) {
        viewModelScope.launch {
            try {
                if (workspace.uuid == selectedWorkspace?.uuid) {
                    return@launch
                }

                settingsManager.workspace = workspace.uuid
                selectedWorkspace = workspace

                cache.clear()
                resetBuffer()

                val rootNode = FileNode(
                    file = workspace.defaultLocation,
                    isExpanded = true,
                    isLoading = true,
                )
                cache[NodeKey.Root] = listOf(rootNode)

                _viewState.update {
                    it.copy(
                        selectedWorkspace = selectedWorkspace,
                        fileNodes = listOf(rootNode),
                    )
                }
                loadFiles(rootNode)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    fun onAddWorkspaceClicked() {
        viewModelScope.launch {
            val screen = AddWorkspaceDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onDeleteWorkspaceClicked(workspace: WorkspaceModel) {
        viewModelScope.launch {
            when (workspace.filesystemType) {
                FilesystemType.LOCAL -> {
                    if (workspace.uuid != LocalFilesystem.LOCAL_UUID) {
                        val screen = DeleteWorkspaceDialog(workspace.uuid, workspace.name)
                        _viewEvent.send(ViewEvent.Navigation(screen))
                    }
                }
                FilesystemType.ROOT -> Unit
                FilesystemType.SERVER -> {
                    val screen = ServerDialog(workspace.uuid)
                    _viewEvent.send(ViewEvent.Navigation(screen))
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        searchQuery = query
        _viewState.update {
            it.copy(searchQuery = searchQuery)
        }
        viewModelScope.launch {
            updateNodeList()
        }
    }

    fun onClearQueryClicked() {
        searchQuery = ""
        _viewState.update {
            it.copy(searchQuery = searchQuery)
        }
        viewModelScope.launch {
            updateNodeList()
        }
    }

    fun onShowHiddenClicked() {
        this.showHidden = !showHidden
        settingsManager.showHidden = showHidden
        _viewState.update {
            it.copy(showHidden = showHidden)
        }
        viewModelScope.launch {
            updateNodeList()
        }
    }

    fun onCompactPackagesClicked() {
        this.compactPackages = !compactPackages
        settingsManager.compactPackages = compactPackages
        _viewState.update {
            it.copy(compactPackages = compactPackages)
        }
        viewModelScope.launch {
            updateNodeList()
        }
    }

    fun onSortModeSelected(sortMode: SortMode) {
        this.sortMode = sortMode
        settingsManager.sortMode = sortMode.value
        _viewState.update {
            it.copy(sortMode = sortMode)
        }
        viewModelScope.launch {
            updateNodeList()
        }
    }

    fun onFileClicked(fileNode: FileNode) {
        if (selectedNodes.isNotEmpty()) {
            onFileSelected(fileNode)
            return
        }
        viewModelScope.launch {
            if (fileNode.isDirectory) {
                val cacheNode = cache[fileNode.key]
                if (cacheNode != null) {
                    cache.updateNode(fileNode) {
                        it.copy(isExpanded = !fileNode.isExpanded)
                    }
                    updateNodeList()
                } else {
                    cache.updateNode(fileNode) {
                        it.copy(isExpanded = true)
                    }
                    loadFiles(fileNode)
                }
            } else {
                when (fileNode.file.type) {
                    FileType.ARCHIVE -> extractFiles(fileNode)
                    FileType.DEFAULT,
                    FileType.TEXT -> {
                        editorInteractor.openFile(fileNode.file)
                        _viewEvent.send(ViewEvent.PopBackStack)
                    }
                    else -> onOpenWithClicked(fileNode)
                }
            }
        }
    }

    fun onFileSelected(fileNode: FileNode) {
        val anySelected = selectedNodes.isNotEmpty()
        val rootSelected = selectedNodes.any(FileNode::isRoot)

        if (fileNode.isRoot && anySelected && !rootSelected) return
        if (!fileNode.isRoot && rootSelected) return

        val index = selectedNodes.indexOf { it.key == fileNode.key }
        if (index == -1) {
            selectedNodes += fileNode
        } else {
            selectedNodes -= fileNode
        }
        _viewState.update {
            it.copy(selectedNodes = selectedNodes)
        }
    }

    fun onRefreshClicked() {
        viewModelScope.launch {
            val fileNode = selectedNodes.firstOrNull() ?: return@launch
            loadFiles(fileNode)

            selectedNodes = emptyList()
            _viewState.update {
                it.copy(selectedNodes = selectedNodes)
            }
        }
    }

    fun onCreateClicked() {
        viewModelScope.launch {
            val fileNode = selectedNodes.firstOrNull() ?: return@launch

            taskType = TaskType.CREATE
            taskBuffer = listOf(fileNode)
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedNodes = selectedNodes,
                )
            }

            val screen = CreateDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onCloneClicked() {
        viewModelScope.launch {
            val fileNode = selectedNodes.firstOrNull() ?: return@launch

            taskType = TaskType.CLONE
            taskBuffer = listOf(fileNode)
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedNodes = selectedNodes,
                )
            }

            val screen = CloneRepoDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onRenameClicked() {
        viewModelScope.launch {
            val fileNode = selectedNodes.firstOrNull() ?: return@launch

            taskType = TaskType.RENAME
            taskBuffer = listOf(fileNode)
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedNodes = selectedNodes,
                )
            }

            val screen = RenameDialog(fileNode.file.name)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            taskType = TaskType.DELETE
            taskBuffer = selectedNodes.toList()
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedNodes = selectedNodes,
                )
            }

            val fileName = taskBuffer.first().file.name
            val screen = DeleteDialog(fileName, taskBuffer.size)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onCopyClicked() {
        viewModelScope.launch {
            taskType = TaskType.COPY
            taskBuffer = selectedNodes.toList()
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedNodes = selectedNodes,
                )
            }
            val message = stringProvider.getString(R.string.message_select_folder_to_paste)
            _viewEvent.send(ViewEvent.Toast(message))
        }
    }

    fun onCutClicked() {
        viewModelScope.launch {
            taskType = TaskType.MOVE
            taskBuffer = selectedNodes.toList()
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedNodes = selectedNodes,
                )
            }
            val message = stringProvider.getString(R.string.message_select_folder_to_paste)
            _viewEvent.send(ViewEvent.Toast(message))
        }
    }

    fun onPasteClicked() {
        when (taskType) {
            TaskType.MOVE -> moveFiles()
            TaskType.COPY -> copyFiles()
            else -> Unit
        }
    }

    fun onOpenWithClicked(fileNode: FileNode? = null) {
        viewModelScope.launch {
            val source = fileNode ?: selectedNodes.firstOrNull()
            if (source != null) {
                _viewEvent.send(ExplorerViewEvent.OpenFileWith(source.file))
            }
            resetBuffer()
        }
    }

    fun onPropertiesClicked() {
        viewModelScope.launch {
            val fileNode = selectedNodes.firstOrNull() ?: return@launch
            val screen = PropertiesDialog(
                fileName = fileNode.file.name,
                filePath = fileNode.file.path,
                fileSize = fileNode.file.size,
                lastModified = fileNode.file.lastModified,
                permission = fileNode.file.permission,
            )
            _viewEvent.send(ViewEvent.Navigation(screen))
            resetBuffer()
        }
    }

    fun onCopyPathClicked() {
        viewModelScope.launch {
            val fileNode = selectedNodes.firstOrNull() ?: return@launch
            _viewEvent.send(ExplorerViewEvent.CopyPath(fileNode.file))
            resetBuffer()
        }
    }

    fun onCompressClicked() {
        viewModelScope.launch {
            if (!cache.ensureCommonParentKey(selectedNodes)) {
                val message = stringProvider.getString(R.string.message_same_directory_required)
                _viewEvent.send(ViewEvent.Toast(message))
                return@launch
            }

            taskType = TaskType.COMPRESS
            taskBuffer = selectedNodes.toList()
            selectedNodes = emptyList()
            _viewState.update {
                it.copy(
                    taskType = taskType,
                    selectedNodes = selectedNodes,
                )
            }

            val screen = CompressDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onClearBufferClicked() {
        taskType = TaskType.CREATE
        taskBuffer = emptyList()
        selectedNodes = emptyList()
        _viewState.update {
            it.copy(
                taskType = taskType,
                selectedNodes = selectedNodes,
            )
        }
    }

    // region ERROR_ACTION

    fun onErrorActionClicked(errorAction: ErrorAction) {
        viewModelScope.launch {
            when (errorAction) {
                ErrorAction.REQUEST_PERMISSIONS -> {
                    _viewEvent.send(ExplorerViewEvent.RequestPermission)
                }
                ErrorAction.ENTER_PASSWORD -> {
                    val screen = AuthDialog(AuthMethod.PASSWORD)
                    _viewEvent.send(ViewEvent.Navigation(screen))
                }
                ErrorAction.ENTER_PASSPHRASE -> {
                    val screen = AuthDialog(AuthMethod.KEY)
                    _viewEvent.send(ViewEvent.Navigation(screen))
                }
                ErrorAction.UNDEFINED -> Unit
            }
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            val screen = StorageDeniedDialog
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onPermissionGranted() {
        val rootNode = cache[NodeKey.Root]?.firstOrNull()
        if (rootNode != null) {
            loadFiles(rootNode)
        }
    }

    fun onCredentialsEntered(credentials: String) {
        viewModelScope.launch {
            try {
                selectedWorkspace?.let { workspace ->
                    serverInteractor.authenticate(workspace.uuid, credentials)
                    val rootNode = cache[NodeKey.Root]?.firstOrNull()
                    if (rootNode != null) {
                        loadFiles(rootNode)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    // endregion

    fun createFile(fileName: String) {
        viewModelScope.launch {
            val parentNode = taskBuffer.firstOrNull() ?: return@launch

            val taskId = explorerRepository.createFile(parentNode.file, fileName, isFolder = false)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        loadFiles(parentNode)
                        onTaskFinished()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun createFolder(fileName: String) {
        viewModelScope.launch {
            val parentNode = taskBuffer.firstOrNull() ?: return@launch

            val taskId = explorerRepository.createFile(parentNode.file, fileName, isFolder = true)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        loadFiles(parentNode)
                        onTaskFinished()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun cloneRepository(url: String, submodules: Boolean) {
        viewModelScope.launch {
            val parentNode = taskBuffer.firstOrNull() ?: return@launch

            val taskId = explorerRepository.cloneRepository(parentNode.file, url, submodules)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        loadFiles(parentNode)
                        onTaskFinished()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun renameFile(fileName: String) {
        viewModelScope.launch {
            val fileNode = taskBuffer.firstOrNull() ?: return@launch

            val taskId = explorerRepository.renameFile(fileNode.file, fileName)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        onTaskFinished()

                        val parentKey = cache.findParentKey(fileNode.key) ?: return@collect
                        val parentNode = cache.findNodeByKey(parentKey) ?: return@collect
                        loadFiles(parentNode)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun deleteFile() {
        viewModelScope.launch {
            val fileNodes = taskBuffer.toList()
            val fileModels = taskBuffer.map(FileNode::file)

            val taskId = explorerRepository.deleteFiles(fileModels)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        onTaskFinished()

                        fileNodes.forEach(cache::removeNode)
                        updateNodeList()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun compressFiles(fileName: String) {
        viewModelScope.launch {
            val fileModels = taskBuffer.map(FileNode::file)
            val fileNode = taskBuffer.firstOrNull() ?: return@launch
            val parentKey = cache.findParentKey(fileNode.key) ?: return@launch
            val parentNode = cache.findNodeByKey(parentKey) ?: return@launch

            val taskId = explorerRepository.compressFiles(fileModels, parentNode.file, fileName)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        onTaskFinished()
                        loadFiles(parentNode)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun moveFiles() {
        viewModelScope.launch {
            val parentNode = selectedNodes.firstOrNull() ?: return@launch
            val fileNodes = taskBuffer.toList()
            val fileModels = taskBuffer.map(FileNode::file)

            val taskId = explorerRepository.moveFiles(fileModels, parentNode.file)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        onTaskFinished()

                        fileNodes.forEach(cache::removeNode)
                        loadFiles(parentNode)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun copyFiles() {
        viewModelScope.launch {
            val parentNode = selectedNodes.firstOrNull() ?: return@launch
            val fileModels = taskBuffer.map(FileNode::file)

            val taskId = explorerRepository.copyFiles(fileModels, parentNode.file)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        onTaskFinished()
                        loadFiles(parentNode)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun extractFiles(fileNode: FileNode) {
        viewModelScope.launch {
            val parentKey = cache.findParentKey(fileNode.key) ?: return@launch
            val parentNode = cache.findNodeByKey(parentKey) ?: return@launch

            val taskId = explorerRepository.extractFiles(fileNode.file, parentNode.file)
            val screen = TaskDialog(taskId)
            _viewEvent.send(ViewEvent.Navigation(screen))

            resetBuffer()

            taskManager.monitor(taskId).collect { task ->
                when (val status = task.status) {
                    is TaskStatus.Error -> onTaskFailed(status.exception)
                    is TaskStatus.Done -> {
                        onTaskFinished()
                        loadFiles(parentNode)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun loadFiles(fileNode: FileNode) {
        viewModelScope.launch {
            try {
                cache.updateNode(fileNode) {
                    it.copy(
                        isLoading = true,
                        errorState = null,
                    )
                }
                updateNodeList()

                val fileList = explorerRepository.listFiles(fileNode.file)
                val currentNodes = cache[fileNode.key].orEmpty()
                val updatedNodes = fileList.map { fileModel ->
                    val currentNode = currentNodes.find { it.file.fileUri == fileModel.fileUri }
                    FileNode(
                        file = fileModel,
                        depth = currentNode?.depth ?: (fileNode.depth + 1),
                        displayName = currentNode?.displayName ?: fileModel.name,
                        displayDepth = currentNode?.displayDepth ?: (fileNode.depth + 1),
                        isExpanded = currentNode?.isExpanded ?: false,
                        isLoading = currentNode?.isLoading ?: false,
                        errorState = currentNode?.errorState,
                    )
                }
                cache[fileNode.key] = updatedNodes

                cache.updateNode(fileNode) {
                    it.copy(
                        isLoading = false,
                        errorState = null,
                    )
                }

                val autoLoad = searchQuery.isBlank() &&
                    updatedNodes.size == 1 &&
                    updatedNodes[0].isDirectory &&
                    (showHidden || !updatedNodes[0].isHidden) &&
                    compactPackages

                if (autoLoad) {
                    val nestedNode = updatedNodes[0].copy(
                        displayDepth = fileNode.depth,
                    )
                    cache.updateNode(nestedNode) {
                        it.copy(isExpanded = true)
                    }
                    loadFiles(nestedNode)
                } else {
                    updateNodeList()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Timber.e(e, e.message)
                if (!fileNode.isRoot) {
                    _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
                }

                cache.updateNode(fileNode) {
                    it.copy(
                        isLoading = false,
                        errorState = errorState(e)
                    )
                }
                updateNodeList()
            }
        }
    }

    private fun loadWorkspaces() {
        viewModelScope.launch {
            try {
                explorerRepository.loadWorkspaces().collect { workspaces ->
                    this@ExplorerViewModel.workspaces = workspaces

                    val workspace = workspaces
                        .find { it.uuid == settingsManager.workspace }
                        ?: workspaces.first()

                    if (workspace.uuid != selectedWorkspace?.uuid) {
                        settingsManager.workspace = workspace.uuid
                        selectedWorkspace = workspace

                        cache.clear()
                        resetBuffer()

                        val rootNode = FileNode(
                            file = workspace.defaultLocation,
                            isExpanded = true,
                            isLoading = true,
                        )
                        cache[NodeKey.Root] = listOf(rootNode)

                        loadFiles(rootNode)
                    }

                    _viewState.update {
                        it.copy(
                            workspaces = workspaces,
                            selectedWorkspace = selectedWorkspace,
                            searchQuery = searchQuery,
                            showHidden = showHidden,
                            compactPackages = compactPackages,
                            sortMode = sortMode,
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun registerOnPreferenceChangeListeners() {
        settingsManager.registerListener(KEY_SHOW_HIDDEN_FILES) {
            val newValue = settingsManager.showHidden
            if (showHidden == newValue) {
                return@registerListener
            }
            showHidden = newValue
            _viewState.update {
                it.copy(showHidden = showHidden)
            }
            viewModelScope.launch {
                updateNodeList()
            }
        }
        settingsManager.registerListener(KEY_COMPACT_PACKAGES) {
            val newValue = settingsManager.compactPackages
            if (compactPackages == newValue) {
                return@registerListener
            }
            compactPackages = newValue
            _viewState.update {
                it.copy(compactPackages = compactPackages)
            }
            viewModelScope.launch {
                updateNodeList()
            }
        }
        settingsManager.registerListener(KEY_FOLDERS_ON_TOP) {
            val newValue = settingsManager.foldersOnTop
            if (foldersOnTop == newValue) {
                return@registerListener
            }
            foldersOnTop = newValue
            /*_viewState.update {
                it.copy(foldersOnTop = foldersOnTop)
            }*/
            viewModelScope.launch {
                updateNodeList()
            }
        }
        settingsManager.registerListener(KEY_SORT_MODE) {
            val newValue = SortMode.of(settingsManager.sortMode)
            if (sortMode == newValue) {
                return@registerListener
            }
            sortMode = newValue
            _viewState.update {
                it.copy(sortMode = sortMode)
            }
            viewModelScope.launch {
                updateNodeList()
            }
        }
    }

    private fun unregisterOnPreferenceChangeListeners() {
        settingsManager.unregisterListener(KEY_SHOW_HIDDEN_FILES)
        settingsManager.unregisterListener(KEY_COMPACT_PACKAGES)
        settingsManager.unregisterListener(KEY_FOLDERS_ON_TOP)
        settingsManager.unregisterListener(KEY_SORT_MODE)
    }

    private fun resetBuffer() {
        taskType = TaskType.CREATE
        taskBuffer = emptyList()
        selectedNodes = emptyList()
        _viewState.update {
            it.copy(
                taskType = taskType,
                selectedNodes = selectedNodes,
            )
        }
    }

    private fun errorState(e: Throwable): ErrorState {
        return when (e) {
            is PermissionException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.message_access_denied),
                subtitle = stringProvider.getString(UiR.string.message_access_required),
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
    }

    private suspend fun updateNodeList() {
        val options = NodeBuilderOptions(
            searchQuery = searchQuery,
            showHidden = showHidden,
            sortMode = sortMode,
            foldersOnTop = foldersOnTop,
            compactPackages = compactPackages,
        )
        val fileNodes = asyncNodeBuilder.buildNodeList(cache, options)
        _viewState.update {
            it.copy(fileNodes = fileNodes)
        }
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<ExplorerViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}