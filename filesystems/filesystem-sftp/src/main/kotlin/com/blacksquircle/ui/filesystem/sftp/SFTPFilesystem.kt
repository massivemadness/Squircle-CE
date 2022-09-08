/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.filesystem.sftp

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.*
import kotlinx.coroutines.flow.Flow

class SFTPFilesystem(private val serverModel: ServerModel) : Filesystem {

    override suspend fun defaultLocation(): FileModel {
        TODO("Not yet implemented")
    }

    override suspend fun provideFile(path: String): FileModel {
        TODO("Not yet implemented")
    }

    override suspend fun provideDirectory(parent: FileModel): FileTree {
        TODO("Not yet implemented")
    }

    override suspend fun createFile(fileModel: FileModel): FileModel {
        TODO("Not yet implemented")
    }

    override suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(fileModel: FileModel): FileModel {
        TODO("Not yet implemented")
    }

    override suspend fun copyFile(source: FileModel, dest: FileModel): FileModel {
        TODO("Not yet implemented")
    }

    override suspend fun propertiesOf(fileModel: FileModel): PropertiesModel {
        TODO("Not yet implemented")
    }

    override suspend fun isExists(fileModel: FileModel): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        TODO("Not yet implemented")
    }

    override suspend fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        TODO("Not yet implemented")
    }

    override suspend fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        TODO("Not yet implemented")
    }

    override suspend fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        TODO("Not yet implemented")
    }

    companion object {
        const val SFTP_SCHEME = "sftp://"
    }
}