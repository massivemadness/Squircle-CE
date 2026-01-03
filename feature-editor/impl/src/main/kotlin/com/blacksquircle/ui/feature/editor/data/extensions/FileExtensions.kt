/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.data.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract.Document
import android.provider.MediaStore
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.saf.SAFFilesystem
import timber.log.Timber

internal fun Context.openUriAsDocument(fileUri: Uri): FileModel {
    try {
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        contentResolver.takePersistableUriPermission(fileUri, flags)
    } catch (e: SecurityException) {
        Timber.e(e, e.message)
    }
    return contentResolver.query(
        /* uri = */ fileUri,
        /* projection = */ arrayOf(Document.COLUMN_DISPLAY_NAME),
        /* selection = */ null,
        /* selectionArgs = */ null,
        /* sortOrder = */ null,
    )?.use { cursor ->
        val columnName = cursor.getColumnIndexOrThrow(Document.COLUMN_DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            FileModel(
                fileUri = fileUri.toString(),
                filesystemUuid = SAFFilesystem.SAF_UUID,
                name = cursor.getString(columnName),
            )
        } else {
            throw FileNotFoundException(fileUri.toString())
        }
    } ?: run {
        throw FileNotFoundException(fileUri.toString())
    }
}

internal fun Context.openUriAsContent(fileUri: Uri): FileModel {
    val filePath = contentResolver.query(
        /* uri = */ fileUri,
        /* projection = */ arrayOf(MediaStore.Files.FileColumns.DATA),
        /* selection = */ null,
        /* selectionArgs = */ null,
        /* sortOrder = */ null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex =
                cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
            if (columnIndex != -1) {
                return@use cursor.getString(columnIndex)
            }
        }
        return@use null
    }
    return if (filePath == null) {
        FileModel(
            fileUri = fileUri.toString(),
            filesystemUuid = SAFFilesystem.SAF_UUID,
        )
    } else {
        FileModel(
            fileUri = LocalFilesystem.LOCAL_SCHEME + filePath,
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
        )
    }
}

internal fun openUriAsFile(fileUri: Uri): FileModel {
    return FileModel(
        fileUri = fileUri.toString(),
        filesystemUuid = LocalFilesystem.LOCAL_UUID,
    )
}