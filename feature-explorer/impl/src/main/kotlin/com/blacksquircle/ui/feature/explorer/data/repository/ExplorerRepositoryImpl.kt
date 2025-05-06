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

package com.blacksquircle.ui.feature.explorer.data.repository

import android.content.Context
import com.blacksquircle.ui.core.database.dao.workspace.WorkspaceDao
import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.isStorageAccessGranted
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.mapper.WorkspaceMapper
import com.blacksquircle.ui.feature.explorer.data.utils.createLocalWorkspace
import com.blacksquircle.ui.feature.explorer.data.utils.createRootWorkspace
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FilesystemType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

internal class ExplorerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val taskManager: TaskManager,
    private val gitInteractor: GitInteractor,
    private val serverInteractor: ServerInteractor,
    private val filesystemFactory: FilesystemFactory,
    private val workspaceDao: WorkspaceDao,
    private val rootBeer: RootBeer,
    private val context: Context,
) : ExplorerRepository {

    override suspend fun loadWorkspaces(): Flow<List<WorkspaceModel>> {
        return combine(
            workspaceDao.flowAll(),
            serverInteractor.flowAll(),
        ) { workspaces, servers ->
            val defaultWorkspaces = buildList {
                add(context.createLocalWorkspace())
                if (rootBeer.isRooted) {
                    add(context.createRootWorkspace())
                }
            }
            val userWorkspaces = workspaces.map(WorkspaceMapper::toModel)
            val serverWorkspaces = servers.map(WorkspaceMapper::toModel)
            defaultWorkspaces + userWorkspaces + serverWorkspaces
        }
    }

    override suspend fun createWorkspace(filePath: String) {
        withContext(dispatcherProvider.io()) {
            val defaultLocation = FileModel(
                fileUri = LocalFilesystem.LOCAL_SCHEME + filePath,
                filesystemUuid = LocalFilesystem.LOCAL_UUID,
                isDirectory = true,
            )
            val workspace = WorkspaceModel(
                uuid = UUID.randomUUID().toString(),
                name = defaultLocation.name,
                filesystemType = FilesystemType.LOCAL,
                defaultLocation = defaultLocation,
            )
            val workspaceEntity = WorkspaceMapper.toEntity(workspace)
            workspaceDao.insert(workspaceEntity)
        }
    }

    override suspend fun deleteWorkspace(uuid: String) {
        withContext(dispatcherProvider.io()) {
            if (uuid == LocalFilesystem.LOCAL_UUID) {
                return@withContext
            }
            workspaceDao.delete(uuid)
        }
    }

    override suspend fun listFiles(parent: FileModel): List<FileModel> {
        return withContext(dispatcherProvider.io()) {
            if (!context.isStorageAccessGranted()) {
                throw PermissionException()
            }
            val filesystem = currentFilesystem()
            filesystem.listFiles(parent)
        }
    }

    override fun createFile(parent: FileModel, fileName: String, isFolder: Boolean): String {
        return taskManager.execute(TaskType.CREATE) { update ->
            val filesystem = currentFilesystem()
            val fileModel = parent.copy(
                fileUri = parent.fileUri + File.separator + fileName,
                isDirectory = isFolder,
            )

            val progress = TaskStatus.Progress(
                count = 1,
                totalCount = 1,
                details = fileModel.path
            )
            update(progress)

            filesystem.createFile(fileModel)
            delay(100)
        }
    }

    override fun renameFile(source: FileModel, fileName: String): String {
        return taskManager.execute(TaskType.RENAME) { update ->
            val filesystem = currentFilesystem()
            val progress = TaskStatus.Progress(
                count = 1,
                totalCount = 1,
                details = source.path
            )
            update(progress)

            filesystem.renameFile(source, fileName)
            delay(100)
        }
    }

    override fun deleteFiles(source: List<FileModel>): String {
        return taskManager.execute(TaskType.DELETE) { update ->
            val filesystem = currentFilesystem()
            source.forEachIndexed { index, fileModel ->
                val progress = TaskStatus.Progress(
                    count = index + 1,
                    totalCount = source.size,
                    details = fileModel.path,
                )
                update(progress)

                filesystem.deleteFile(fileModel)
                delay(100)
            }
        }
    }

    override fun copyFiles(source: List<FileModel>, dest: FileModel): String {
        return taskManager.execute(TaskType.COPY) { update ->
            val filesystem = currentFilesystem()
            source.forEachIndexed { index, fileModel ->
                val progress = TaskStatus.Progress(
                    count = index + 1,
                    totalCount = source.size,
                    details = fileModel.path,
                )
                update(progress)

                filesystem.copyFile(fileModel, dest)
                delay(100)
            }
        }
    }

    override fun moveFiles(source: List<FileModel>, dest: FileModel): String {
        return taskManager.execute(TaskType.MOVE) { update ->
            val filesystem = currentFilesystem()
            source.forEachIndexed { index, fileModel ->
                val progress = TaskStatus.Progress(
                    count = index + 1,
                    totalCount = source.size,
                    details = fileModel.path,
                )
                update(progress)

                filesystem.copyFile(fileModel, dest)
                filesystem.deleteFile(fileModel)
                delay(100)
            }
        }
    }

    override fun compressFiles(source: List<FileModel>, dest: FileModel, fileName: String): String {
        return taskManager.execute(TaskType.COMPRESS) { update ->
            val filesystem = currentFilesystem()
            val child = dest.copy(
                fileUri = dest.fileUri + File.separator + fileName,
                isDirectory = false,
            )

            filesystem.compressFiles(source, child)
                .collectIndexed { index, fileModel ->
                    val progress = TaskStatus.Progress(
                        count = index + 1,
                        totalCount = source.size,
                        details = fileModel.path,
                    )
                    update(progress)
                    delay(100)
                }
        }
    }

    override fun extractFiles(source: FileModel, dest: FileModel): String {
        return taskManager.execute(TaskType.EXTRACT) { update ->
            val filesystem = currentFilesystem()
            filesystem.extractFiles(source, dest)
                .onStart {
                    val progress = TaskStatus.Progress(
                        count = -1,
                        totalCount = -1,
                        details = source.path
                    )
                    update(progress)
                }
                .collect()
        }
    }

    override fun cloneRepository(parent: FileModel, url: String, submodules: Boolean): String {
        return taskManager.execute(TaskType.CLONE) { update ->
            gitInteractor.cloneRepository(parent, url, submodules).collect { details ->
                val progress = TaskStatus.Progress(
                    count = -1,
                    totalCount = -1,
                    details = details,
                )
                update(progress)
            }
        }
    }

    private suspend fun currentFilesystem(): Filesystem {
        val workspaceId = settingsManager.workspace
        val filesystemUuid = workspaceDao.load(workspaceId)
            ?.filesystemUuid // user-defined folder
            ?: workspaceId // if not found, it's a serverId
        return filesystemFactory.create(filesystemUuid)
    }
}