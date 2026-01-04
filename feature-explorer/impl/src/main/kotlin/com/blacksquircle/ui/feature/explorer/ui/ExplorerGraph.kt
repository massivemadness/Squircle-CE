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

package com.blacksquircle.ui.feature.explorer.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.ds.extensions.LocalNavController
import com.blacksquircle.ui.feature.explorer.api.navigation.AddWorkspaceRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.CloneRepoRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.CompressFileRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.CreateFileRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteFileRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteWorkspaceRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.ExplorerRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.LocalWorkspaceRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.NotificationDeniedRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.PropertiesRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.RenameFileRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.ServerAuthRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.StorageDeniedRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.TaskRoute
import com.blacksquircle.ui.feature.explorer.ui.auth.AuthScreen
import com.blacksquircle.ui.feature.explorer.ui.clone.CloneRepoScreen
import com.blacksquircle.ui.feature.explorer.ui.compress.CompressScreen
import com.blacksquircle.ui.feature.explorer.ui.create.CreateFileScreen
import com.blacksquircle.ui.feature.explorer.ui.delete.DeleteScreen
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.permissions.NotificationDeniedScreen
import com.blacksquircle.ui.feature.explorer.ui.permissions.StorageDeniedScreen
import com.blacksquircle.ui.feature.explorer.ui.properties.PropertiesScreen
import com.blacksquircle.ui.feature.explorer.ui.rename.RenameScreen
import com.blacksquircle.ui.feature.explorer.ui.task.TaskScreen
import com.blacksquircle.ui.feature.explorer.ui.workspace.AddWorkspaceScreen
import com.blacksquircle.ui.feature.explorer.ui.workspace.DeleteWorkspaceScreen
import com.blacksquircle.ui.feature.explorer.ui.workspace.LocalWorkspaceScreen

fun NavGraphBuilder.explorerGraph(navController: NavHostController) {
    composable<ExplorerRoute> {
        ExplorerScreen(navController)
    }
    dialog<ServerAuthRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<ServerAuthRoute>()
        AuthScreen(navArgs, navController)
    }
    dialog<CreateFileRoute> {
        CreateFileScreen(navController)
    }
    dialog<CloneRepoRoute> {
        CloneRepoScreen(navController)
    }
    dialog<RenameFileRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<RenameFileRoute>()
        RenameScreen(navArgs, navController)
    }
    dialog<DeleteFileRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<DeleteFileRoute>()
        DeleteScreen(navArgs, navController)
    }
    dialog<CompressFileRoute> {
        CompressScreen(navController)
    }
    dialog<TaskRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<TaskRoute>()
        TaskScreen(navArgs, navController)
    }
    dialog<PropertiesRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<PropertiesRoute>()
        PropertiesScreen(navArgs, navController)
    }
    dialog<StorageDeniedRoute> {
        StorageDeniedScreen(navController)
    }
    dialog<NotificationDeniedRoute> {
        NotificationDeniedScreen(navController)
    }
    dialog<AddWorkspaceRoute> {
        AddWorkspaceScreen(navController)
    }
    dialog<DeleteWorkspaceRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<DeleteWorkspaceRoute>()
        DeleteWorkspaceScreen(navArgs, navController)
    }
    dialog<LocalWorkspaceRoute> {
        LocalWorkspaceScreen(navController)
    }
}

/**
 * FIXME Requires :feature-explorer:impl dependency.
 * Waiting for navigation3?
 */
@Composable
fun DrawerExplorer(closeDrawer: () -> Unit) {
    ExplorerScreen(
        navController = LocalNavController.current,
        closeDrawer = closeDrawer,
    )
}