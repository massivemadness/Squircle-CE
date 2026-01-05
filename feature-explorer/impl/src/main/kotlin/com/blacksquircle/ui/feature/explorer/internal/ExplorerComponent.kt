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

package com.blacksquircle.ui.feature.explorer.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.provideCoreApi
import com.blacksquircle.ui.feature.editor.api.internal.EditorApi
import com.blacksquircle.ui.feature.editor.api.internal.provideEditorApi
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApi
import com.blacksquircle.ui.feature.explorer.api.internal.provideExplorerApi
import com.blacksquircle.ui.feature.explorer.ui.auth.ServerAuthViewModel
import com.blacksquircle.ui.feature.explorer.ui.clone.CloneRepoViewModel
import com.blacksquircle.ui.feature.explorer.ui.compress.CompressFileViewModel
import com.blacksquircle.ui.feature.explorer.ui.create.CreateFileViewModel
import com.blacksquircle.ui.feature.explorer.ui.delete.DeleteFileViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.permissions.PermissionViewModel
import com.blacksquircle.ui.feature.explorer.ui.properties.PropertiesViewModel
import com.blacksquircle.ui.feature.explorer.ui.rename.RenameFileViewModel
import com.blacksquircle.ui.feature.explorer.ui.task.TaskService
import com.blacksquircle.ui.feature.explorer.ui.task.TaskViewModel
import com.blacksquircle.ui.feature.explorer.ui.workspace.AddWorkspaceViewModel
import com.blacksquircle.ui.feature.explorer.ui.workspace.DeleteWorkspaceViewModel
import com.blacksquircle.ui.feature.explorer.ui.workspace.LocalWorkspaceViewModel
import com.blacksquircle.ui.feature.git.api.internal.GitApi
import com.blacksquircle.ui.feature.git.api.internal.provideGitApi
import com.blacksquircle.ui.feature.servers.api.internal.ServersApi
import com.blacksquircle.ui.feature.servers.api.internal.provideServersApi
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApi
import com.blacksquircle.ui.feature.terminal.api.internal.provideTerminalApi
import com.blacksquircle.ui.navigation.api.internal.NavigationApi
import com.blacksquircle.ui.navigation.api.internal.provideNavigationApi
import dagger.Component

@ExplorerScope
@Component(
    modules = [
        ExplorerModule::class,
    ],
    dependencies = [
        CoreApi::class,
        NavigationApi::class,
        ExplorerApi::class,
        EditorApi::class,
        GitApi::class,
        ServersApi::class,
        TerminalApi::class,
    ]
)
internal interface ExplorerComponent {

    fun inject(service: TaskService)
    fun inject(factory: TaskViewModel.ParameterizedFactory)
    fun inject(factory: ExplorerViewModel.Factory)
    fun inject(factory: ServerAuthViewModel.Factory)
    fun inject(factory: CreateFileViewModel.Factory)
    fun inject(factory: RenameFileViewModel.Factory)
    fun inject(factory: DeleteFileViewModel.Factory)
    fun inject(factory: PropertiesViewModel.Factory)
    fun inject(factory: CloneRepoViewModel.Factory)
    fun inject(factory: CompressFileViewModel.Factory)
    fun inject(factory: PermissionViewModel.Factory)
    fun inject(factory: AddWorkspaceViewModel.Factory)
    fun inject(factory: DeleteWorkspaceViewModel.Factory)
    fun inject(factory: LocalWorkspaceViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApi: CoreApi,
            navigationApi: NavigationApi,
            editorApi: EditorApi,
            explorerApi: ExplorerApi,
            gitApi: GitApi,
            serversApi: ServersApi,
            terminalApi: TerminalApi,
        ): ExplorerComponent
    }

    companion object {

        private var component: ExplorerComponent? = null

        fun buildOrGet(context: Context): ExplorerComponent {
            return component ?: DaggerExplorerComponent.factory().create(
                coreApi = context.provideCoreApi(),
                navigationApi = context.provideNavigationApi(),
                editorApi = context.provideEditorApi(),
                explorerApi = context.provideExplorerApi(),
                gitApi = context.provideGitApi(),
                serversApi = context.provideServersApi(),
                terminalApi = context.provideTerminalApi(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}