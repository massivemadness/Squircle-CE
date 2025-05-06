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

package com.blacksquircle.ui.feature.explorer.ui.explorer.model

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.filesystem.base.model.FileModel

@Immutable
internal data class FileNode(
    val file: FileModel,
    val depth: Int = 0,
    val key: NodeKey = NodeKey.File(file.fileUri),
    val displayName: String = file.name,
    val displayDepth: Int = depth,
    val isExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val errorState: ErrorState? = null,
) {
    val isDirectory: Boolean
        get() = file.isDirectory
    val isHidden: Boolean
        get() = displayName.startsWith('.')
    val isError: Boolean
        get() = errorState != null
    val isRoot: Boolean
        get() = depth == 0
}