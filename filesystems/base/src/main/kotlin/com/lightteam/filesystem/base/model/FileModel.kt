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

package com.lightteam.filesystem.base.model

import android.os.Parcelable
import com.lightteam.filesystem.base.utils.endsWith
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileModel(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    val isFolder: Boolean,
    val isHidden: Boolean
) : Parcelable {

    companion object {
        val TEXT = arrayOf(
            ".txt", ".js", ".json", ".java", ".kt", ".md", ".lua"
        )
        val ARCHIVE = arrayOf(
            ".zip", ".jar", ".rar", ".7z", ".tar", ".gz", ".tgz",
            ".zipx", ".gtar", "xtar", ".z", ".xz", ".bz", ".bz2",
            ".zst", ".lzh", ".lzma", ".arj"
        )
        val IMAGE = arrayOf(
            ".png", ".jpg", ".jpeg", ".gif", ".webp", ".bmp", ".ico"
        )
        val AUDIO = arrayOf(
            ".mp2", ".mp3", ".ogg", ".wma", ".aac", ".wav", ".flac",
            ".amr", ".m4a", ".m4b", ".m4p", ".mid", ".midi", ".mpga",
            ".m3u"
        )
        val VIDEO = arrayOf(
            ".3gp", ".mp4", ".avi", ".wmv", ".mkv", ".mpe", ".mpg",
            ".mpeg", ".asf", ".m4v", ".mov", ".rmvb", ".m4u", ".m3u8"
        )
        val DOCUMENT = arrayOf(
            ".rtf", ".doc", ".docx", ".ppt", ".pptx", ".pps", ".ppsx",
            ".xls", ".xlsx", ".csv", ".wps", ".pdf"
        )
        val EBOOK = arrayOf(
            ".epub", ".umb", ".chm", ".ceb", ".pdg", ".caj"
        )
        val APPLICATION = arrayOf(
            ".apk", ".aab"
        )
    }

    fun getType(): FileType {
        return when {
            name.endsWith(TEXT) -> FileType.TEXT
            name.endsWith(ARCHIVE) -> FileType.ARCHIVE
            name.endsWith(IMAGE) -> FileType.IMAGE
            name.endsWith(AUDIO) -> FileType.AUDIO
            name.endsWith(VIDEO) -> FileType.VIDEO
            name.endsWith(DOCUMENT) -> FileType.DOCUMENT
            name.endsWith(EBOOK) -> FileType.EBOOK
            name.endsWith(APPLICATION) -> FileType.APPLICATION
            else -> FileType.DEFAULT
        }
    }
}