package com.blacksquircle.ui.feature.themes.navigation

import com.blacksquircle.ui.core.navigation.Screen

sealed class ThemesScreen(route: String) : Screen(route) {
    object Themes : ThemesScreen("blacksquircle://themes")
    object Create : ThemesScreen("blacksquircle://themes/create")
    class Update(uuid: String?) : ThemesScreen("blacksquircle://themes/update?uuid=$uuid")
}