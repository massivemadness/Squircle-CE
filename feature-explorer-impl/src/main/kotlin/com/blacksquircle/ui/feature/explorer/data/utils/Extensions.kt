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

import android.content.*
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.filesystem.base.model.FileModel
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

fun Context.openFileWith(fileModel: FileModel) {
    try {
        val file = File(fileModel.path)
        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }

        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            file,
        )

        val mime = contentResolver?.getType(uri)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, mime)
        }
        startActivity(intent)
    } catch (e: Exception) {
        Timber.d(e, e.message)
        showToast(R.string.message_cannot_be_opened)
    }
}

fun Long.toReadableDate(pattern: String): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(this)
}

fun Long.toReadableSize(): String {
    if (this <= 0) {
        return "0"
    }
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#")
        .format(this / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

fun String.clipText(context: Context?) = clip(context, ClipData.newPlainText("Text", this))

private fun clip(context: Context?, data: ClipData) {
    context?.getSystemService<ClipboardManager>()?.setPrimaryClip(data)
}