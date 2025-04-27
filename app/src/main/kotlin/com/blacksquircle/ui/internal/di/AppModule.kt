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

package com.blacksquircle.ui.internal.di

import android.content.Context
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.internal.provider.coroutine.DispatcherProviderImpl
import com.blacksquircle.ui.internal.provider.resources.StringProviderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object AppModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }

    @Provides
    @Singleton
    fun provideStringProvider(context: Context): StringProvider {
        return StringProviderImpl(context)
    }
}