package com.blacksquircle.ui.feature.settings.ui.navigation

import com.blacksquircle.ui.core.ui.navigation.Screen

sealed class SettingsScreen(route: String) : Screen<String>(route) {

    object Application : SettingsScreen("blacksquircle://settings/application")
    object Editor : SettingsScreen("blacksquircle://settings/editor")
    object CodeStyle : SettingsScreen("blacksquircle://settings/codestyle")
    object Files : SettingsScreen("blacksquircle://settings/files")
    object About : SettingsScreen("blacksquircle://settings/about")

    object Preset : SettingsScreen("blacksquircle://settings/editor/preset")
    object ChangeLog : SettingsScreen("blacksquircle://settings/about/changelog")
}