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

package com.blacksquircle.ui.feature.editor.ui.navigation

import com.blacksquircle.ui.core.extensions.encodeUrl
import com.blacksquircle.ui.core.navigation.Screen

sealed class EditorScreen(route: String) : Screen<String>(route) {

    class ForceSyntaxDialog(languageName: String) : EditorScreen(
        route = "blacksquircle://editor/syntax?languageName=${languageName.encodeUrl()}",
    )
    class CloseModifiedDialog(position: Int, fileName: String) : EditorScreen(
        route = "blacksquircle://editor/close?position=$position&fileName=${fileName.encodeUrl()}",
    )

    data object GotoLine : EditorScreen("blacksquircle://editor/goto")
    data object InsertColor : EditorScreen("blacksquircle://editor/insertcolor")
}