package com.blacksquircle.ui.feature.explorer.ui.navigation

import com.blacksquircle.ui.core.ui.navigation.Screen

sealed class ExplorerScreen(route: String) : Screen<String>(route) {

    class RestrictedDialog(action: String, data: String) : ExplorerScreen(
        route = "blacksquircle://explorer/restricted?action=$action&data=$data"
    )
}