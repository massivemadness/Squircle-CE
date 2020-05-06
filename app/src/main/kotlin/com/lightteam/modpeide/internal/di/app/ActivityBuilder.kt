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

package com.lightteam.modpeide.internal.di.app

import com.lightteam.modpeide.internal.di.fonts.FontsFragmentsProvider
import com.lightteam.modpeide.internal.di.fonts.FontsModule
import com.lightteam.modpeide.internal.di.main.MainModule
import com.lightteam.modpeide.internal.di.main.MainFragmentsProvider
import com.lightteam.modpeide.internal.di.main.MainScope
import com.lightteam.modpeide.internal.di.settings.SettingsModule
import com.lightteam.modpeide.internal.di.settings.SettingsFragmentsProvider
import com.lightteam.modpeide.internal.di.settings.SettingsScope
import com.lightteam.modpeide.internal.di.themes.ThemesFragmentsProvider
import com.lightteam.modpeide.internal.di.themes.ThemesModule
import com.lightteam.modpeide.ui.main.activities.MainActivity
import com.lightteam.modpeide.ui.settings.activities.SettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @MainScope
    @ContributesAndroidInjector(modules = [
        MainModule::class,
        MainFragmentsProvider::class
    ])
    abstract fun buildMainActivity(): MainActivity

    @SettingsScope
    @ContributesAndroidInjector(modules = [
        SettingsModule::class,
        SettingsFragmentsProvider::class,
        ThemesModule::class,
        ThemesFragmentsProvider::class,
        FontsModule::class,
        FontsFragmentsProvider::class
    ])
    abstract fun buildSettingsActivity(): SettingsActivity
}