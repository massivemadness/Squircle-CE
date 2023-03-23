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

package com.blacksquircle.ui.core.extensions

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import com.google.android.material.color.MaterialColors
import java.io.FileNotFoundException

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

fun Context.getColour(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.getColorAttr(@AttrRes attrRes: Int): Int {
    return MaterialColors.getColor(this, attrRes, "The attribute is not set in the current theme")
}

private const val ASSET_PATH = "file:///android_asset/"

fun Context.createTypefaceFromPath(path: String): Typeface {
    return if (path.startsWith(ASSET_PATH)) {
        Typeface.createFromAsset(assets, path.substring(ASSET_PATH.length))
    } else {
        try {
            Typeface.createFromFile(path)
        } catch (e: Exception) {
            Typeface.MONOSPACE
        }
    }
}

private const val SCHEME_CONTENT = "content"
private const val SCHEME_FILE = "file"

private const val AUTHORITY_EXTERNAL_STORAGE = "com.android.externalstorage.documents"
private const val AUTHORITY_DOWNLOADS = "com.android.providers.downloads.documents"

private const val TYPE_PRIMARY = "primary"
private const val TYPE_NON = "non"

private const val COLUMN_DATA = "_data"
private const val FOLDER_ANDROID = "Android"
private const val DELIMITER = ":"
private const val SEPARATOR = "/"

fun Context.extractFilePath(fileUri: Uri): String = when {
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
            fileUri, arrayOf(MediaStore.Downloads.DISPLAY_NAME), null, null, null,
        ).use { cursor ->
            cursor ?: throw FileNotFoundException(fileUri.toString())
            cursor.moveToFirst()
            val fileIndex = cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME)
            val fileName = cursor.getStringOrNull(fileIndex)
            return@use Environment.getExternalStorageDirectory().path + "/Download/" + fileName
        }
    }
    fileUri.scheme == SCHEME_FILE -> fileUri.path.orEmpty()
    fileUri.scheme == SCHEME_CONTENT -> contentResolver.query(
        fileUri, arrayOf(COLUMN_DATA), null, null, null,
    ).use { cursor ->
        cursor ?: throw FileNotFoundException(fileUri.toString())
        cursor.moveToFirst()
        val dataIndex = cursor.getColumnIndex(COLUMN_DATA)
        val dataValue = cursor.getStringOrNull(dataIndex)
        return@use dataValue.orEmpty()
    }
    else -> throw FileNotFoundException(fileUri.toString())
}