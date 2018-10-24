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
import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class App extends Application implements HasActivityInjector {

    private static final String TAG = App.class.getSimpleName();

    public static String PACKAGE_NAME;

    @Inject
    DispatchingAndroidInjector<Activity> mActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        PACKAGE_NAME = getPackageName();
        DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build()
                .injectApp(this);
        detectLeakedDatabases();
    }

    /**
     * Проверка утечек и их устранение.
     */
    protected void detectLeakedDatabases() {
        StrictMode.VmPolicy.Builder detectLeakedSqlLiteObjects = new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectActivityLeaks()
                .detectLeakedClosableObjects()
                .detectLeakedSqlLiteObjects()
                .detectLeakedRegistrationObjects();
        try {
            StrictMode.setVmPolicy(detectLeakedSqlLiteObjects.build());
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mActivityInjector;
    }
}
