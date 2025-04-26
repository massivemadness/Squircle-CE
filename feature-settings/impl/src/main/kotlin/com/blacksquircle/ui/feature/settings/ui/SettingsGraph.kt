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

package com.blacksquircle.ui.feature.settings.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.blacksquircle.ui.feature.settings.api.navigation.AboutHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.AppHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.CodeStyleHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.EditorHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.FilesHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.GitHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.HeaderListScreen
import com.blacksquircle.ui.feature.settings.ui.about.AboutHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.application.AppHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.codestyle.CodeHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.editor.EditorHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.files.FilesHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.git.GitHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.header.HeaderListScreen

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    composable<HeaderListScreen> {
        HeaderListScreen(navController)
    }
    composable<AppHeaderScreen> {
        AppHeaderScreen(navController)
    }
    composable<EditorHeaderScreen> {
        EditorHeaderScreen(navController)
    }
    composable<CodeStyleHeaderScreen> {
        CodeHeaderScreen(navController)
    }
    composable<FilesHeaderScreen> {
        FilesHeaderScreen(navController)
    }
    composable<GitHeaderScreen> {
        GitHeaderScreen(navController)
    }
    composable<AboutHeaderScreen> {
        AboutHeaderScreen(navController)
    }
}