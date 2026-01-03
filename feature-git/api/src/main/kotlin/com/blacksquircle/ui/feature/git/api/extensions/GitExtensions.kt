/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.git.api.extensions

import com.blacksquircle.ui.filesystem.base.model.FileModel
import java.io.File

private const val GIT_FOLDER = ".git"

fun FileModel.findGitRepository(): String? {
    var current: File? = File(path)
    while (current != null) {
        val gitDir = File(current, GIT_FOLDER)
        if (gitDir.exists() && gitDir.isDirectory) {
            return current.absolutePath
        }
        current = current.parentFile
    }
    return null
}