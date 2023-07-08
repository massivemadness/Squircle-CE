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

package com.blacksquircle.ui.feature.settings.ui.navigation

import com.blacksquircle.ui.core.navigation.Screen

sealed class SettingsScreen(route: String) : Screen<String>(route) {

    object Application : SettingsScreen("blacksquircle://settings/application")
    object Editor : SettingsScreen("blacksquircle://settings/editor")
    object CodeStyle : SettingsScreen("blacksquircle://settings/codestyle")
    object Files : SettingsScreen("blacksquircle://settings/files")
    object Keybindings : SettingsScreen("blacksquircle://settings/keybindings")
    object Cloud : SettingsScreen("blacksquircle://settings/cloud")
    object About : SettingsScreen("blacksquircle://settings/about")
    object ChangeLog : SettingsScreen("blacksquircle://settings/about/changelog")
}