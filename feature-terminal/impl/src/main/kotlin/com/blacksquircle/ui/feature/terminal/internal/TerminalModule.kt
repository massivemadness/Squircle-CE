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

package com.blacksquircle.ui.feature.terminal.internal

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.terminal.data.manager.RuntimeManagerImpl
import com.blacksquircle.ui.feature.terminal.data.manager.SessionManagerImpl
import com.blacksquircle.ui.feature.terminal.data.runtime.AndroidRuntime
import com.blacksquircle.ui.feature.terminal.domain.manager.RuntimeManager
import com.blacksquircle.ui.feature.terminal.domain.manager.SessionManager
import com.blacksquircle.ui.feature.terminal.domain.runtime.TerminalRuntime
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
internal object TerminalModule {

    @Provides
    @TerminalScope
    fun provideSessionManager(): SessionManager {
        return SessionManagerImpl()
    }

    @Provides
    @TerminalScope
    fun provideRuntimeManager(
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        runtimeSet: @JvmSuppressWildcards Set<TerminalRuntime>,
    ): RuntimeManager {
        return RuntimeManagerImpl(
            dispatcherProvider = dispatcherProvider,
            settingsManager = settingsManager,
            runtimeSet = runtimeSet,
            installerMap = emptyMap(),
        )
    }

    @IntoSet
    @Provides
    fun provideAndroidRuntime(): TerminalRuntime {
        return AndroidRuntime
    }
}