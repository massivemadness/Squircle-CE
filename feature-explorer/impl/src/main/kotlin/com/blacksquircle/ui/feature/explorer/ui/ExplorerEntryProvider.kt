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

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
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
import com.blacksquircle.ui.feature.explorer.ui.auth.ServerAuthScreen
import com.blacksquircle.ui.feature.explorer.ui.clone.CloneRepoScreen
import com.blacksquircle.ui.feature.explorer.ui.compress.CompressFileScreen
import com.blacksquircle.ui.feature.explorer.ui.create.CreateFileScreen
import com.blacksquircle.ui.feature.explorer.ui.delete.DeleteFileScreen
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.permissions.NotificationDeniedScreen
import com.blacksquircle.ui.feature.explorer.ui.permissions.StorageDeniedScreen
import com.blacksquircle.ui.feature.explorer.ui.properties.PropertiesScreen
import com.blacksquircle.ui.feature.explorer.ui.rename.RenameFileScreen
import com.blacksquircle.ui.feature.explorer.ui.task.TaskScreen
import com.blacksquircle.ui.feature.explorer.ui.workspace.AddWorkspaceScreen
import com.blacksquircle.ui.feature.explorer.ui.workspace.DeleteWorkspaceScreen
import com.blacksquircle.ui.feature.explorer.ui.workspace.LocalWorkspaceScreen
import com.blacksquircle.ui.navigation.api.provider.EntryProvider

internal class ExplorerEntryProvider : EntryProvider {

    override fun EntryProviderScope<NavKey>.builder() {
        entry<ExplorerRoute> {
            ExplorerScreen()
        }
        entry<ServerAuthRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            ServerAuthScreen(navArgs)
        }
        entry<CreateFileRoute>(metadata = DialogSceneStrategy.dialog()) {
            CreateFileScreen()
        }
        entry<CloneRepoRoute>(metadata = DialogSceneStrategy.dialog()) {
            CloneRepoScreen()
        }
        entry<RenameFileRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            RenameFileScreen(navArgs)
        }
        entry<DeleteFileRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            DeleteFileScreen(navArgs)
        }
        entry<CompressFileRoute>(metadata = DialogSceneStrategy.dialog()) {
            CompressFileScreen()
        }
        entry<TaskRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            TaskScreen(navArgs)
        }
        entry<PropertiesRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            PropertiesScreen(navArgs)
        }
        entry<StorageDeniedRoute>(metadata = DialogSceneStrategy.dialog()) {
            StorageDeniedScreen()
        }
        entry<NotificationDeniedRoute>(metadata = DialogSceneStrategy.dialog()) {
            NotificationDeniedScreen()
        }
        entry<AddWorkspaceRoute>(metadata = DialogSceneStrategy.dialog()) {
            AddWorkspaceScreen()
        }
        entry<DeleteWorkspaceRoute>(metadata = DialogSceneStrategy.dialog()) { navArgs ->
            DeleteWorkspaceScreen(navArgs)
        }
        entry<LocalWorkspaceRoute>(metadata = DialogSceneStrategy.dialog()) {
            LocalWorkspaceScreen()
        }
    }
}