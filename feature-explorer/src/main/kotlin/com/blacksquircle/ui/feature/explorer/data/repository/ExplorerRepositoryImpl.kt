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

package com.blacksquircle.ui.feature.explorer.data.repository

import android.content.Context
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.ui.extensions.checkStorageAccess
import com.blacksquircle.ui.feature.explorer.data.utils.fileComparator
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.worker.CreateFileWorker
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.RestrictedException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileTree
import com.blacksquircle.ui.filesystem.base.model.PropertiesModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ExplorerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val filesystem: Filesystem,
    private val context: Context,
) : ExplorerRepository {

    override suspend fun listFiles(parent: FileModel?): FileTree {
        return withContext(dispatcherProvider.io()) {
            context.checkStorageAccess(
                onSuccess = {
                    val fileTree = filesystem.provideDirectory(parent ?: filesystem.defaultLocation())
                    fileTree.copy(children = fileTree.children
                        .filter { if (it.isHidden) settingsManager.filterHidden else true }
                        .sortedWith(fileComparator(settingsManager.sortMode.toInt()))
                        .sortedBy { it.isFolder != settingsManager.foldersOnTop }
                    )
                },
                onFailure = {
                    throw RestrictedException()
                }
            )
        }
    }

    override suspend fun createFile(fileModel: FileModel) {
        return withContext(dispatcherProvider.io()) {
            context.checkStorageAccess(
                onSuccess = { CreateFileWorker.scheduleJob(context, fileModel) },
                onFailure = { throw RestrictedException() }
            )
        }
    }

    override suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel {
        return withContext(dispatcherProvider.io()) {
            context.checkStorageAccess(
                onSuccess = { filesystem.renameFile(fileModel, fileName) },
                onFailure = { throw RestrictedException() }
            )
        }
    }

    override suspend fun propertiesOf(fileModel: FileModel): PropertiesModel {
        return withContext(dispatcherProvider.io()) {
            context.checkStorageAccess(
                onSuccess = { filesystem.propertiesOf(fileModel) },
                onFailure = { throw RestrictedException() }
            )
        }
    }

    override suspend fun deleteFiles(source: List<FileModel>): Flow<FileModel> {
        return context.checkStorageAccess(
            onSuccess = {
                callbackFlow {
                    source.forEach {
                        val fileModel = filesystem.deleteFile(it)
                        send(fileModel)
                        delay(20)
                    }
                    close()
                }.flowOn(dispatcherProvider.io())
            },
            onFailure = {
                throw RestrictedException()
            }
        )
    }

    override suspend fun copyFiles(source: List<FileModel>, destPath: String): Flow<FileModel> {
        return context.checkStorageAccess(
            onSuccess = {
                callbackFlow {
                    val dest = filesystem.provideFile(destPath)
                    source.forEach {
                        val fileModel = filesystem.copyFile(it, dest)
                        send(fileModel)
                        delay(20)
                    }
                    close()
                }.flowOn(dispatcherProvider.io())
            },
            onFailure = {
                throw RestrictedException()
            }
        )
    }

    override suspend fun cutFiles(source: List<FileModel>, destPath: String): Flow<FileModel> {
        return context.checkStorageAccess(
            onSuccess = {
                callbackFlow {
                    val dest = filesystem.provideFile(destPath)
                    source.forEach { fileModel ->
                        filesystem.copyFile(fileModel, dest)
                        filesystem.deleteFile(fileModel)
                        send(fileModel)
                        delay(20)
                    }
                    close()
                }.flowOn(dispatcherProvider.io())
            },
            onFailure = {
                throw RestrictedException()
            }
        )
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        return context.checkStorageAccess(
            onSuccess = {
                filesystem.compress(source, dest)
                    .flowOn(dispatcherProvider.io())
            },
            onFailure = {
                throw RestrictedException()
            }
        )
    }

    override suspend fun extractAll(source: FileModel, dest: FileModel): Flow<FileModel> {
        return context.checkStorageAccess(
            onSuccess = {
                filesystem.extractAll(source, dest)
                    .flowOn(dispatcherProvider.io())
            },
            onFailure = {
                throw RestrictedException()
            }
        )
    }
}