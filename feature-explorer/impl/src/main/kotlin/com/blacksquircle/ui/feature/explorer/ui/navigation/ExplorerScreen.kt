/*
 * Copyright 2023 Squircle CE contributors.
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

import com.blacksquircle.ui.core.extensions.encodeUrl
import com.blacksquircle.ui.core.extensions.toJsonEncoded
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.feature.explorer.data.utils.Operation
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.google.gson.Gson

sealed class ExplorerScreen(route: String) : Screen<String>(route) {

    class DeleteDialog(fileName: String, fileCount: Int) : ExplorerScreen(
        route = "blacksquircle://explorer/delete?fileName=${fileName.encodeUrl()}&fileCount=$fileCount",
    )
    class RenameDialog(fileName: String) : ExplorerScreen(
        route = "blacksquircle://explorer/rename?fileName=${fileName.encodeUrl()}",
    )
    class ProgressDialog(totalCount: Int, operation: Operation) : ExplorerScreen(
        route = "blacksquircle://explorer/progress?totalCount=$totalCount&operation=${operation.value}",
    )
    class PropertiesDialog(fileModel: FileModel) : ExplorerScreen(
        route = "blacksquircle://explorer/properties?data=${Gson().toJsonEncoded(fileModel)}",
    )
    class AuthDialog(authMethod: AuthMethod) : ExplorerScreen(
        route = "blacksquircle://explorer/authenticate?authMethod=${authMethod.value}"
    )

    data object CreateDialog : ExplorerScreen("blacksquircle://explorer/create")
    data object CompressDialog : ExplorerScreen("blacksquircle://explorer/compress")

    data object StorageDeniedForever : ExplorerScreen(
        route = "blacksquircle://explorer/storage_denied_forever"
    )
    data object NotificationDeniedForever : ExplorerScreen(
        route = "blacksquircle://explorer/notification_denied_forever"
    )
}