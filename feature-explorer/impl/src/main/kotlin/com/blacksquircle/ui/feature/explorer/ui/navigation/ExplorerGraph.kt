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

package com.blacksquircle.ui.feature.explorer.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.explorer.api.navigation.AuthDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.CompressDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.CreateDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.DeleteDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.api.navigation.NotificationDeniedDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.PropertiesDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.RenameDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.StorageDeniedDialog
import com.blacksquircle.ui.feature.explorer.api.navigation.TaskDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.AuthScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.CompressScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.CreateScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.DeleteScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.NotificationDeniedScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.PropertiesScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.RenameScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.StorageDeniedScreen
import com.blacksquircle.ui.feature.explorer.ui.dialog.TaskScreen
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerScreen

fun NavGraphBuilder.explorerGraph(navController: NavHostController) {
    composable<ExplorerScreen> {
        ExplorerScreen(navController)
    }
    dialog<AuthDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<AuthDialog>()
        AuthScreen(navArgs, navController)
    }
    dialog<CreateDialog> {
        CreateScreen(navController)
    }
    dialog<RenameDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<RenameDialog>()
        RenameScreen(navArgs, navController)
    }
    dialog<DeleteDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<DeleteDialog>()
        DeleteScreen(navArgs, navController)
    }
    dialog<CompressDialog> {
        CompressScreen(navController)
    }
    dialog<TaskDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<TaskDialog>()
        TaskScreen(navArgs, navController)
    }
    dialog<PropertiesDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<PropertiesDialog>()
        PropertiesScreen(navArgs, navController)
    }
    dialog<StorageDeniedDialog> {
        StorageDeniedScreen(navController)
    }
    dialog<NotificationDeniedDialog> {
        NotificationDeniedScreen(navController)
    }
}