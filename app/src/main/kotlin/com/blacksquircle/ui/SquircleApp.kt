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
import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.core.logger.AndroidTree
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.theme.Theme
import com.blacksquircle.ui.core.theme.ThemeManager
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiDepsProvider
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiDepsProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiDepsProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiProvider
import com.blacksquircle.ui.feature.servers.api.internal.ServersApiDepsProvider
import com.blacksquircle.ui.feature.servers.api.internal.ServersApiProvider
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApiDepsProvider
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApiProvider
import com.blacksquircle.ui.feature.themes.api.internal.ThemesApiDepsProvider
import com.blacksquircle.ui.feature.themes.api.internal.ThemesApiProvider
import com.blacksquircle.ui.internal.di.AppComponent
import timber.log.Timber

class SquircleApp : Application(),
    CoreApiProvider,
    EditorApiProvider,
    ExplorerApiProvider,
    FontsApiProvider,
    ServersApiProvider,
    ShortcutsApiProvider,
    ThemesApiProvider {

    override fun attachBaseContext(base: Context) {
        val settingsManager = SettingsManager(base)
        val themeManager = ThemeManager(base)
        val theme = Theme.of(settingsManager.theme)
        themeManager.apply(theme)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        AppComponent.buildOrGet(this)
        Timber.plant(AndroidTree())
    }

    // region DAGGER

    override fun provideCoreApiDepsProvider(): CoreApiDepsProvider {
        return AppComponent.buildOrGet(this)
    }

    override fun provideEditorApiDepsProvider(): EditorApiDepsProvider {
        return AppComponent.buildOrGet(this)
    }

    override fun provideExplorerApiDepsProvider(): ExplorerApiDepsProvider {
        return AppComponent.buildOrGet(this)
    }

    override fun provideFontsApiDepsProvider(): FontsApiDepsProvider {
        return AppComponent.buildOrGet(this)
    }

    override fun provideServersApiDepsProvider(): ServersApiDepsProvider {
        return AppComponent.buildOrGet(this)
    }

    override fun provideShortcutsApiDepsProvider(): ShortcutsApiDepsProvider {
        return AppComponent.buildOrGet(this)
    }

    override fun provideThemesApiDepsProvider(): ThemesApiDepsProvider {
        return AppComponent.buildOrGet(this)
    }

    // endregion
}