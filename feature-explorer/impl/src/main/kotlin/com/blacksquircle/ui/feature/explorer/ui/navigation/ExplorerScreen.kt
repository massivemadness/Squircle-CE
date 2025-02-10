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

package com.blacksquircle.ui.feature.explorer.ui.navigation

import androidx.core.os.bundleOf
import com.blacksquircle.ui.core.extensions.NavAction
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.mapper.FileMapper
import com.blacksquircle.ui.feature.explorer.ui.dialog.AuthDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.DeleteDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.RenameDialog
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerFragmentDirections
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileModel

internal sealed class ExplorerScreen(route: Any) : Screen(route) {

    data class DeleteDialogScreen(val fileName: String, val fileCount: Int) : ExplorerScreen(
        route = NavAction(
            id = R.id.deleteDialog,
            args = bundleOf(
                DeleteDialog.ARG_FILE_NAME to fileName,
                DeleteDialog.ARG_FILE_COUNT to fileCount,
            )
        )
    )

    data class RenameDialogScreen(val fileName: String) : ExplorerScreen(
        route = NavAction(
            id = R.id.renameDialog,
            args = bundleOf(RenameDialog.ARG_FILE_NAME to fileName)
        )
    )

    data class TaskDialogScreen(val taskId: String) : ExplorerScreen(
        route = "blacksquircle://explorer/tasks/$taskId"
    )

    data class PropertiesDialogScreen(val fileModel: FileModel) : ExplorerScreen(
        route = ExplorerFragmentDirections.toPropertiesDialog(FileMapper.toBundle(fileModel))
    )

    data class AuthDialogScreen(val authMethod: AuthMethod) : ExplorerScreen(
        route = NavAction(
            id = R.id.authDialog,
            args = bundleOf(AuthDialog.ARG_AUTH_METHOD to authMethod.value)
        )
    )

    data object CreateDialogScreen : ExplorerScreen(route = NavAction(R.id.createDialog))
    data object CompressDialogScreen : ExplorerScreen(route = NavAction(R.id.compressDialog))
    data object StorageDeniedScreen : ExplorerScreen(route = NavAction(R.id.storageDeniedDialog))
    data object NotificationDeniedScreen : ExplorerScreen(route = NavAction(R.id.notificationDeniedDialog))
}