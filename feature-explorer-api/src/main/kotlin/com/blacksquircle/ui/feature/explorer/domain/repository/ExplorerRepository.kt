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

package com.blacksquircle.ui.feature.explorer.domain.repository

import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileTree

interface ExplorerRepository {

    suspend fun loadFilesystems(): List<FilesystemModel>
    suspend fun selectFilesystem(filesystemUuid: String)

    suspend fun listFiles(parent: FileModel?): FileTree

    suspend fun createFile(fileModel: FileModel)
    suspend fun renameFile(source: FileModel, dest: FileModel)
    suspend fun deleteFiles(source: List<FileModel>)

    suspend fun copyFiles(source: List<FileModel>, dest: FileModel)
    suspend fun cutFiles(source: List<FileModel>, dest: FileModel)

    suspend fun compressFiles(source: List<FileModel>, dest: FileModel)
    suspend fun extractFiles(source: FileModel, dest: FileModel)
}