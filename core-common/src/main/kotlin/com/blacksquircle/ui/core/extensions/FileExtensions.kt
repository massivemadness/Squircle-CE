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

package com.blacksquircle.ui.core.extensions

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import java.io.File

private const val PRIMARY = "primary:"

fun Uri.extractFilePath(): String? {
    if (!DocumentsContract.isTreeUri(this)) {
        return null
    }
    val documentId = DocumentsContract.getTreeDocumentId(this)
    if (documentId.startsWith(PRIMARY)) {
        val basePath = Environment.getExternalStorageDirectory().absolutePath
        val relativePath = documentId.removePrefix(PRIMARY)
        return basePath + File.separator + relativePath
    }
    return null
}