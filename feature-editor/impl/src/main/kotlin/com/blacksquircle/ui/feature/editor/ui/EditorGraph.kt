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

package com.blacksquircle.ui.feature.editor.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.editor.api.navigation.CloseFileRoute
import com.blacksquircle.ui.feature.editor.api.navigation.ConfirmExitRoute
import com.blacksquircle.ui.feature.editor.api.navigation.EditorRoute
import com.blacksquircle.ui.feature.editor.api.navigation.ForceSyntaxRoute
import com.blacksquircle.ui.feature.editor.api.navigation.GoToLineRoute
import com.blacksquircle.ui.feature.editor.api.navigation.InsertColorRoute
import com.blacksquircle.ui.feature.editor.ui.closefile.CloseFileScreen
import com.blacksquircle.ui.feature.editor.ui.confirmexit.ConfirmExitScreen
import com.blacksquircle.ui.feature.editor.ui.editor.EditorScreen
import com.blacksquircle.ui.feature.editor.ui.forcesyntax.ForceSyntaxScreen
import com.blacksquircle.ui.feature.editor.ui.gotoline.GoToLineScreen
import com.blacksquircle.ui.feature.editor.ui.insertcolor.InsertColorScreen

fun NavGraphBuilder.editorGraph(navController: NavHostController) {
    composable<EditorRoute> {
        EditorScreen(navController)
    }
    dialog<CloseFileRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<CloseFileRoute>()
        CloseFileScreen(navArgs, navController)
    }
    dialog<ForceSyntaxRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<ForceSyntaxRoute>()
        ForceSyntaxScreen(navArgs, navController)
    }
    dialog<GoToLineRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<GoToLineRoute>()
        GoToLineScreen(navArgs, navController)
    }
    dialog<InsertColorRoute> {
        InsertColorScreen(navController)
    }
    dialog<ConfirmExitRoute> {
        ConfirmExitScreen(navController)
    }
}