package com.blacksquircle.ui.feature.explorer.ui.navigation

import com.blacksquircle.ui.core.ui.navigation.Screen
import com.blacksquircle.ui.feature.explorer.data.utils.Operation

sealed class ExplorerScreen(route: String) : Screen<String>(route) {

    class RestrictedDialog(action: String, data: String) : ExplorerScreen(
        route = "blacksquircle://explorer/restricted?action=$action&data=$data"
    )
    class DeleteDialog(fileName: String, fileCount: Int) : ExplorerScreen(
        route = "blacksquircle://explorer/delete?fileName=$fileName&fileCount=$fileCount"
    )
    class RenameDialog(fileName: String) : ExplorerScreen(
        route = "blacksquircle://explorer/rename?fileName=$fileName"
    )
    class ProgressDialog(totalCount: Int, operation: Operation) : ExplorerScreen(
        route = "blacksquircle://explorer/progress?totalCount=$totalCount&operation=${operation.value}"
    )

    object CreateDialog : ExplorerScreen("blacksquircle://explorer/create")
    object CompressDialog : ExplorerScreen("blacksquircle://explorer/compress")
}