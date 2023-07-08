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

package com.blacksquircle.ui.feature.editor.internal

import android.content.Context
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.database.AppDatabase
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.editor.data.repository.DocumentRepositoryImpl
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.explorer.domain.factory.FilesystemFactory
import com.blacksquircle.ui.filesystem.base.Filesystem
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
    fun provideDocumentRepository(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        appDatabase: AppDatabase,
        filesystemFactory: FilesystemFactory,
        @Named("Cache") cacheFilesystem: Filesystem,
    ): DocumentRepository {
        return DocumentRepositoryImpl(
            dispatcherProvider = dispatcherProvider,
            settingsManager = settingsManager,
            appDatabase = appDatabase,
            filesystemFactory = filesystemFactory,
            cacheFilesystem = cacheFilesystem,
            context = context,
        )
    }
}