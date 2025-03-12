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

package com.blacksquircle.ui.core.navigation

abstract class Screen(val route: Any) {
    object Explorer : Screen("blacksquircle://explorer")
    object Settings : Screen("blacksquircle://settings")
    object Fonts : Screen("blacksquircle://fonts")
    object Themes : Screen("blacksquircle://themes")
    object Server : Screen("blacksquircle://settings/cloud/create") {
        const val KEY_SAVE = "KEY_SAVE"
        const val KEY_DELETE = "KEY_DELETE"
    }
}