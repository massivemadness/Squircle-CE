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

package com.blacksquircle.ui.filesystem.mock

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.FileTree
import com.blacksquircle.ui.filesystem.base.model.PropertiesModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MockFilesystem : Filesystem {

    override suspend fun defaultLocation(): FileModel {
        return suspendCoroutine { cont ->
            val fileModel = FileModel("file:///storage/emulated/0", MOCK_UUID)
            cont.resume(fileModel)
        }
    }

    override suspend fun provideFile(path: String): FileModel {
        return suspendCoroutine { cont ->
            val fileModel = FileModel(path, MOCK_UUID)
            cont.resume(fileModel)
        }
    }

    override suspend fun provideDirectory(parent: FileModel): FileTree {
        return suspendCoroutine { cont ->
            val fileTree = FileTree(parent, listOf(
                FileModel("file:///storage/emulated/0/folder_1", MOCK_UUID, isFolder = true),
                FileModel("file:///storage/emulated/0/folder_2", MOCK_UUID, isFolder = true),
                FileModel("file:///storage/emulated/0/folder_3", MOCK_UUID, isFolder = true),
                FileModel("file:///storage/emulated/0/folder_4", MOCK_UUID, isFolder = true),
                FileModel("file:///storage/emulated/0/file_1.txt", MOCK_UUID, isFolder = false),
                FileModel("file:///storage/emulated/0/file_2.txt", MOCK_UUID, isFolder = false),
                FileModel("file:///storage/emulated/0/file_3.txt", MOCK_UUID, isFolder = false),
                FileModel("file:///storage/emulated/0/file_4.txt", MOCK_UUID, isFolder = false),
            ))
            cont.resume(fileTree)
        }
    }

    override suspend fun createFile(fileModel: FileModel): FileModel {
        return suspendCoroutine { cont ->
            cont.resume(fileModel)
        }
    }

    override suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel {
        return suspendCoroutine { cont ->
            cont.resume(fileModel)
        }
    }

    override suspend fun deleteFile(fileModel: FileModel): FileModel {
        return suspendCoroutine { cont ->
            cont.resume(fileModel)
        }
    }

    override suspend fun copyFile(source: FileModel, dest: FileModel): FileModel {
        return suspendCoroutine { cont ->
            cont.resume(source)
        }
    }

    override suspend fun propertiesOf(fileModel: FileModel): PropertiesModel {
        return suspendCoroutine { cont ->
            val result = PropertiesModel(
                path = fileModel.path,
                lastModified = fileModel.lastModified,
                size = fileModel.size,
                lines = 12345,
                words = 12345,
                chars = 12345,
                readable = true,
                writable = true,
                executable = false
            )
            cont.resume(result)
        }
    }

    override suspend fun isExists(fileModel: FileModel): Boolean {
        return suspendCoroutine { cont ->
            cont.resume(true)
        }
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            for (fileModel in source) {
                send(fileModel)
            }
        }
    }

    override suspend fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            send(source)
        }
    }

    override suspend fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        return suspendCoroutine { cont ->
            cont.resume("Mock Data")
        }
    }

    override suspend fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        return suspendCoroutine { cont ->
            cont.resume(Unit)
        }
    }

    companion object {
        private const val MOCK_UUID = "mock"
    }
}