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

package com.blacksquircle.ui.feature.editor.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.editor.api.navigation.CloseFileDialog
import com.blacksquircle.ui.feature.editor.api.navigation.ConfirmExitDialog
import com.blacksquircle.ui.feature.editor.api.navigation.EditorScreen
import com.blacksquircle.ui.feature.editor.api.navigation.ForceSyntaxDialog
import com.blacksquircle.ui.feature.editor.api.navigation.GoToLineDialog
import com.blacksquircle.ui.feature.editor.api.navigation.InsertColorDialog
import com.blacksquircle.ui.feature.editor.ui.dialog.CloseFileScreen
import com.blacksquircle.ui.feature.editor.ui.dialog.ConfirmExitScreen
import com.blacksquircle.ui.feature.editor.ui.dialog.ForceSyntaxScreen
import com.blacksquircle.ui.feature.editor.ui.dialog.GoToLineScreen
import com.blacksquircle.ui.feature.editor.ui.dialog.InsertColorScreen
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorScreen

fun NavGraphBuilder.editorGraph(navController: NavHostController) {
    composable<EditorScreen> {
        EditorScreen(navController)
    }
    dialog<CloseFileDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<CloseFileDialog>()
        CloseFileScreen(navArgs, navController)
    }
    dialog<ForceSyntaxDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<ForceSyntaxDialog>()
        ForceSyntaxScreen(navArgs, navController)
    }
    dialog<GoToLineDialog> {
        GoToLineScreen(navController)
    }
    dialog<InsertColorDialog> {
        InsertColorScreen(navController)
    }
    dialog<ConfirmExitDialog> {
        ConfirmExitScreen(navController)
    }
}