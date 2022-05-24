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

package com.blacksquircle.ui.utils.extensions

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri

private const val SCHEME_CONTENT = "content"
private const val SCHEME_FILE = "file"

private const val AUTHORITY_EXTERNAL_STORAGE = "com.android.externalstorage.documents"
private const val AUTHORITY_DOWNLOADS = "com.android.providers.downloads.documents"
private const val AUTHORITY_MEDIA = "com.android.providers.media.documents"

private const val TYPE_PRIMARY = "primary"
private const val TYPE_NON = "non"
private const val TYPE_IMAGE = "image"
private const val TYPE_VIDEO = "video"
private const val TYPE_AUDIO = "audio"

private const val COLUMN_DATA = "_data"
private const val SELECTION_ID = "_id=?"
private const val URI_DOWNLOADS = "content://downloads/public_downloads"
private const val FOLDER_ANDROID = "Android"
private const val DELIMITER = ":"
private const val SEPARATOR = "/"

private val Uri.isExternalStorageDocument: Boolean
    get() = authority == AUTHORITY_EXTERNAL_STORAGE
private val Uri.isDownloadsDocument: Boolean
    get() = authority == AUTHORITY_DOWNLOADS
private val Uri.isMediaDocument: Boolean
    get() = authority == AUTHORITY_MEDIA

@Suppress("DEPRECATION")
fun Context.resolveFilePath(uri: Uri): String {
    when {
        DocumentsContract.isDocumentUri(this, uri) -> when {
            uri.isExternalStorageDocument -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(DELIMITER).toTypedArray()
                val type = split[0]
                return if (type.equals(TYPE_PRIMARY, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + SEPARATOR + split[1]
                } else {
                    var filePath = TYPE_NON
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
            uri.isDownloadsDocument -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(URI_DOWNLOADS.toUri(), docId.toLong())
                return resolveDataColumn(contentUri, null, null)
            }
            uri.isMediaDocument -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(DELIMITER).toTypedArray()
                val type = split[0]
                if (type == TYPE_IMAGE || type == TYPE_VIDEO || type == TYPE_AUDIO) {
                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val selection = SELECTION_ID
                    val selectionArgs = arrayOf(split[1])
                    return resolveDataColumn(contentUri, selection, selectionArgs)
                }
            }
        }
        uri.scheme.equals(SCHEME_FILE, ignoreCase = true) -> return uri.path.orEmpty()
        uri.scheme.equals(SCHEME_CONTENT, ignoreCase = true) ->
            return resolveDataColumn(uri, null, null)
    }
    return ""
}

private fun Context.resolveDataColumn(
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?,
): String {
    return contentResolver.query(
        uri, arrayOf(COLUMN_DATA), selection, selectionArgs, null,
    ).use { cursor ->
        cursor ?: return ""
        cursor.moveToFirst()
        return@use cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_DATA)).orEmpty()
    }
}