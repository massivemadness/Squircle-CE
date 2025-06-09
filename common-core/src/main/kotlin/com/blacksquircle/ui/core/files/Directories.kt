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

package com.blacksquircle.ui.core.files

import android.content.Context
import java.io.File

object Directories {

    // region CACHE

    /** /data/data/com.blacksquircle.ui/cache/documents */
    fun documentsDir(context: Context): File {
        val directory = File(context.cacheDir, "documents")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    /** /data/data/com.blacksquircle.ui/cache/tmp */
    fun tmpDir(context: Context): File {
        val directory = File(context.cacheDir, "tmp")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    // endregion

    // region FILES

    /** /data/data/com.blacksquircle.ui/files/fonts */
    fun fontsDir(context: Context): File {
        val directory = File(context.filesDir, "fonts")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    /** /data/data/com.blacksquircle.ui/files/themes */
    fun themesDir(context: Context): File {
        val directory = File(context.filesDir, "themes")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    /** /data/data/com.blacksquircle.ui/files/keys */
    fun keysDir(context: Context): File {
        val directory = File(context.filesDir, "keys")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    /** /data/data/com.blacksquircle.ui/files/home */
    fun terminalDir(context: Context): File {
        val directory = File(context.filesDir, "home")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    /** /data/data/com.blacksquircle.ui/files/alpine */
    fun alpineDir(context: Context): File {
        val directory = File(context.filesDir, "alpine")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    // endregion
}