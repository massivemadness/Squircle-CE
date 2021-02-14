/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.internal.di.editor

import android.content.Context
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.repository.documents.CacheRepository
import com.brackeys.ui.data.repository.documents.LocalRepository
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.filesystem.base.Filesystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object EditorModule {

    @Provides
    @ViewModelScoped
    fun provideCacheRepository(
        @ApplicationContext context: Context,
        appDatabase: AppDatabase,
        @Named("Cache")
        filesystem: Filesystem
    ): CacheRepository {
        return CacheRepository(context.filesDir, appDatabase, filesystem)
    }

    @Provides
    @ViewModelScoped
    fun provideFileRepository(
        settingsManager: SettingsManager,
        appDatabase: AppDatabase,
        @Named("Local")
        filesystem: Filesystem
    ): LocalRepository {
        return LocalRepository(settingsManager, appDatabase, filesystem)
    }
}