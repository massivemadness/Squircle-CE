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

package com.lightteam.modpeide.domain.model.explorer

import android.os.Parcelable
import com.lightteam.modpeide.domain.model.editor.DocumentModel
import com.lightteam.modpeide.domain.utils.endsWith
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
        val ARCHIVE = arrayOf(
            ".zip", ".rar", ".7z", ".tar"
        )
        val IMAGE = arrayOf(
            ".png", ".jpg", ".jpeg", ".gif", ".webp"
        )
        val AUDIO = arrayOf(
            ".mp3", ".ogg", ".wma", ".aac", ".wav", ".flac"
        )
        val VIDEO = arrayOf(
            ".mp4", ".avi", ".wmv", ".mkv"
        )
    }

    fun getType(): FileType {
        return when {
            name.endsWith(DocumentModel.OPENABLE) -> FileType.TEXT
            name.endsWith(ARCHIVE) -> FileType.ARCHIVE
            name.endsWith(IMAGE) -> FileType.IMAGE
            name.endsWith(AUDIO) -> FileType.AUDIO
            name.endsWith(VIDEO) -> FileType.VIDEO
            else -> FileType.DEFAULT
        }
    }
}