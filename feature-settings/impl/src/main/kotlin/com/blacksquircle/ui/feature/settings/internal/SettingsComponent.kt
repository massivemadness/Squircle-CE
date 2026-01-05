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

package com.blacksquircle.ui.feature.settings.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.provideCoreApi
import com.blacksquircle.ui.feature.settings.ui.about.AboutHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.application.AppHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.codestyle.CodeHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.editor.EditorHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.files.FilesHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.git.GitHeaderViewModel
import com.blacksquircle.ui.feature.settings.ui.header.HeaderListViewModel
import com.blacksquircle.ui.feature.settings.ui.terminal.TerminalHeaderViewModel
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApi
import com.blacksquircle.ui.feature.terminal.api.internal.provideTerminalApi
import com.blacksquircle.ui.navigation.api.internal.NavigationApi
import com.blacksquircle.ui.navigation.api.internal.provideNavigationApi
import dagger.Component

@SettingsScope
@Component(
    dependencies = [
        CoreApi::class,
        NavigationApi::class,
        TerminalApi::class,
    ]
)
internal interface SettingsComponent {

    fun inject(factory: HeaderListViewModel.Factory)
    fun inject(factory: AppHeaderViewModel.Factory)
    fun inject(factory: CodeHeaderViewModel.Factory)
    fun inject(factory: EditorHeaderViewModel.Factory)
    fun inject(factory: FilesHeaderViewModel.Factory)
    fun inject(factory: TerminalHeaderViewModel.Factory)
    fun inject(factory: GitHeaderViewModel.Factory)
    fun inject(factory: AboutHeaderViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApi: CoreApi,
            navigationApi: NavigationApi,
            terminalApi: TerminalApi,
        ): SettingsComponent
    }

    companion object {

        private var component: SettingsComponent? = null

        fun buildOrGet(context: Context): SettingsComponent {
            return component ?: DaggerSettingsComponent.factory().create(
                coreApi = context.provideCoreApi(),
                navigationApi = context.provideNavigationApi(),
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