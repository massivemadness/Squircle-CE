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

package com.blacksquircle.ui.feature.explorer.data.mapper

import android.os.Bundle
import androidx.core.os.bundleOf
import com.blacksquircle.ui.filesystem.base.model.FileModel

internal object FileMapper {

    private const val ARG_FILE_URI = "fileUri"
    private const val ARG_FILESYSTEM_UUID = "filesystemUuid"
    private const val ARG_SIZE = "size"
    private const val ARG_LAST_MODIFIED = "lastModified"
    private const val ARG_DIRECTORY = "directory"
    private const val ARG_PERMISSION = "permission"

    fun toBundle(fileModel: FileModel): Bundle {
        return bundleOf(
            ARG_FILE_URI to fileModel.fileUri,
            ARG_FILESYSTEM_UUID to fileModel.filesystemUuid,
            ARG_SIZE to fileModel.size,
            ARG_LAST_MODIFIED to fileModel.lastModified,
            ARG_DIRECTORY to fileModel.directory,
            ARG_PERMISSION to fileModel.permission,
        )
    }

    fun fromBundle(bundle: Bundle): FileModel {
        return FileModel(
            fileUri = bundle.getString(ARG_FILE_URI).orEmpty(),
            filesystemUuid = bundle.getString(ARG_FILESYSTEM_UUID).orEmpty(),
            size = bundle.getLong(ARG_SIZE),
            lastModified = bundle.getLong(ARG_LAST_MODIFIED),
            directory = bundle.getBoolean(ARG_DIRECTORY),
            permission = bundle.getInt(ARG_PERMISSION),
        )
    }
}