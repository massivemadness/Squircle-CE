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

package com.blacksquircle.ui.feature.git.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.git.ui.checkout.CheckoutViewModel
import com.blacksquircle.ui.feature.git.ui.commit.CommitViewModel
import com.blacksquircle.ui.feature.git.ui.fetch.FetchViewModel
import com.blacksquircle.ui.feature.git.ui.git.GitViewModel
import com.blacksquircle.ui.feature.git.ui.pull.PullViewModel
import com.blacksquircle.ui.feature.git.ui.push.PushViewModel
import dagger.Component

@GitScope
@Component(
    modules = [
        GitModule::class,
    ],
    dependencies = [
        CoreApiDepsProvider::class,
    ]
)
internal interface GitComponent {

    fun inject(factory: GitViewModel.ParameterizedFactory)
    fun inject(factory: FetchViewModel.ParameterizedFactory)
    fun inject(factory: PullViewModel.ParameterizedFactory)
    fun inject(factory: CommitViewModel.ParameterizedFactory)
    fun inject(factory: PushViewModel.ParameterizedFactory)
    fun inject(factory: CheckoutViewModel.ParameterizedFactory)

    @Component.Factory
    interface Factory {
        fun create(coreApiDepsProvider: CoreApiDepsProvider): GitComponent
    }

    companion object {

        private var component: GitComponent? = null

        fun buildOrGet(context: Context): GitComponent {
            return component ?: DaggerGitComponent.factory().create(
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