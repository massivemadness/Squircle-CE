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

package com.blacksquircle.ui.feature.servers.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.servers.api.navigation.CloudScreen
import com.blacksquircle.ui.feature.servers.api.navigation.ServerDialog
import com.blacksquircle.ui.feature.servers.ui.cloud.CloudScreen
import com.blacksquircle.ui.feature.servers.ui.server.ServerScreen

fun NavGraphBuilder.serversGraph(navController: NavHostController) {
    composable<CloudScreen> {
        CloudScreen(navController)
    }
    dialog<ServerDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<ServerDialog>()
        ServerScreen(navArgs, navController)
    }
}