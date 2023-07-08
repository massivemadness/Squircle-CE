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

package com.blacksquircle.ui.feature.editor.domain.model

import android.webkit.MimeTypeMap
import com.blacksquircle.ui.language.base.Language

data class DocumentModel(
    val uuid: String,
    val fileUri: String,
    val filesystemUuid: String,
    val language: Language,
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
        get() = fileUri.substringAfterLast("://").ifEmpty { "/" }
    val name: String
        get() = fileUri.substringAfterLast("/").ifEmpty { "/" }
    val extension: String
        get() = fileUri.substringAfterLast(".", "")
    val mimeType: String
        get() = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(extension)
            ?: "text/*"
}