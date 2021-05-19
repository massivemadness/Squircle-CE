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

package com.blacksquircle.ui.filesystem.local.converter

import com.blacksquircle.ui.filesystem.base.model.FileModel
import java.io.File

object FileConverter {

    fun toModel(file: File): FileModel {
        return FileModel(
            path = file.path,
            size = file.length(),
            lastModified = file.lastModified(),
            isFolder = file.isDirectory,
            isHidden = file.isHidden
        )
    }

    fun toFile(fileModel: FileModel): File {
        return File(fileModel.path)
    }
}