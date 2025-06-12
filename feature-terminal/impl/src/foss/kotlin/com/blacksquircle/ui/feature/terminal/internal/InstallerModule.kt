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

package com.blacksquircle.ui.feature.terminal.internal

import android.content.Context
import com.blacksquircle.ui.feature.terminal.api.model.RuntimeType
import com.blacksquircle.ui.feature.terminal.data.installer.AlpineInstaller
import com.blacksquircle.ui.feature.terminal.data.network.AlpineApi
import com.blacksquircle.ui.feature.terminal.domain.installer.RuntimeInstaller
import com.blacksquircle.ui.feature.terminal.internal.multibindings.RuntimeKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

@Module
internal object InstallerModule {

    @Provides
    @IntoMap
    @RuntimeKey(RuntimeType.ALPINE)
    fun provideAlpineInstaller(
        alpineApi: AlpineApi,
        context: Context,
    ): RuntimeInstaller {
        return AlpineInstaller(
            alpineApi = alpineApi,
            context = context,
        )
    }

    @Provides
    fun provideAlpineApi(retrofit: Retrofit): AlpineApi {
        return retrofit.create(AlpineApi::class.java)
    }
}