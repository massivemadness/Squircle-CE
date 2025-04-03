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

package com.blacksquircle.ui.feature.themes.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiDepsProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiProvider
import com.blacksquircle.ui.feature.themes.ui.thememaker.EditThemeViewModel
import com.blacksquircle.ui.feature.themes.ui.themes.ThemesViewModel
import dagger.Component

@ThemesScope
@Component(
    modules = [
        ThemesModule::class,
    ],
    dependencies = [
        CoreApiDepsProvider::class,
        FontsApiDepsProvider::class,
    ]
)
internal interface ThemesComponent {

    fun inject(factory: ThemesViewModel.Factory)
    fun inject(factory: EditThemeViewModel.ParameterizedFactory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApiDepsProvider: CoreApiDepsProvider,
            fontsApiDepsProvider: FontsApiDepsProvider,
        ): ThemesComponent
    }

    companion object {

        private var component: ThemesComponent? = null

        fun buildOrGet(context: Context): ThemesComponent {
            return component ?: DaggerThemesComponent.factory().create(
                coreApiDepsProvider = (context.applicationContext as CoreApiProvider)
                    .provideCoreApiDepsProvider(),
                fontsApiDepsProvider = (context.applicationContext as FontsApiProvider)
                    .provideFontsApiDepsProvider()
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}