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

package com.blacksquircle.ui.feature.themes.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.provideCoreApi
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApi
import com.blacksquircle.ui.feature.fonts.api.internal.provideFontsApi
import com.blacksquircle.ui.feature.themes.api.internal.ThemesApi
import com.blacksquircle.ui.feature.themes.api.internal.provideThemesApi
import com.blacksquircle.ui.feature.themes.ui.themes.ThemesViewModel
import com.blacksquircle.ui.navigation.api.internal.NavigationApi
import com.blacksquircle.ui.navigation.api.internal.provideNavigationApi
import dagger.Component

@ThemesScope
@Component(
    modules = [
        ThemesModule::class,
    ],
    dependencies = [
        CoreApi::class,
        NavigationApi::class,
        FontsApi::class,
        ThemesApi::class,
    ]
)
internal interface ThemesComponent {

    fun inject(factory: ThemesViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApi: CoreApi,
            navigationApi: NavigationApi,
            fontsApi: FontsApi,
            themesApi: ThemesApi,
        ): ThemesComponent
    }

    companion object {

        private var component: ThemesComponent? = null

        fun buildOrGet(context: Context): ThemesComponent {
            return component ?: DaggerThemesComponent.factory().create(
                coreApi = context.provideCoreApi(),
                navigationApi = context.provideNavigationApi(),
                fontsApi = context.provideFontsApi(),
                themesApi = context.provideThemesApi(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}