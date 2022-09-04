package com.blacksquircle.ui.feature.explorer.data.utils

import androidx.work.Data
import com.blacksquircle.ui.filesystem.base.model.FileModel

private const val KEY_PATH = "path"
private const val KEY_SIZE = "size"
private const val KEY_LAST_MODIFIED = "lastModified"
private const val KEY_IS_FOLDER = "isFolder"
private const val KEY_IS_HIDDEN = "isHidden"

internal fun FileModel.toData(): Data {
    return Data.Builder()
        .putString(KEY_PATH, path)
        .putLong(KEY_SIZE, size)
        .putLong(KEY_LAST_MODIFIED, lastModified)
        .putBoolean(KEY_IS_FOLDER, isFolder)
        .putBoolean(KEY_IS_HIDDEN, isHidden)
        .build()
}

internal fun Data.toFileModel(): FileModel {
    return FileModel(
        path = getString(KEY_PATH).orEmpty(),
        size = getLong(KEY_SIZE, 0L),
        lastModified = getLong(KEY_LAST_MODIFIED, 0L),
        isFolder = getBoolean(KEY_IS_FOLDER, false),
        isHidden = getBoolean(KEY_IS_HIDDEN, false),
    )
}