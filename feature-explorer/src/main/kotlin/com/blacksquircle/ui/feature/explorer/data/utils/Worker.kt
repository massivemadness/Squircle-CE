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

package com.blacksquircle.ui.feature.explorer.data.utils

import androidx.work.Data
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.Permission
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val KEY_LIST = "list"
private const val KEY_FILE_URI = "fileUri"
private const val KEY_FILESYSTEM_UUID = "filesystemUuid"
private const val KEY_SIZE = "size"
private const val KEY_LAST_MODIFIED = "lastModified"
private const val KEY_DIRECTORY = "directory"
private const val KEY_PERMISSION = "permission"

internal fun FileModel.toData(): Data {
    return Data.Builder()
        .putString(KEY_FILE_URI, fileUri)
        .putString(KEY_FILESYSTEM_UUID, filesystemUuid)
        .putLong(KEY_SIZE, size)
        .putLong(KEY_LAST_MODIFIED, lastModified)
        .putBoolean(KEY_DIRECTORY, directory)
        .putInt(KEY_PERMISSION, permission)
        .build()
}

internal fun Data.toFileModel(): FileModel {
    return FileModel(
        fileUri = getString(KEY_FILE_URI).orEmpty(),
        filesystemUuid = getString(KEY_FILESYSTEM_UUID).orEmpty(),
        size = getLong(KEY_SIZE, 0L),
        lastModified = getLong(KEY_LAST_MODIFIED, 0L),
        directory = getBoolean(KEY_DIRECTORY, false),
        permission = getInt(KEY_PERMISSION, Permission.EMPTY),
    )
}

internal fun List<FileModel>.toData(): Data {
    return Data.Builder()
        .putString(KEY_LIST, Gson().toJson(this))
        .build()
}

internal fun Data.toFileList(): List<FileModel> {
    return Gson().fromJson(getString(KEY_LIST), object : TypeToken<List<FileModel>>() {}.type)
}