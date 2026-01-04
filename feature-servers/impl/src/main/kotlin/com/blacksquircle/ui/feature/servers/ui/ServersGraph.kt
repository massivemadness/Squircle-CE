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

package com.blacksquircle.ui.feature.servers.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.servers.api.navigation.ServerDetailsRoute
import com.blacksquircle.ui.feature.servers.api.navigation.ServerListRoute
import com.blacksquircle.ui.feature.servers.ui.details.ServerDetailsScreen
import com.blacksquircle.ui.feature.servers.ui.list.ServerListScreen

fun NavGraphBuilder.serversGraph(navController: NavHostController) {
    composable<ServerListRoute> {
        ServerListScreen(navController)
    }
    dialog<ServerDetailsRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<ServerDetailsRoute>()
        ServerDetailsScreen(navArgs, navController)
    }
}