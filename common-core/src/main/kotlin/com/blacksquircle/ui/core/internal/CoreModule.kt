/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.core.internal

import android.content.Context
import android.content.SharedPreferences
import com.blacksquircle.ui.core.data.delegate.DatabaseDelegate
import com.blacksquircle.ui.core.data.factory.FilesystemFactory
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideSettingsManager(sharedPreferences: SharedPreferences): SettingsManager {
        return SettingsManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return DatabaseDelegate.provideAppDatabase(context)
    }

    @Provides
    @Singleton
    fun provideFilesystemFactory(
        @ApplicationContext context: Context,
        appDatabase: AppDatabase,
    ): FilesystemFactory {
        return FilesystemFactory(appDatabase, context.cacheDir)
    }

    @Provides
    @Singleton
    @Named("Cache")
    fun provideCacheFilesystem(@ApplicationContext context: Context): Filesystem {
        return LocalFilesystem(context.filesDir)
    }
}