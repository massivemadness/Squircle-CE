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

package com.blacksquircle.ui.core.extensions

import android.content.ContentResolver
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.database.getStringOrNull

fun Context.showToast(
    @StringRes textRes: Int = -1,
    text: CharSequence = "",
    duration: Int = Toast.LENGTH_SHORT,
) {
    if (textRes != -1) {
        Toast.makeText(this, textRes, duration).show()
    } else {
        Toast.makeText(this, text, duration).show()
    }
}

private const val ASSET_PATH = "file:///android_asset/"

fun Context.createTypefaceFromPath(path: String): Typeface {
    return if (path.startsWith(ASSET_PATH)) {
        try {
            Typeface.createFromAsset(assets, path.substring(ASSET_PATH.length))
        } catch (e: Exception) {
            Typeface.MONOSPACE
        }
    } else {
        try {
            Typeface.createFromFile(path)
        } catch (e: Exception) {
            Typeface.MONOSPACE
        }
    }
}

private const val AUTHORITY_EXTERNAL_STORAGE = "com.android.externalstorage.documents"
private const val AUTHORITY_DOWNLOADS = "com.android.providers.downloads.documents"

private const val TYPE_PRIMARY = "primary"
private const val TYPE_NON = "non"
private const val FOLDER_ANDROID = "Android"
private const val DELIMITER = ":"
private const val SEPARATOR = "/"

fun Context.extractFilePath(fileUri: Uri): String? = when {
    fileUri.authority == AUTHORITY_EXTERNAL_STORAGE -> {
        val documentId = DocumentsContract.getDocumentId(fileUri)
        val split = documentId.split(DELIMITER)
        when (val type = split[0]) {
            TYPE_PRIMARY -> Environment.getExternalStorageDirectory().path + SEPARATOR + split[1]
            else -> {
                var filePath = TYPE_NON
                @Suppress("DEPRECATION")
                for (file in externalMediaDirs) {
                    filePath = file.absolutePath
                    if (filePath.contains(type)) {
                        val endIndex = filePath.indexOf(FOLDER_ANDROID)
                        filePath = filePath.substring(0, endIndex) + split[1]
                    }
                }
                filePath
            }
        }
    }

    fileUri.authority == AUTHORITY_DOWNLOADS -> {
        contentResolver.query(
            /* uri = */ fileUri,
            /* projection = */ arrayOf(MediaStore.Downloads.DISPLAY_NAME),
            /* selection = */ null,
            /* selectionArgs = */ null,
            /* sortOrder = */ null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME)
                val value = cursor.getStringOrNull(index) ?: return null
                return@use Environment.getExternalStorageDirectory().path + "/Download/" + value
            }
            return null
        }
    }

    fileUri.scheme == ContentResolver.SCHEME_FILE -> {
        fileUri.path.orEmpty()
    }

    fileUri.scheme == ContentResolver.SCHEME_CONTENT -> {
        contentResolver.query(
            /* uri = */ fileUri,
            /* projection = */ arrayOf(MediaStore.MediaColumns.DATA),
            /* selection = */ null,
            /* selectionArgs = */ null,
            /* sortOrder = */ null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                val value = cursor.getStringOrNull(index) ?: return null
                return@use value
            }
            return null
        }
    }

    else -> null
}