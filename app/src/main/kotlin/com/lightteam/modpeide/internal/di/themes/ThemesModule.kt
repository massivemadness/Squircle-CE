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

package com.lightteam.modpeide.internal.di.themes

import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.modpeide.data.utils.commons.PreferenceHandler
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.internal.di.settings.SettingsScope
import com.lightteam.modpeide.ui.settings.activities.SettingsActivity
import com.lightteam.modpeide.ui.themes.viewmodel.ThemesViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ThemesModule {

    @Provides
    @SettingsScope // @ThemesScope
    fun provideThemesViewModelFactory(
        schedulersProvider: SchedulersProvider,
        preferenceHandler: PreferenceHandler,
        appDatabase: AppDatabase,
        @Named("Local")
        filesystem: Filesystem,
        gson: Gson
    ): ThemesViewModel.Factory {
        return ThemesViewModel.Factory(
            schedulersProvider,
            preferenceHandler,
            appDatabase,
            filesystem,
            gson
        )
    }

    @Provides
    @SettingsScope // @ThemesScope
    fun provideThemesViewModel(
        activity: SettingsActivity,
        factory: ThemesViewModel.Factory
    ): ThemesViewModel {
        return ViewModelProvider(activity, factory).get(ThemesViewModel::class.java)
    }
}