/*
 * Copyright 2021 Squircle IDE contributors.
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
import com.blacksquircle.ui.BuildConfig
import com.blacksquircle.ui.domain.providers.coroutine.DispatcherProvider
import com.blacksquircle.ui.internal.providers.coroutine.DispatcherProviderImpl
import com.blacksquircle.ui.utils.inappupdate.InAppUpdate
import com.blacksquircle.ui.utils.inappupdate.InAppUpdateImpl
import com.blacksquircle.ui.utils.inappupdate.InAppUpdateStub
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }

    @Provides
    @Singleton
    fun provideInAppUpdate(@ApplicationContext context: Context): InAppUpdate {
        return if (!BuildConfig.DEBUG) {
            InAppUpdateImpl(context)
        } else {
            InAppUpdateStub()
        }
    }
}