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

package com.blacksquircle.ui.feature.shortcuts.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.KeybindingViewModel
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.ShortcutsViewModel
import dagger.Component

@ShortcutsScope
@Component(
    modules = [
        ShortcutsModule::class,
    ],
    dependencies = [
        CoreApiDepsProvider::class,
    ]
)
internal interface ShortcutsComponent {

    fun inject(factory: ShortcutsViewModel.Factory)
    fun inject(factory: KeybindingViewModel.ParameterizedFactory)

    @Component.Factory
    interface Factory {
        fun create(coreApiDepsProvider: CoreApiDepsProvider): ShortcutsComponent
    }

    companion object {

        private var component: ShortcutsComponent? = null

        fun buildOrGet(context: Context): ShortcutsComponent {
            return component ?: DaggerShortcutsComponent.factory().create(
                coreApiDepsProvider = (context.applicationContext as CoreApiProvider)
                    .provideCoreApiDepsProvider(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}