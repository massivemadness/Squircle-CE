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

import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FilesystemType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem

internal fun defaultWorkspaces(): List<WorkspaceModel> {
    return listOf(
        WorkspaceModel(
            uuid = LocalFilesystem.LOCAL_UUID,
            name = "Local",
            filesystemType = FilesystemType.LOCAL,
            defaultLocation = FileModel(
                fileUri = "file:///storage/emulated/0/",
                filesystemUuid = LocalFilesystem.LOCAL_UUID,
                isDirectory = true,
            ),
        ),
        WorkspaceModel(
            uuid = RootFilesystem.ROOT_UUID,
            name = "Root",
            filesystemType = FilesystemType.ROOT,
            defaultLocation = FileModel(
                fileUri = "sufile:///",
                filesystemUuid = RootFilesystem.ROOT_UUID,
                isDirectory = true,
            ),
        )
    )
}

internal fun createWorkspace(
    uuid: String = LocalFilesystem.LOCAL_UUID,
): WorkspaceModel {
    return WorkspaceModel(
        uuid = uuid,
        name = "Filesystem",
        filesystemType = FilesystemType.LOCAL,
        defaultLocation = FileModel(
            fileUri = "file:///storage/emulated/0/",
            filesystemUuid = uuid,
        ),
    )
}

internal fun createFile(
    name: String,
    size: Long = 100L,
    lastModified: Long = 100L
): FileModel {
    return FileModel(
        fileUri = "file:///storage/emulated/0/$name",
        filesystemUuid = LocalFilesystem.LOCAL_UUID,
        size = size,
        lastModified = lastModified,
        isDirectory = false,
    )
}

internal fun createFolder(
    name: String,
    size: Long = 100L,
    lastModified: Long = 100L
): FileModel {
    return FileModel(
        fileUri = "file:///storage/emulated/0/$name",
        filesystemUuid = LocalFilesystem.LOCAL_UUID,
        size = size,
        lastModified = lastModified,
        isDirectory = true,
    )
}

internal fun createNode(
    file: FileModel,
    depth: Int = 0,
    displayName: String = file.name,
    displayDepth: Int = depth,
    isExpanded: Boolean = false,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
): FileNode {
    return FileNode(
        file = file,
        depth = depth,
        displayName = displayName,
        displayDepth = displayDepth,
        isExpanded = isExpanded,
        isLoading = isLoading,
        errorState = errorState,
    )
}

internal fun createFileNode(
    name: String,
    size: Long = 100L,
    lastModified: Long = 100L,
    depth: Int = 0,
    isExpanded: Boolean = false,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
): FileNode {
    return FileNode(
        file = createFile(
            name = name,
            size = size,
            lastModified = lastModified
        ),
        depth = depth,
        isExpanded = isExpanded,
        isLoading = isLoading,
        errorState = errorState,
    )
}

internal fun createFolderNode(
    name: String,
    size: Long = 100L,
    lastModified: Long = 100L,
    depth: Int = 0,
    isExpanded: Boolean = false,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
): FileNode {
    return FileNode(
        file = createFolder(
            name = name,
            size = size,
            lastModified = lastModified
        ),
        depth = depth,
        isExpanded = isExpanded,
        isLoading = isLoading,
        errorState = errorState,
    )
}