/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.core.storage

import android.content.Context
import java.io.File

object Directories {

    /** /data/data/com.blacksquircle.ui/files */
    fun filesDir(context: Context): File {
        val directory = File(context.dataDir, "files")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    /** /data/data/com.blacksquircle.ui/fonts */
    fun fontsDir(context: Context): File {
        val directory = File(context.dataDir, "fonts")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    /** /data/data/com.blacksquircle.ui/ftp */
    fun ftpDir(context: Context): File {
        val directory = File(context.dataDir, "ftp")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }
}