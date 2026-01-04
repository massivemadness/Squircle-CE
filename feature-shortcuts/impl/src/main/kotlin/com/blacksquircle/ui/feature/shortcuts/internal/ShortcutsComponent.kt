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

package com.blacksquircle.ui.feature.shortcuts.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.provideCoreApi
import com.blacksquircle.ui.feature.shortcuts.ui.conflict.ConflictKeyViewModel
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.KeybindingViewModel
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.ShortcutsViewModel
import dagger.Component

@ShortcutsScope
@Component(
    modules = [
        ShortcutsModule::class,
    ],
    dependencies = [
        CoreApi::class,
    ]
)
internal interface ShortcutsComponent {

    fun inject(factory: ShortcutsViewModel.Factory)
    fun inject(factory: KeybindingViewModel.ParameterizedFactory)
    fun inject(factory: ConflictKeyViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(coreApi: CoreApi): ShortcutsComponent
    }

    companion object {

        private var component: ShortcutsComponent? = null

        fun buildOrGet(context: Context): ShortcutsComponent {
            return component ?: DaggerShortcutsComponent.factory().create(
                coreApi = context.provideCoreApi(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}