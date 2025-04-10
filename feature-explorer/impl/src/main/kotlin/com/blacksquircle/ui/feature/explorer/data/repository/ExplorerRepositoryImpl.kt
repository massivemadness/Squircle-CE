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

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.blacksquircle.ui.core.database.dao.path.PathDao
import com.blacksquircle.ui.core.extensions.isPermissionGranted
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.mapper.FileMapper
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.base.exception.PermissionException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File

internal class ExplorerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val taskManager: TaskManager,
    private val serverInteractor: ServerInteractor,
    private val filesystemFactory: FilesystemFactory,
    private val pathDao: PathDao,
    private val context: Context,
) : ExplorerRepository {

    private val currentFilesystem: String
        get() = settingsManager.filesystem

    override suspend fun loadFilesystems(): List<FilesystemModel> {
        return withContext(dispatcherProvider.io()) {
            val defaultFilesystems = listOf(
                FilesystemModel(
                    uuid = LocalFilesystem.LOCAL_UUID,
                    title = context.getString(R.string.storage_local),
                    defaultLocation = FileModel(
                        fileUri = LocalFilesystem.LOCAL_SCHEME +
                            Environment.getExternalStorageDirectory().absolutePath,
                        filesystemUuid = LocalFilesystem.LOCAL_UUID,
                    ),
                ),
                FilesystemModel(
                    uuid = RootFilesystem.ROOT_UUID,
                    title = context.getString(R.string.storage_root),
                    defaultLocation = FileModel(
                        fileUri = RootFilesystem.ROOT_SCHEME,
                        filesystemUuid = RootFilesystem.ROOT_UUID,
                    ),
                ),
            )
            val serverFilesystems = serverInteractor.loadServers().map { config ->
                val scheme = config.scheme.value
                val path = config.initialDir.trim(File.separatorChar)
                val fileUri = if (path.isNotEmpty()) scheme + File.separator + path else scheme
                FilesystemModel(
                    uuid = config.uuid,
                    title = config.name,
                    defaultLocation = FileModel(
                        fileUri = fileUri,
                        filesystemUuid = config.uuid,
                    ),
                )
            }

            defaultFilesystems + serverFilesystems
        }
    }

    override suspend fun loadBreadcrumbs(filesystemModel: FilesystemModel): List<FileModel> {
        return withContext(dispatcherProvider.io()) {
            val pathEntity = pathDao.load(filesystemModel.uuid)
                ?: return@withContext listOf(filesystemModel.defaultLocation)

            val fileModel = FileMapper.toModel(pathEntity)
            val defaultLocation = filesystemModel.defaultLocation

            val fileUri = Uri.parse(fileModel.fileUri)
            val defaultUri = Uri.parse(defaultLocation.fileUri)

            val scheme = "${fileUri.scheme}://"
            val filePath = fileUri.path.orEmpty()
            val defaultPath = defaultUri.path.orEmpty()
            val base = defaultPath.trim(File.separatorChar)
            val parts = filePath
                .removePrefix(defaultPath)
                .trim(File.separatorChar)
                .split(File.separator)
                .filterNot(String::isEmpty)

            val defaultList = listOf(defaultLocation)
            val pathParts = parts.indices.map { index ->
                val part = parts.subList(0, index + 1).joinToString(File.separator)
                FileModel(
                    fileUri = scheme + File.separator + base + File.separator + part,
                    filesystemUuid = filesystemModel.uuid,
                )
            }

            defaultList + pathParts
        }
    }

    override suspend fun listFiles(parent: FileModel): List<FileModel> {
        return withContext(dispatcherProvider.io()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    throw PermissionException()
                }
            } else {
                if (!context.isPermissionGranted(WRITE_EXTERNAL_STORAGE)) {
                    throw PermissionException()
                }
            }

            val filesystem = filesystemFactory.create(currentFilesystem)
            val entity = FileMapper.toEntity(parent)
            pathDao.insert(entity)

            filesystem.listFiles(parent)
        }
    }

    override fun createFile(parent: FileModel, fileName: String, isFolder: Boolean): String {
        return taskManager.execute(TaskType.CREATE) { update ->
            val filesystem = filesystemFactory.create(currentFilesystem)
            val fileModel = parent.copy(
                fileUri = parent.fileUri + File.separator + fileName,
                directory = isFolder,
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
            val filesystem = filesystemFactory.create(currentFilesystem)
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
            val filesystem = filesystemFactory.create(currentFilesystem)
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
            val filesystem = filesystemFactory.create(currentFilesystem)
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

    override fun cutFiles(source: List<FileModel>, dest: FileModel): String {
        return taskManager.execute(TaskType.CUT) { update ->
            val filesystem = filesystemFactory.create(currentFilesystem)
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
            val filesystem = filesystemFactory.create(currentFilesystem)
            val child = dest.copy(
                fileUri = dest.fileUri + File.separator + fileName,
                directory = false,
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
            val filesystem = filesystemFactory.create(currentFilesystem)
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
}