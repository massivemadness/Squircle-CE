/*
 * Copyright 2022 Squircle IDE contributors.
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

package com.blacksquircle.ui.core.data.factory

import android.os.Environment
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem

class FilesystemFactory(private val database: AppDatabase) {

    companion object {
        private val cache = HashMap<String, Filesystem>()
    }

    fun create(uuid: String?): Filesystem {
        val filesystemUuid = uuid ?: LocalFilesystem.LOCAL_UUID
        return cache[filesystemUuid] ?: when (filesystemUuid) {
            LocalFilesystem.LOCAL_UUID -> LocalFilesystem(Environment.getExternalStorageDirectory())
            else -> throw IllegalArgumentException("Can't find filesystem")
        }.also {
            cache[filesystemUuid] = it
        }
    }
}