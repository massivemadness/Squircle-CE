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

package com.blacksquircle.ui.feature.explorer

import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem

internal fun createFilesystem(): FilesystemModel {
    return defaultFilesystems().first()
}

internal fun defaultFilesystems(): List<FilesystemModel> {
    return listOf(
        FilesystemModel(
            uuid = LocalFilesystem.LOCAL_UUID,
            title = "Local Storage",
            defaultLocation = FileModel(
                fileUri = "file:///storage/emulated/0/",
                filesystemUuid = LocalFilesystem.LOCAL_UUID,
            ),
        ),
        FilesystemModel(
            uuid = RootFilesystem.ROOT_UUID,
            title = "Root Directory",
            defaultLocation = FileModel(
                fileUri = "sufile:///",
                filesystemUuid = RootFilesystem.ROOT_UUID,
            ),
        )
    )
}

internal fun createFile(fileName: String): FileModel {
    return FileModel(
        fileUri = "file:///storage/emulated/0/$fileName",
        filesystemUuid = LocalFilesystem.LOCAL_UUID,
        directory = false,
    )
}

internal fun createFolder(fileName: String): FileModel {
    return FileModel(
        fileUri = "file:///storage/emulated/0/$fileName",
        filesystemUuid = LocalFilesystem.LOCAL_UUID,
        directory = true,
    )
}