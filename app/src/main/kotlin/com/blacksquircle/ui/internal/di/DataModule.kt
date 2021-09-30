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
import android.content.SharedPreferences
import android.os.Environment
import com.blacksquircle.ui.data.delegate.DatabaseDelegate
import com.blacksquircle.ui.data.delegate.FilesystemDelegate
import com.blacksquircle.ui.data.storage.database.AppDatabase
import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.filesystem.base.Filesystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    const val KEY_LOCAL_SAVEDDIR = "LOCAL_SAVEDDIR"

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
    @Named("Local")
    fun provideLocalFilesystem(sharedPreferences: SharedPreferences): Filesystem {
        var localSaveddir: String = sharedPreferences.getString(KEY_LOCAL_SAVEDDIR, "") ?: ""
        if (localSaveddir.equals(""))
            return FilesystemDelegate.provideFilesystem(Environment.getExternalStorageDirectory())
        else
            return FilesystemDelegate.provideFilesystem(File(localSaveddir))
    }

    @Provides
    @Singleton
    @Named("Cache")
    fun provideCacheFilesystem(@ApplicationContext context: Context): Filesystem {
        return FilesystemDelegate.provideFilesystem(context.filesDir)
    }
}