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
import com.lightteam.modpeide.data.delegate.DataLayerDelegate
import com.lightteam.modpeide.data.repository.LocalFileRepository
import com.lightteam.modpeide.data.storage.cache.CacheHandler
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.utils.commons.VersionChecker
import com.lightteam.modpeide.internal.di.scopes.PerApplication
import com.lightteam.modpeide.internal.providers.SchedulersProviderImpl
import com.lightteam.modpeide.ui.common.viewmodel.ViewModelFactory
import com.lightteam.modpeide.ui.main.adapters.BreadcrumbAdapter
import com.lightteam.modpeide.ui.main.adapters.DocumentAdapter
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    @PerApplication
    fun provideContext(application: BaseApplication): Context {
        return application
    }

    @Provides
    @PerApplication
    fun provideSchedulersProvider(): SchedulersProvider {
        return SchedulersProviderImpl()
    }

    @Provides
    @PerApplication
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @PerApplication
    fun provideRxSharedPreferences(sharedPreferences: SharedPreferences): RxSharedPreferences {
        return RxSharedPreferences.create(sharedPreferences)
    }

    @Provides
    @PerApplication
    fun providePreferenceHandler(rxSharedPreferences: RxSharedPreferences): PreferenceHandler {
        return PreferenceHandler(rxSharedPreferences)
    }

    @Provides
    @PerApplication
    fun provideVersionChecker(application: BaseApplication): VersionChecker {
        return VersionChecker(application.isUltimate)
    }

    @Provides
    @PerApplication
    fun provideAppDatabase(context: Context): AppDatabase {
        return DataLayerDelegate.provideAppDatabase(context)
    }

    @Provides
    @PerApplication
    fun provideCacheHandler(context: Context): CacheHandler {
        return CacheHandler(context)
    }

    @Provides
    @PerApplication
    fun provideFileRepository(database: AppDatabase): FileRepository {
        return LocalFileRepository(database)
    }

    @Provides
    @PerApplication
    fun provideViewModelFactory(fileRepository: FileRepository,
                                database: AppDatabase,
                                schedulersProvider: SchedulersProvider,
                                preferenceHandler: PreferenceHandler,
                                cacheHandler: CacheHandler,
                                breadcrumbAdapter: BreadcrumbAdapter,
                                documentAdapter: DocumentAdapter,
                                versionChecker: VersionChecker): ViewModelFactory {
        return ViewModelFactory(
            fileRepository,
            database,
            schedulersProvider,
            preferenceHandler,
            cacheHandler,
            breadcrumbAdapter,
            documentAdapter,
            versionChecker
        )
    }

    @Provides
    @PerApplication
    fun provideDocumentAdapter(): DocumentAdapter {
        return DocumentAdapter()
    }

    @Provides
    @PerApplication
    fun provideBreadcrumbAdapter(): BreadcrumbAdapter {
        return BreadcrumbAdapter()
    }
}