/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.internal.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.lightteam.modpeide.BaseApplication
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.utils.commons.VersionChecker
import com.lightteam.modpeide.internal.di.scopes.PerApplication
import com.lightteam.modpeide.internal.providers.SchedulersProviderImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    @PerApplication
    fun provideContext(application: BaseApplication): Context
            = application

    @Provides
    @PerApplication
    fun provideSchedulersProvider(): SchedulersProvider
            = SchedulersProviderImpl()

    @Provides
    @PerApplication
    fun provideSharedPreferences(context: Context): SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @PerApplication
    fun provideRxSharedPreferences(sharedPreferences: SharedPreferences): RxSharedPreferences
            = RxSharedPreferences.create(sharedPreferences)

    @Provides
    @PerApplication
    fun providePreferenceHandler(sharedPreferences: SharedPreferences,
                                 rxSharedPreferences: RxSharedPreferences): PreferenceHandler
            = PreferenceHandler(sharedPreferences, rxSharedPreferences)

    @Provides
    @PerApplication
    fun provideVersionChecker(application: BaseApplication): VersionChecker
            = VersionChecker(application.isUltimate)
}