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

package com.lightteam.modpeide.data.utils.commons

import com.lightteam.modpeide.domain.model.FileModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Properties {

    data class Result(
        val name: String,
        val path: String,
        val lastModified: String,
        val size: String,
        val words: String,
        val chars: String,
        val lines: String,

        val readable: Boolean,
        val writable: Boolean,
        val executable: Boolean
    )

    fun analyze(fileModel: FileModel): Result {
        return Result(
            fileModel.name,
            fileModel.path,
            getReadableDate(fileModel.lastModified),
            getReadableSize(fileModel.size),
            "",
            "",
            "",
            true,
            true,
            false
        )
    }

    private fun getReadableDate(date: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy EEE HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getReadableSize(size: Long): String {
        if (size <= 0)
            return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return (DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble()))
                + " " + units[digitGroups])
    }
}