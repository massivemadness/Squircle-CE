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

package com.blacksquircle.ui.feature.explorer.data.utils

import android.content.Context
import android.os.Environment
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FilesystemType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem

internal fun Context.createLocalWorkspace(): WorkspaceModel {
    return WorkspaceModel(
        uuid = LocalFilesystem.LOCAL_UUID,
        name = getString(R.string.storage_local),
        filesystemType = FilesystemType.LOCAL,
        defaultLocation = FileModel(
            fileUri = LocalFilesystem.LOCAL_SCHEME +
                Environment.getExternalStorageDirectory().absolutePath,
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
            isDirectory = true,
        ),
    )
}

internal fun Context.createRootWorkspace(): WorkspaceModel {
    return WorkspaceModel(
        uuid = RootFilesystem.ROOT_UUID,
        name = getString(R.string.storage_root),
        filesystemType = FilesystemType.ROOT,
        defaultLocation = FileModel(
            fileUri = RootFilesystem.ROOT_SCHEME,
            filesystemUuid = RootFilesystem.ROOT_UUID,
            isDirectory = true,
        ),
    )
}