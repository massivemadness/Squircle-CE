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

package com.blacksquircle.ui.feature.explorer.domain.repository

import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.filesystem.base.model.FileModel

internal interface ExplorerRepository {

    suspend fun loadFilesystems(): List<FilesystemModel>
    suspend fun loadBreadcrumbs(filesystemModel: FilesystemModel): List<FileModel>
    suspend fun listFiles(parent: FileModel): List<FileModel>

    fun createFile(parent: FileModel, fileName: String, isFolder: Boolean): String
    fun renameFile(source: FileModel, fileName: String): String
    fun deleteFiles(source: List<FileModel>): String
    fun copyFiles(source: List<FileModel>, dest: FileModel): String
    fun cutFiles(source: List<FileModel>, dest: FileModel): String
    fun compressFiles(source: List<FileModel>, dest: FileModel, fileName: String): String
    fun extractFiles(source: FileModel, dest: FileModel): String
    fun cloneRepository(parent: FileModel, url: String): String
}