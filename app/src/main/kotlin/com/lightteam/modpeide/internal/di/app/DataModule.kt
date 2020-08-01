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
import android.os.Environment
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.localfilesystem.repository.LocalFilesystem
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.database.delegate.DatabaseDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return DatabaseDelegate.provideAppDatabase(context)
    }

    @Provides
    @Singleton
    @Named("Local")
    fun provideLocalFilesystem(): Filesystem {
        return LocalFilesystem(Environment.getExternalStorageDirectory())
    }

    @Provides
    @Singleton
    @Named("Cache")
    fun provideCacheFilesystem(@ApplicationContext context: Context): Filesystem {
        return LocalFilesystem(context.filesDir)
    }
}