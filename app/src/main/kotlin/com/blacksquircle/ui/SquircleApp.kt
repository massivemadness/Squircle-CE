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

package com.blacksquircle.ui

import android.app.Application
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.core.logger.AndroidTree
import com.blacksquircle.ui.feature.editor.api.internal.EditorApi
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApi
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApi
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiProvider
import com.blacksquircle.ui.feature.git.api.internal.GitApi
import com.blacksquircle.ui.feature.git.api.internal.GitApiProvider
import com.blacksquircle.ui.feature.servers.api.internal.ServersApi
import com.blacksquircle.ui.feature.servers.api.internal.ServersApiProvider
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApi
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApiProvider
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApi
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApiProvider
import com.blacksquircle.ui.feature.themes.api.internal.ThemesApi
import com.blacksquircle.ui.feature.themes.api.internal.ThemesApiProvider
import com.blacksquircle.ui.internal.di.AppComponent
import timber.log.Timber

internal class SquircleApp : Application(),
    CoreApiProvider,
    EditorApiProvider,
    ExplorerApiProvider,
    FontsApiProvider,
    GitApiProvider,
    ServersApiProvider,
    ShortcutsApiProvider,
    TerminalApiProvider,
    ThemesApiProvider {

    private val appComponent: AppComponent
        get() = AppComponent.buildOrGet(this)

    override fun onCreate() {
        super.onCreate()
        AppComponent.buildOrGet(this)
        Timber.plant(AndroidTree())
    }

    // region DAGGER

    override fun provideCoreApi(): CoreApi = appComponent

    override fun provideEditorApi(): EditorApi = appComponent

    override fun provideExplorerApi(): ExplorerApi = appComponent

    override fun provideFontsApi(): FontsApi = appComponent

    override fun provideGitApi(): GitApi = appComponent

    override fun provideServersApi(): ServersApi = appComponent

    override fun provideShortcutsApi(): ShortcutsApi = appComponent

    override fun provideTerminalApi(): TerminalApi = appComponent

    override fun provideThemesApi(): ThemesApi = appComponent

    // endregion
}