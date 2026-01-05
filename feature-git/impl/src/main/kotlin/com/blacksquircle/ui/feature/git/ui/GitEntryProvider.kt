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

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
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
import com.blacksquircle.ui.navigation.api.provider.EntryProvider

internal class GitEntryProvider : EntryProvider {

    override fun EntryProviderScope<NavKey>.builder() {
        entry<FetchRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            FetchScreen(navArgs)
        }
        entry<PullRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            PullScreen(navArgs)
        }
        entry<CommitRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            CommitScreen(navArgs)
        }
        entry<PushRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            PushScreen(navArgs)
        }
        entry<CheckoutRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            CheckoutScreen(navArgs)
        }
    }
}