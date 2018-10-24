/*
 * Copyright (C) 2018 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide;

import android.app.Activity;

import com.KillerBLS.modpeide.activity.MainActivity;
import com.KillerBLS.modpeide.activity.SettingsActivity;
import com.KillerBLS.modpeide.activity.SplashActivity;
import com.KillerBLS.modpeide.activity.dagger.MainActivityComponent;
import com.KillerBLS.modpeide.activity.dagger.SettingsActivityComponent;
import com.KillerBLS.modpeide.activity.dagger.SplashActivityComponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {SplashActivityComponent.class, MainActivityComponent.class, SettingsActivityComponent.class})
public abstract class AppScBuildersModule {

    @Binds
    @IntoMap
    @ActivityKey(SplashActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindSplashActivityInjectorFactory(SplashActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindMainActivityInjectorFactory(MainActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(SettingsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindSettingsActivityInjectorFactory(SettingsActivityComponent.Builder builder);

}