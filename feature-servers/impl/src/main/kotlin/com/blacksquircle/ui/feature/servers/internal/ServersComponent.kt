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

package com.blacksquircle.ui.feature.servers.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.servers.ui.cloud.CloudViewModel
import com.blacksquircle.ui.feature.servers.ui.server.ServerViewModel
import dagger.Component

@ServersScope
@Component(
    modules = [
        ServersModule::class,
    ],
    dependencies = [
        CoreApiDepsProvider::class,
    ]
)
internal interface ServersComponent {

    fun inject(factory: CloudViewModel.Factory)
    fun inject(factory: ServerViewModel.ParameterizedFactory)

    @Component.Factory
    interface Factory {
        fun create(coreApiDepsProvider: CoreApiDepsProvider): ServersComponent
    }

    companion object {

        private var component: ServersComponent? = null

        fun buildOrGet(context: Context): ServersComponent {
            return component ?: DaggerServersComponent.factory().create(
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