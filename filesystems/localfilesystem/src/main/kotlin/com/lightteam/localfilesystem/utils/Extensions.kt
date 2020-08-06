/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.localfilesystem.utils

import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

fun String.isValidFileName(): Boolean {
    return isNotBlank() && !contains("/") && !equals(".") && !equals("..")
}

fun File.size(): Long {
    if (isDirectory) {
        var length = 0L
        for (child in listFiles()!!) {
            length += child.size()
        }
        return length
    }
    return length()
}

fun Long.formatAsDate(pattern: String, locale: Locale): String {
    val dateFormat = SimpleDateFormat(pattern, locale)
    return dateFormat.format(this)
}

fun Long.formatAsDate(): String {
    val locale = Locale.getDefault()
    val languageTag = locale.toLanguageTag()
    // e.g. zh-CN, zh-Hans, zh-hans-CN
    if (languageTag.contains("zh")) {
        // Date format in Chinese
        return formatAsDate("yyyy/MM/dd EEE HH:mm", locale)
    }
    // toReadableDate ? dd MMM yy E HH:mm:ss
    return formatAsDate("dd/MM/yyyy EEE HH:mm", locale)
}

fun Long.formatAsSize(): String {
    if (this <= 0)
        return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return (DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble())) +
        " " + units[digitGroups])
}