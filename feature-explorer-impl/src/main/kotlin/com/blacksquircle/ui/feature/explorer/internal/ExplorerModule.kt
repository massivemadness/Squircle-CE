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

package com.blacksquircle.ui.feature.explorer.internal

import android.content.Context
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.explorer.data.factory.FilesystemFactoryImpl
import com.blacksquircle.ui.feature.explorer.data.repository.ExplorerRepositoryImpl
import com.blacksquircle.ui.feature.explorer.domain.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.servers.domain.repository.ServersRepository
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
object ExplorerModule {

    @Provides
    @Singleton
    fun provideExplorerRepository(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        filesystemFactory: FilesystemFactory,
    ): ExplorerRepository {
        return ExplorerRepositoryImpl(
            dispatcherProvider = dispatcherProvider,
            settingsManager = settingsManager,
            filesystemFactory = filesystemFactory,
            context = context,
        )
    }

    @Provides
    @Singleton
    fun provideFilesystemFactory(
        @ApplicationContext context: Context,
        serversRepository: ServersRepository,
    ): FilesystemFactory {
        return FilesystemFactoryImpl(serversRepository, Directories.ftpDir(context))
    }

    @Provides
    @Singleton
    @Named("Cache")
    fun provideCacheFilesystem(@ApplicationContext context: Context): Filesystem {
        return LocalFilesystem(Directories.filesDir(context))
    }
}