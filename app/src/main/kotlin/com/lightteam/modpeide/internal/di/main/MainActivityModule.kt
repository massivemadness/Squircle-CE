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

package com.lightteam.modpeide.internal.di.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.main.activities.MainActivity
import com.lightteam.modpeide.ui.main.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    @MainScope
    fun provideMainViewModelFactory(
        schedulersProvider: SchedulersProvider,
        preferenceHandler: PreferenceHandler,
        appUpdateManager: AppUpdateManager
    ): MainViewModel.Factory {
        return MainViewModel.Factory(
            schedulersProvider,
            preferenceHandler,
            appUpdateManager
        )
    }

    @Provides
    @MainScope
    fun provideMainViewModel(
        activity: MainActivity,
        factory: MainViewModel.Factory
    ): MainViewModel {
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }

    @Provides
    @MainScope
    fun provideAppUpdateManager(context: Context): AppUpdateManager {
        return AppUpdateManagerFactory.create(context)
    }
}