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

package com.blacksquircle.ui.feature.editor.domain.model

import android.content.ContentResolver
import android.net.Uri
import java.io.File

internal data class DocumentModel(
    val uuid: String,
    val fileUri: String,
    val filesystemUuid: String,
    val language: String,
    val modified: Boolean,
    val position: Int,
    val scrollX: Int,
    val scrollY: Int,
    val selectionStart: Int,
    val selectionEnd: Int,
) {
    val scheme: String
        get() = fileUri.substringBefore("://")
    val path: String
        get() = fileUri.substringAfterLast("://").ifEmpty(File::separator)
    val name: String
        get() = when {
            scheme == ContentResolver.SCHEME_CONTENT -> {
                Uri.decode(path).substringAfterLast(File.separator)
            }
            else -> {
                path.substringAfterLast(File.separator)
            }
        }
    val extension: String
        get() = "." + name.substringAfterLast(".")
}