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

package com.blacksquircle.ui.feature.git.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog
import com.blacksquircle.ui.feature.git.api.navigation.CommitDialog
import com.blacksquircle.ui.feature.git.api.navigation.FetchDialog
import com.blacksquircle.ui.feature.git.api.navigation.GitDialog
import com.blacksquircle.ui.feature.git.api.navigation.PullDialog
import com.blacksquircle.ui.feature.git.api.navigation.PushDialog
import com.blacksquircle.ui.feature.git.ui.checkout.CheckoutScreen
import com.blacksquircle.ui.feature.git.ui.commit.CommitScreen
import com.blacksquircle.ui.feature.git.ui.fetch.FetchScreen
import com.blacksquircle.ui.feature.git.ui.git.GitScreen
import com.blacksquircle.ui.feature.git.ui.pull.PullScreen
import com.blacksquircle.ui.feature.git.ui.push.PushScreen

fun NavGraphBuilder.gitGraph(navController: NavHostController) {
    dialog<GitDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<GitDialog>()
        GitScreen(navArgs, navController)
    }
    dialog<FetchDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<FetchDialog>()
        FetchScreen(navArgs, navController)
    }
    dialog<PullDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<PullDialog>()
        PullScreen(navArgs, navController)
    }
    dialog<CommitDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<CommitDialog>()
        CommitScreen(navArgs, navController)
    }
    dialog<PushDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<PushDialog>()
        PushScreen(navArgs, navController)
    }
    dialog<CheckoutDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<CheckoutDialog>()
        CheckoutScreen(navArgs, navController)
    }
}