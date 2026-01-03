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

package com.blacksquircle.ui.feature.terminal.internal.api

import android.content.Context
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.terminal.api.interactor.TerminalInteractor
import com.blacksquircle.ui.feature.terminal.data.interactor.TerminalInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TerminalApiModule {

    @Provides
    @Singleton
    fun provideTerminalInteractor(
        settingsManager: SettingsManager,
        context: Context,
    ): TerminalInteractor {
        return TerminalInteractorImpl(
            settingsManager = settingsManager,
            context = context
        )
    }
}