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

package com.blacksquircle.ui.feature.explorer.api.navigation

import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import kotlinx.serialization.Serializable

@Serializable
data object ExplorerScreen

@Serializable
data class AuthDialog(val authMethod: AuthMethod)

@Serializable
data object CreateDialog

@Serializable
data object CloneRepoDialog

@Serializable
data class RenameDialog(val fileName: String)

@Serializable
data class DeleteDialog(val fileName: String, val fileCount: Int)

@Serializable
data object CompressDialog

@Serializable
data class TaskDialog(val taskId: String)

@Serializable
data class PropertiesDialog(
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val lastModified: Long,
    val permission: Int,
)

@Serializable
data object StorageDeniedDialog

@Serializable
data object NotificationDeniedDialog

@Serializable
data object AddWorkspaceDialog

@Serializable
data class DeleteWorkspaceDialog(val uuid: String, val name: String)

@Serializable
data object LocalWorkspaceDialog