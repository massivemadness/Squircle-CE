package com.brackeys.ui.domain.repository.explorer

import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.filesystem.base.model.FileTree
import com.brackeys.ui.filesystem.base.model.PropertiesModel
import kotlinx.coroutines.flow.Flow

interface ExplorerRepository {
    suspend fun fetchFiles(fileModel: FileModel?): FileTree

    suspend fun createFile(fileModel: FileModel): FileModel
    suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel
    suspend fun propertiesOf(fileModel: FileModel): PropertiesModel

    suspend fun deleteFiles(source: List<FileModel>): Flow<FileModel>
    suspend fun copyFiles(source: List<FileModel>, destPath: String): Flow<FileModel>
    suspend fun cutFiles(source: List<FileModel>, destPath: String): Flow<FileModel>
    suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel>
    suspend fun extractAll(source: FileModel, dest: FileModel): FileModel
}