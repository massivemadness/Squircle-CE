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

package com.blacksquircle.ui.feature.explorer.data.repository

import android.content.Context
import com.blacksquircle.ui.core.data.converter.ServerConverter
import com.blacksquircle.ui.core.data.factory.FilesystemFactory
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.ui.extensions.checkStorageAccess
import com.blacksquircle.ui.feature.explorer.data.utils.fileComparator
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.worker.*
import com.blacksquircle.ui.filesystem.base.exception.PermissionException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileTree
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ExplorerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val filesystemFactory: FilesystemFactory,
    private val context: Context,
    appDatabase: AppDatabase,
) : ExplorerRepository {

    override val serverFlow = appDatabase.serverDao().flow()
        .map { it.map(ServerConverter::toModel) }
        .flowOn(dispatcherProvider.io())

    private var currentFilesystem = settingsManager.filesystem

    override suspend fun filesystem(filesystemUuid: String) {
        return withContext(dispatcherProvider.io()) {
            settingsManager.filesystem = filesystemUuid
            currentFilesystem = filesystemUuid
        }
    }

    override suspend fun listFiles(parent: FileModel?): FileTree {
        return withContext(dispatcherProvider.io()) {
            suspendCoroutine { cont ->
                context.checkStorageAccess(
                    onSuccess = { cont.resume(Unit) },
                    onFailure = { cont.resumeWithException(PermissionException()) },
                )
            }
            val filesystem = filesystemFactory.create(currentFilesystem)
            val fileTree = filesystem.provideDirectory(parent ?: filesystem.defaultLocation())
            fileTree.copy(
                children = fileTree.children
                    .filter { if (it.isHidden) settingsManager.showHidden else true }
                    .sortedWith(fileComparator(settingsManager.sortMode.toInt()))
                    .sortedBy { it.directory != settingsManager.foldersOnTop },
            )
        }
    }

    override suspend fun createFile(fileModel: FileModel) {
        return withContext(dispatcherProvider.io()) {
            context.checkStorageAccess(
                onSuccess = { CreateFileWorker.scheduleJob(context, listOf(fileModel)) },
                onFailure = { throw PermissionException() },
            )
        }
    }

    override suspend fun renameFile(source: FileModel, dest: FileModel) {
        return withContext(dispatcherProvider.io()) {
            context.checkStorageAccess(
                onSuccess = { RenameFileWorker.scheduleJob(context, listOf(source, dest)) },
                onFailure = { throw PermissionException() },
            )
        }
    }

    override suspend fun deleteFiles(source: List<FileModel>) {
        return context.checkStorageAccess(
            onSuccess = { DeleteFileWorker.scheduleJob(context, source) },
            onFailure = { throw PermissionException() },
        )
    }

    override suspend fun copyFiles(source: List<FileModel>, dest: FileModel) {
        return context.checkStorageAccess(
            onSuccess = { CopyFileWorker.scheduleJob(context, source + dest) },
            onFailure = { throw PermissionException() },
        )
    }

    override suspend fun cutFiles(source: List<FileModel>, dest: FileModel) {
        return context.checkStorageAccess(
            onSuccess = { CutFileWorker.scheduleJob(context, source + dest) },
            onFailure = { throw PermissionException() },
        )
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel) {
        return context.checkStorageAccess(
            onSuccess = { CompressFileWorker.scheduleJob(context, source + dest) },
            onFailure = { throw PermissionException() },
        )
    }

    override suspend fun extractFiles(source: FileModel, dest: FileModel) {
        return context.checkStorageAccess(
            onSuccess = { ExtractFileWorker.scheduleJob(context, listOf(source, dest)) },
            onFailure = { throw PermissionException() },
        )
    }
}