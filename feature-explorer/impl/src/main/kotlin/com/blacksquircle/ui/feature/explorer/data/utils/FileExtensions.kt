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

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import java.io.File

private const val PRIMARY = "primary:"

internal fun Uri.guessFilePath(): String? {
    if (DocumentsContract.isTreeUri(this)) {
        val docId = DocumentsContract.getTreeDocumentId(this)
        if (docId.startsWith(PRIMARY)) {
            val basePath = Environment.getExternalStorageDirectory().absolutePath
            val relativePath = docId.removePrefix(PRIMARY)
            return basePath + File.separator + relativePath
        }
    }
    return null
}