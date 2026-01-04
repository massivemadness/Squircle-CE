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

package com.blacksquircle.ui.feature.servers.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.provideCoreApi
import com.blacksquircle.ui.feature.servers.ui.details.ServerDetailsViewModel
import com.blacksquircle.ui.feature.servers.ui.list.ServerListViewModel
import com.blacksquircle.ui.navigation.api.internal.NavigationApi
import com.blacksquircle.ui.navigation.api.internal.provideNavigationApi
import dagger.Component

@ServersScope
@Component(
    modules = [
        ServersModule::class,
    ],
    dependencies = [
        CoreApi::class,
        NavigationApi::class,
    ]
)
internal interface ServersComponent {

    fun inject(factory: ServerListViewModel.Factory)
    fun inject(factory: ServerDetailsViewModel.ParameterizedFactory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApi: CoreApi,
            navigationApi: NavigationApi,
        ): ServersComponent
    }

    companion object {

        private var component: ServersComponent? = null

        fun buildOrGet(context: Context): ServersComponent {
            return component ?: DaggerServersComponent.factory().create(
                coreApi = context.provideCoreApi(),
                navigationApi = context.provideNavigationApi(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}