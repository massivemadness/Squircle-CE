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

package com.blacksquircle.ui.feature.themes.internal.api

import android.content.Context
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.themes.api.interactor.ThemeInteractor
import com.blacksquircle.ui.feature.themes.data.interactor.ThemeInteractorImpl
import com.blacksquircle.ui.feature.themes.ui.ThemesEntryProvider
import com.blacksquircle.ui.navigation.api.provider.EntryProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
object ThemesApiModule {

    @Provides
    @Singleton
    fun provideThemeInteractor(
        dispatcherProvider: DispatcherProvider,
        jsonParser: Json,
        context: Context,
    ): ThemeInteractor {
        return ThemeInteractorImpl(
            dispatcherProvider = dispatcherProvider,
            jsonParser = jsonParser,
            context = context,
        )
    }

    @IntoSet
    @Provides
    fun provideThemesEntryProvider(): EntryProvider {
        return ThemesEntryProvider()
    }
}