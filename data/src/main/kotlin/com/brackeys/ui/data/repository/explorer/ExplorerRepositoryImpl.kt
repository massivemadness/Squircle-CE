package com.brackeys.ui.data.repository.explorer

import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.data.utils.FileSorter
import com.brackeys.ui.domain.providers.coroutines.DispatcherProvider
import com.brackeys.ui.domain.repository.explorer.ExplorerRepository
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.filesystem.base.model.FileTree
import com.brackeys.ui.filesystem.base.model.PropertiesModel
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
                .sortedBy { !it.isFolder == settingsManager.foldersOnTop }
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
                offer(fileModel)
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
                offer(fileModel)
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
                offer(fileModel)
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