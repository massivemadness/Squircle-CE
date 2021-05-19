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

package com.blacksquircle.ui.filesystem.base

import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.FileTree
import com.blacksquircle.ui.filesystem.base.model.PropertiesModel
import kotlinx.coroutines.flow.Flow

interface Filesystem {
    suspend fun defaultLocation(): FileModel

    suspend fun provideFile(path: String): FileModel
    suspend fun provideDirectory(parent: FileModel): FileTree

    suspend fun createFile(fileModel: FileModel): FileModel
    suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel
    suspend fun deleteFile(fileModel: FileModel): FileModel
    suspend fun copyFile(source: FileModel, dest: FileModel): FileModel
    suspend fun propertiesOf(fileModel: FileModel): PropertiesModel
    suspend fun isExists(fileModel: FileModel): Boolean

    suspend fun compress(source: List<FileModel>, dest: FileModel): Flow<FileModel>
    suspend fun extractAll(source: FileModel, dest: FileModel): Flow<FileModel>

    suspend fun loadFile(fileModel: FileModel, fileParams: FileParams): String
    suspend fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams)
}