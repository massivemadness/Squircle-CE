package com.blacksquircle.ui.application.navigation

import com.blacksquircle.ui.core.ui.navigation.Screen

sealed class AppScreen(route: String) : Screen<String>(route) {

    object ConfirmExit : AppScreen("blacksquircle://exit")
}