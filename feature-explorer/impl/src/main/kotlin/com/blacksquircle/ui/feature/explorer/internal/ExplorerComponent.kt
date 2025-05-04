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

package com.blacksquircle.ui.feature.explorer.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiDepsProvider
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiDepsProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiProvider
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.task.TaskService
import com.blacksquircle.ui.feature.explorer.ui.task.TaskViewModel
import com.blacksquircle.ui.feature.explorer.ui.workspace.AddWorkspaceViewModel
import com.blacksquircle.ui.feature.explorer.ui.workspace.DeleteWorkspaceViewModel
import com.blacksquircle.ui.feature.explorer.ui.workspace.LocalWorkspaceViewModel
import com.blacksquircle.ui.feature.git.api.internal.GitApiDepsProvider
import com.blacksquircle.ui.feature.git.api.internal.GitApiProvider
import com.blacksquircle.ui.feature.servers.api.internal.ServersApiDepsProvider
import com.blacksquircle.ui.feature.servers.api.internal.ServersApiProvider
import dagger.Component

@ExplorerScope
@Component(
    modules = [
        ExplorerModule::class,
    ],
    dependencies = [
        CoreApiDepsProvider::class,
        ExplorerApiDepsProvider::class,
        EditorApiDepsProvider::class,
        GitApiDepsProvider::class,
        ServersApiDepsProvider::class,
    ]
)
internal interface ExplorerComponent {

    fun inject(service: TaskService)
    fun inject(factory: TaskViewModel.ParameterizedFactory)
    fun inject(factory: ExplorerViewModel.Factory)
    fun inject(factory: AddWorkspaceViewModel.Factory)
    fun inject(factory: DeleteWorkspaceViewModel.Factory)
    fun inject(factory: LocalWorkspaceViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApiDepsProvider: CoreApiDepsProvider,
            editorApiDepsProvider: EditorApiDepsProvider,
            explorerApiDepsProvider: ExplorerApiDepsProvider,
            gitApiDepsProvider: GitApiDepsProvider,
            serversApiDepsProvider: ServersApiDepsProvider,
        ): ExplorerComponent
    }

    companion object {

        private var component: ExplorerComponent? = null

        fun buildOrGet(context: Context): ExplorerComponent {
            return component ?: DaggerExplorerComponent.factory().create(
                coreApiDepsProvider = (context.applicationContext as CoreApiProvider)
                    .provideCoreApiDepsProvider(),
                editorApiDepsProvider = (context.applicationContext as EditorApiProvider)
                    .provideEditorApiDepsProvider(),
                explorerApiDepsProvider = (context.applicationContext as ExplorerApiProvider)
                    .provideExplorerApiDepsProvider(),
                gitApiDepsProvider = (context.applicationContext as GitApiProvider)
                    .provideGitApiDepsProvider(),
                serversApiDepsProvider = (context.applicationContext as ServersApiProvider)
                    .provideServersApiDepsProvider(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}