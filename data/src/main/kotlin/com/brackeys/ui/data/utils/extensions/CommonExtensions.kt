/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.data.utils.extensions

import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.filesystem.base.model.FileModel
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

fun safeCharset(charsetName: String): Charset = try {
    charset(charsetName)
} catch (e: UnsupportedCharsetException) {
    Charsets.UTF_8
}

fun Int.toHexString(fallbackColor: String = "#000000"): String {
    return try {
        "#" + Integer.toHexString(this)
    } catch (e: Exception) {
        fallbackColor
    }
}

fun Collection<FileModel>.containsFileModel(fileModel: FileModel): Boolean {
    forEach { indexedModel ->
        if (indexedModel.path == fileModel.path) {
            return true
        }
    }
    return false
}

fun Collection<DocumentModel>.containsDocumentModel(documentModel: DocumentModel): Boolean {
    forEach { indexedModel ->
        if (indexedModel.path == documentModel.path) {
            return true
        }
    }
    return false
}

fun Collection<DocumentModel>.indexBy(uuid: String): Int? {
    forEachIndexed { index, indexedModel ->
        if (indexedModel.uuid == uuid) {
            return index
        }
    }
    return null
}

fun Collection<DocumentModel>.indexBy(documentModel: DocumentModel): Int? {
    forEachIndexed { index, indexedModel ->
        if (indexedModel.path == documentModel.path) {
            return index
        }
    }
    return null
}

fun <T> MutableList<T>.replaceList(collection: Collection<T>) {
    clear()
    addAll(collection)
}