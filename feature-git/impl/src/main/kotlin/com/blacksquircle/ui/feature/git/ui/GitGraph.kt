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

package com.blacksquircle.ui.feature.git.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutRoute
import com.blacksquircle.ui.feature.git.api.navigation.CommitRoute
import com.blacksquircle.ui.feature.git.api.navigation.FetchRoute
import com.blacksquircle.ui.feature.git.api.navigation.PullRoute
import com.blacksquircle.ui.feature.git.api.navigation.PushRoute
import com.blacksquircle.ui.feature.git.ui.checkout.CheckoutScreen
import com.blacksquircle.ui.feature.git.ui.commit.CommitScreen
import com.blacksquircle.ui.feature.git.ui.fetch.FetchScreen
import com.blacksquircle.ui.feature.git.ui.pull.PullScreen
import com.blacksquircle.ui.feature.git.ui.push.PushScreen

fun NavGraphBuilder.gitGraph(navController: NavHostController) {
    dialog<FetchRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<FetchRoute>()
        FetchScreen(navArgs, navController)
    }
    dialog<PullRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<PullRoute>()
        PullScreen(navArgs, navController)
    }
    dialog<CommitRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<CommitRoute>()
        CommitScreen(navArgs, navController)
    }
    dialog<PushRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<PushRoute>()
        PushScreen(navArgs, navController)
    }
    dialog<CheckoutRoute> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<CheckoutRoute>()
        CheckoutScreen(navArgs, navController)
    }
}