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

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.localfilesystem.repository.LocalFilesystem
import com.lightteam.modpeide.BaseApplication
import com.lightteam.modpeide.data.delegate.DataLayerDelegate
import com.lightteam.modpeide.data.repository.CacheRepository
import com.lightteam.modpeide.data.repository.FileRepository
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.internal.providers.rx.SchedulersProviderImpl
import com.lightteam.modpeide.ui.base.viewmodel.ViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: BaseApplication): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideSchedulersProvider(): SchedulersProvider {
        return SchedulersProviderImpl()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRxSharedPreferences(sharedPreferences: SharedPreferences): RxSharedPreferences {
        return RxSharedPreferences.create(sharedPreferences)
    }

    @Provides
    @Singleton
    fun providePreferenceHandler(rxSharedPreferences: RxSharedPreferences): PreferenceHandler {
        return PreferenceHandler(rxSharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAppUpdateManager(context: Context): AppUpdateManager {
        return AppUpdateManagerFactory.create(context)
    }

    @Provides
    @Singleton
    @Named("Local")
    fun provideLocalFilesystem(): Filesystem {
        return LocalFilesystem(Environment.getExternalStorageDirectory().absoluteFile)
    }

    @Provides
    @Singleton
    @Named("Cache")
    fun provideCacheFilesystem(context: Context): Filesystem {
        return LocalFilesystem(context.filesDir)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return DataLayerDelegate.provideAppDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCacheRepository(
        context: Context,
        @Named("Cache")
        filesystem: Filesystem,
        appDatabase: AppDatabase
    ): CacheRepository {
        return CacheRepository(context.filesDir, filesystem, appDatabase)
    }

    @Provides
    @Singleton
    fun provideFileRepository(
        @Named("Local")
        filesystem: Filesystem,
        appDatabase: AppDatabase
    ): FileRepository {
        return FileRepository(filesystem, appDatabase)
    }

    @Provides
    @Singleton
    fun provideViewModelFactory(
        schedulersProvider: SchedulersProvider,
        appUpdateManager: AppUpdateManager,
        @Named("Local")
        filesystem: Filesystem,
        fileRepository: FileRepository,
        cacheRepository: CacheRepository,
        appDatabase: AppDatabase,
        preferenceHandler: PreferenceHandler
    ): ViewModelFactory {
        return ViewModelFactory(
            schedulersProvider,
            appUpdateManager,
            filesystem,
            fileRepository,
            cacheRepository,
            appDatabase,
            preferenceHandler
        )
    }
}