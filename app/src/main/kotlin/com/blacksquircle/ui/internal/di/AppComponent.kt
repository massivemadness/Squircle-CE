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

package com.blacksquircle.ui.internal.di

import android.content.Context
import com.blacksquircle.ui.application.MainViewModel
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.CoreModule
import com.blacksquircle.ui.feature.editor.api.internal.EditorApi
import com.blacksquircle.ui.feature.editor.internal.api.EditorApiModule
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApi
import com.blacksquircle.ui.feature.explorer.internal.api.ExplorerApiModule
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApi
import com.blacksquircle.ui.feature.fonts.internal.api.FontsApiModule
import com.blacksquircle.ui.feature.git.api.internal.GitApi
import com.blacksquircle.ui.feature.git.internal.api.GitApiModule
import com.blacksquircle.ui.feature.servers.api.internal.ServersApi
import com.blacksquircle.ui.feature.servers.internal.api.ServersApiModule
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApi
import com.blacksquircle.ui.feature.shortcuts.internal.api.ShortcutsApiModule
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApi
import com.blacksquircle.ui.feature.terminal.internal.api.TerminalApiModule
import com.blacksquircle.ui.feature.themes.api.internal.ThemesApi
import com.blacksquircle.ui.feature.themes.internal.api.ThemesApiModule
import com.blacksquircle.ui.navigation.api.internal.NavigationApi
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        CoreModule::class,
        EditorApiModule::class,
        ExplorerApiModule::class,
        FontsApiModule::class,
        GitApiModule::class,
        ServersApiModule::class,
        ShortcutsApiModule::class,
        TerminalApiModule::class,
        ThemesApiModule::class,
    ],
)
internal interface AppComponent :
    CoreApi,
    NavigationApi,
    EditorApi,
    ExplorerApi,
    FontsApi,
    GitApi,
    ServersApi,
    ShortcutsApi,
    TerminalApi,
    ThemesApi {

    fun inject(factory: MainViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    companion object {

        private var component: AppComponent? = null

        fun buildOrGet(context: Context): AppComponent {
            return component ?: DaggerAppComponent.factory().create(context).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}