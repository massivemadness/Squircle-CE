/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.filesystem.local.utils

import com.blacksquircle.ui.filesystem.base.model.FileType
import java.io.File

internal fun File.size(): Long {
    if (isDirectory) {
        var length = 0L
        for (child in listFiles()!!) {
            length += child.size()
        }
        return length
    }
    return length()
}

internal fun File.lineCount(fileType: FileType): Int? {
    if (isFile && fileType == FileType.TEXT) {
        var lines = 0
        forEachLine {
            lines++
        }
        return lines
    }
    return null
}

internal fun File.wordCount(fileType: FileType): Int? {
    if (isFile && fileType == FileType.TEXT) {
        var words = 0
        forEachLine {
            words += it.split(' ').size
        }
        return words
    }
    return null
}

internal fun File.charCount(fileType: FileType): Int? {
    if (isFile && fileType == FileType.TEXT) {
        return length().toInt()
    }
    return null
}