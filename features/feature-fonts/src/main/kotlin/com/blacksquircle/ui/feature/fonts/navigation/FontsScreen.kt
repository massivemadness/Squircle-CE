package com.blacksquircle.ui.feature.fonts.navigation

import com.blacksquircle.ui.core.navigation.Screen

sealed class FontsScreen(route: String) : Screen(route) {
    object Fonts : FontsScreen("blacksquircle://fonts")
    object ExternalFont : FontsScreen("blacksquircle://fonts/create")
}