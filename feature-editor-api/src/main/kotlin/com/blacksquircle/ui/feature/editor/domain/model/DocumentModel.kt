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

data class DocumentModel(
    val uuid: String,
    val fileUri: String,
    val filesystemUuid: String,
    val modified: Boolean,
    val position: Int,
    var scrollX: Int,
    var scrollY: Int,
    var selectionStart: Int,
    var selectionEnd: Int,
) {

    val scheme: String
        get() = fileUri.substringBeforeLast("://") + "://"
    val path: String
        get() = fileUri.substringAfterLast("://")
    val name: String
        get() = fileUri.substringAfterLast("/")
    val extension: String
        get() = fileUri.substringAfterLast(".", "")
    val mimeType: String
        get() = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(extension)
            ?: "*/*"
}