/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.data.repository.explorer

import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.data.utils.FileSorter
import com.blacksquircle.ui.domain.providers.coroutine.DispatcherProvider
import com.blacksquircle.ui.domain.repository.explorer.ExplorerRepository
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileTree
import com.blacksquircle.ui.filesystem.base.model.PropertiesModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ExplorerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val filesystem: Filesystem
) : ExplorerRepository {

    override suspend fun fetchFiles(fileModel: FileModel?): FileTree {
        return withContext(dispatcherProvider.io()) {
            val fileTree = filesystem.provideDirectory(fileModel ?: filesystem.defaultLocation())
            fileTree.copy(children = fileTree.children
                .filter { if (it.isHidden) settingsManager.filterHidden else true }
                .sortedWith(FileSorter.getComparator(settingsManager.sortMode.toInt()))
                .sortedBy { it.isFolder != settingsManager.foldersOnTop }
            )
        }
    }

    override suspend fun createFile(fileModel: FileModel): FileModel {
        return withContext(dispatcherProvider.io()) {
            filesystem.createFile(fileModel)
        }
    }

    override suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel {
        return withContext(dispatcherProvider.io()) {
            filesystem.renameFile(fileModel, fileName)
        }
    }

    override suspend fun propertiesOf(fileModel: FileModel): PropertiesModel {
        return withContext(dispatcherProvider.io()) {
            filesystem.propertiesOf(fileModel)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun deleteFiles(source: List<FileModel>): Flow<FileModel> {
        return callbackFlow {
            source.forEach {
                val fileModel = filesystem.deleteFile(it)
                trySend(fileModel)
                delay(20)
            }
            close()
        }.flowOn(dispatcherProvider.io())
    }

    @ExperimentalCoroutinesApi
    override suspend fun copyFiles(source: List<FileModel>, destPath: String): Flow<FileModel> {
        return callbackFlow {
            val dest = filesystem.provideFile(destPath)
            source.forEach {
                val fileModel = filesystem.copyFile(it, dest)
                trySend(fileModel)
                delay(20)
            }
            close()
        }.flowOn(dispatcherProvider.io())
    }

    @ExperimentalCoroutinesApi
    override suspend fun cutFiles(source: List<FileModel>, destPath: String): Flow<FileModel> {
        return callbackFlow {
            val dest = filesystem.provideFile(destPath)
            source.forEach { fileModel ->
                filesystem.copyFile(fileModel, dest)
                filesystem.deleteFile(fileModel)
                trySend(fileModel)
                delay(20)
            }
            close()
        }.flowOn(dispatcherProvider.io())
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        return filesystem.compress(source, dest)
            .flowOn(dispatcherProvider.io())
    }

    override suspend fun extractAll(source: FileModel, dest: FileModel): Flow<FileModel> {
        return filesystem.extractAll(source, dest)
            .flowOn(dispatcherProvider.io())
    }
}