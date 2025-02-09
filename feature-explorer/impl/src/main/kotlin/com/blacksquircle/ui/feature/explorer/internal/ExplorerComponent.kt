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
import com.blacksquircle.ui.feature.explorer.ui.dialog.AuthDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.CompressDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.CreateDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.DeleteDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.ProgressDialog
import com.blacksquircle.ui.feature.explorer.ui.dialog.RenameDialog
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerFragment
import com.blacksquircle.ui.feature.explorer.ui.service.FileService
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
        EditorApiDepsProvider::class,
        ServersApiDepsProvider::class,
    ]
)
internal interface ExplorerComponent {

    fun inject(dialog: AuthDialog)
    fun inject(dialog: CompressDialog)
    fun inject(dialog: CreateDialog)
    fun inject(dialog: DeleteDialog)
    fun inject(dialog: ProgressDialog)
    fun inject(dialog: RenameDialog)
    fun inject(fragment: ExplorerFragment)
    fun inject(service: FileService)

    @Component.Factory
    interface Factory {
        fun create(
            coreApiDepsProvider: CoreApiDepsProvider,
            editorApiDepsProvider: EditorApiDepsProvider,
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