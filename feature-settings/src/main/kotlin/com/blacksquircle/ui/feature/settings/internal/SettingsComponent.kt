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

package com.blacksquircle.ui.feature.settings.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.settings.ui.about.AboutHeaderFragment
import com.blacksquircle.ui.feature.settings.ui.application.AppHeaderFragment
import com.blacksquircle.ui.feature.settings.ui.codestyle.CodeHeaderFragment
import com.blacksquircle.ui.feature.settings.ui.editor.EditorHeaderFragment
import com.blacksquircle.ui.feature.settings.ui.files.FilesHeaderFragment
import com.blacksquircle.ui.feature.settings.ui.header.HeaderListFragment
import dagger.Component

@SettingsScope
@Component(
    dependencies = [
        CoreApiDepsProvider::class,
    ]
)
internal interface SettingsComponent {

    fun inject(fragment: AboutHeaderFragment)
    fun inject(fragment: AppHeaderFragment)
    fun inject(fragment: CodeHeaderFragment)
    fun inject(fragment: EditorHeaderFragment)
    fun inject(fragment: FilesHeaderFragment)
    fun inject(fragment: HeaderListFragment)

    @Component.Factory
    interface Factory {
        fun create(coreApiDepsProvider: CoreApiDepsProvider): SettingsComponent
    }

    companion object {

        private var component: SettingsComponent? = null

        fun buildOrGet(context: Context): SettingsComponent {
            return component ?: DaggerSettingsComponent.factory().create(
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