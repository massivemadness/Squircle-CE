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

package com.blacksquircle.ui.data.utils

import com.blacksquircle.ui.editorkit.model.TextChange
import com.blacksquircle.ui.editorkit.utils.UndoStack
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

fun Int.toHexString(fallbackColor: String = "#000000"): String {
    return try {
        "#" + Integer.toHexString(this)
    } catch (e: Exception) {
        fallbackColor
    }
}

internal fun charsetFor(charsetName: String): Charset = try {
    charset(charsetName)
} catch (e: UnsupportedCharsetException) {
    Charsets.UTF_8
}

internal fun UndoStack.encodeStack(): String {
    val builder = StringBuilder()
    val delimiter = "\u0005"
    for (i in size - 1 downTo 0) {
        val textChange = get(i)
        builder.append(textChange.oldText)
        builder.append(delimiter)
        builder.append(textChange.newText)
        builder.append(delimiter)
        builder.append(textChange.start)
        builder.append(delimiter)
    }
    if (builder.isNotEmpty()) {
        builder.deleteCharAt(builder.length - 1)
    }
    return builder.toString()
}

internal fun String.decodeStack(): UndoStack {
    val result = UndoStack()
    if (isNotEmpty()) {
        val items = split("\u0005").toTypedArray()
        if (items[items.size - 1].endsWith("\n")) {
            val item = items[items.size - 1]
            items[items.size - 1] = item.substring(0, item.length - 1)
        }
        for (i in items.size - 3 downTo 0 step 3) {
            val change = TextChange(
                newText = items[i + 1],
                oldText = items[i],
                start = items[i + 2].toInt()
            )
            result.push(change)
        }
    }
    return result
}