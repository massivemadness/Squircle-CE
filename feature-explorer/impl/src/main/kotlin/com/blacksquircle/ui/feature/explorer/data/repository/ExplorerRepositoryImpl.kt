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
import com.blacksquircle.ui.core.extensions.checkStoragePermissions
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.filesystem.base.model.FileModel
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
    private val filesystemFactory: FilesystemFactory,
    private val context: Context,
) : ExplorerRepository {

    private val currentFilesystem: String
        get() = settingsManager.filesystem

    override suspend fun listFiles(parent: FileModel?): List<FileModel> {
        return withContext(dispatcherProvider.io()) {
            context.checkStoragePermissions() // throws exception
            val filesystem = filesystemFactory.create(currentFilesystem)
            val directory = parent ?: filesystem.defaultLocation()
            filesystem.provideDirectory(directory)
        }
    }

    override fun createFile(parent: FileModel?, fileName: String, isFolder: Boolean): String {
        return taskManager.execute(TaskType.CREATE) { update ->
            val filesystem = filesystemFactory.create(currentFilesystem)
            val directory = parent ?: filesystem.defaultLocation()
            val fileModel = directory.copy(
                fileUri = directory.fileUri + File.separator + fileName,
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
            val fileModel = source.copy(
                fileUri = source.fileUri.substringBeforeLast(File.separator) +
                    File.separator + fileName,
                directory = source.directory,
            )

            val progress = TaskStatus.Progress(
                count = 1,
                totalCount = 1,
                details = source.path
            )
            update(progress)

            filesystem.renameFile(source, fileModel)
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

    override fun copyFiles(source: List<FileModel>, dest: FileModel?): String {
        return taskManager.execute(TaskType.COPY) { update ->
            val filesystem = filesystemFactory.create(currentFilesystem)
            val directory = dest ?: filesystem.defaultLocation()
            source.forEachIndexed { index, fileModel ->
                val progress = TaskStatus.Progress(
                    count = index + 1,
                    totalCount = source.size,
                    details = fileModel.path,
                )
                update(progress)

                filesystem.copyFile(fileModel, directory)
                delay(100)
            }
        }
    }

    override fun cutFiles(source: List<FileModel>, dest: FileModel?): String {
        return taskManager.execute(TaskType.CUT) { update ->
            val filesystem = filesystemFactory.create(currentFilesystem)
            val directory = dest ?: filesystem.defaultLocation()
            source.forEachIndexed { index, fileModel ->
                val progress = TaskStatus.Progress(
                    count = index + 1,
                    totalCount = source.size,
                    details = fileModel.path,
                )
                update(progress)

                filesystem.copyFile(fileModel, directory)
                filesystem.deleteFile(fileModel)
                delay(100)
            }
        }
    }

    override fun compressFiles(source: List<FileModel>, dest: FileModel?, fileName: String): String {
        return taskManager.execute(TaskType.COMPRESS) { update ->
            val filesystem = filesystemFactory.create(currentFilesystem)
            val directory = dest ?: filesystem.defaultLocation()
            val child = directory.copy(
                fileUri = directory.fileUri + "/" + fileName,
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

    override fun extractFiles(source: FileModel, dest: FileModel?): String {
        return taskManager.execute(TaskType.EXTRACT) { update ->
            val filesystem = filesystemFactory.create(currentFilesystem)
            val directory = dest ?: filesystem.defaultLocation()
            filesystem.extractFiles(source, directory)
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