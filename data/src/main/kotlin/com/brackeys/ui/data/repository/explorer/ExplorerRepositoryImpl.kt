package com.brackeys.ui.data.repository.explorer

import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.data.utils.FileSorter
import com.brackeys.ui.domain.providers.coroutine.DispatcherProvider
import com.brackeys.ui.domain.repository.explorer.ExplorerRepository
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.filesystem.base.model.FileTree
import com.brackeys.ui.filesystem.base.model.PropertiesModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class ExplorerRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val filesystem: Filesystem
) : ExplorerRepository {

    override suspend fun fetchFiles(fileModel: FileModel?): FileTree {
        return withContext(dispatcherProvider.io()) {
            val defaultLocation = filesystem.defaultLocation().blockingGet()
            filesystem.provideDirectory(fileModel ?: defaultLocation)
                .map { fileTree ->
                    val newList = mutableListOf<FileModel>()
                    fileTree.children.forEach { file ->
                        if (file.isHidden) {
                            if (settingsManager.filterHidden) {
                                newList.add(file)
                            }
                        } else {
                            newList.add(file)
                        }
                    }
                    fileTree.copy(children = newList)
                }
                .map { fileTree ->
                    val comparator = FileSorter.getComparator(settingsManager.sortMode.toInt())
                    val children = fileTree.children.sortedWith(comparator)
                    fileTree.copy(children = children)
                }
                .map { fileTree ->
                    val children = fileTree.children.sortedBy { file ->
                        !file.isFolder == settingsManager.foldersOnTop
                    }
                    fileTree.copy(children = children)
                }
                .blockingGet()
        }
    }

    override suspend fun createFile(fileModel: FileModel): FileModel {
        return withContext(dispatcherProvider.io()) {
            filesystem.createFile(fileModel)
                .blockingGet()
        }
    }

    override suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel {
        return withContext(dispatcherProvider.io()) {
            filesystem.renameFile(fileModel, fileName)
                .blockingGet()
        }
    }

    override suspend fun propertiesOf(fileModel: FileModel): PropertiesModel {
        return withContext(dispatcherProvider.io()) {
            filesystem.propertiesOf(fileModel)
                .blockingGet()
        }
    }

    override suspend fun deleteFiles(source: List<FileModel>): Flow<FileModel> {
        return callbackFlow {
            source.forEach {
                val fileModel = filesystem.deleteFile(it).blockingGet()
                offer(fileModel)
                delay(20)
            }
            close()
        }.flowOn(dispatcherProvider.io())
    }

    override suspend fun copyFiles(source: List<FileModel>, destPath: String): Flow<FileModel> {
        return callbackFlow {
            val dest = filesystem.provideFile(destPath).blockingGet()
            source.forEach {
                val fileModel = filesystem.copyFile(it, dest).blockingGet()
                offer(fileModel)
                delay(20)
            }
            close()
        }.flowOn(dispatcherProvider.io())
    }

    override suspend fun cutFiles(source: List<FileModel>, destPath: String): Flow<FileModel> {
        return callbackFlow {
            val dest = filesystem.provideFile(destPath).blockingGet()
            source.forEach { fileModel ->
                filesystem.copyFile(fileModel, dest).blockingGet()
                filesystem.deleteFile(fileModel).blockingGet()
                offer(fileModel)
                delay(20)
            }
            close()
        }.flowOn(dispatcherProvider.io())
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            filesystem.compress(source, dest)
                .subscribeBy(
                    onError = { throw it },
                    onNext = { offer(it) },
                    onComplete = { close() }
                )
        }.flowOn(dispatcherProvider.io())
    }

    override suspend fun extractAll(source: FileModel, dest: FileModel): FileModel {
        return withContext(dispatcherProvider.io()) {
            filesystem.extractAll(source, dest)
                .blockingGet()
        }
    }
}