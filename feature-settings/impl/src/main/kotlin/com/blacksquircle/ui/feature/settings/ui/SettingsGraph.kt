/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.settings.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.blacksquircle.ui.feature.settings.api.navigation.AboutHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.ApplicationHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.CodeStyleHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.EditorHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.FilesHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.GitHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.HeaderListRoute
import com.blacksquircle.ui.feature.settings.api.navigation.TerminalHeaderRoute
import com.blacksquircle.ui.feature.settings.ui.about.AboutHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.application.AppHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.codestyle.CodeHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.editor.EditorHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.files.FilesHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.git.GitHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.header.HeaderListScreen
import com.blacksquircle.ui.feature.settings.ui.terminal.TerminalHeaderScreen

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    composable<HeaderListRoute> {
        HeaderListScreen(navController)
    }
    composable<ApplicationHeaderRoute> {
        AppHeaderScreen(navController)
    }
    composable<EditorHeaderRoute> {
        EditorHeaderScreen(navController)
    }
    composable<CodeStyleHeaderRoute> {
        CodeHeaderScreen(navController)
    }
    composable<FilesHeaderRoute> {
        FilesHeaderScreen(navController)
    }
    composable<TerminalHeaderRoute> {
        TerminalHeaderScreen(navController)
    }
    composable<GitHeaderRoute> {
        GitHeaderScreen(navController)
    }
    composable<AboutHeaderRoute> {
        AboutHeaderScreen(navController)
    }
}