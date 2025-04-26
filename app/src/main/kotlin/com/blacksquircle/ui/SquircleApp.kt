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

package com.blacksquircle.ui

import android.app.Application
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.core.logger.AndroidTree
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiDepsProvider
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiDepsProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiDepsProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiProvider
import com.blacksquircle.ui.feature.git.api.internal.GitApiDepsProvider
import com.blacksquircle.ui.feature.git.api.internal.GitApiProvider
import com.blacksquircle.ui.feature.servers.api.internal.ServersApiDepsProvider
import com.blacksquircle.ui.feature.servers.api.internal.ServersApiProvider
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApiDepsProvider
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApiProvider
import com.blacksquircle.ui.feature.themes.api.internal.ThemesApiDepsProvider
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
    ThemesApiProvider {

    private val appComponent: AppComponent
        get() = AppComponent.buildOrGet(this)

    override fun onCreate() {
        super.onCreate()
        AppComponent.buildOrGet(this)
        Timber.plant(AndroidTree())
    }

    // region DAGGER

    override fun provideCoreApiDepsProvider(): CoreApiDepsProvider = appComponent

    override fun provideEditorApiDepsProvider(): EditorApiDepsProvider = appComponent

    override fun provideExplorerApiDepsProvider(): ExplorerApiDepsProvider = appComponent

    override fun provideFontsApiDepsProvider(): FontsApiDepsProvider = appComponent

    override fun provideGitApiDepsProvider(): GitApiDepsProvider = appComponent

    override fun provideServersApiDepsProvider(): ServersApiDepsProvider = appComponent

    override fun provideShortcutsApiDepsProvider(): ShortcutsApiDepsProvider = appComponent

    override fun provideThemesApiDepsProvider(): ThemesApiDepsProvider = appComponent

    // endregion
}