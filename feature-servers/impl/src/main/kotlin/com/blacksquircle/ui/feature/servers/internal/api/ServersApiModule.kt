/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.servers.internal.api

import android.content.Context
import com.blacksquircle.ui.core.database.AppDatabase
import com.blacksquircle.ui.core.database.dao.path.PathDao
import com.blacksquircle.ui.core.database.dao.server.ServerDao
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.servers.api.factory.ServerFactory
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.feature.servers.data.factory.ServerFactoryImpl
import com.blacksquircle.ui.feature.servers.data.interactor.ServerInteractorImpl
import com.blacksquircle.ui.feature.servers.data.repository.ServerRepositoryImpl
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ServersApiModule {

    @Provides
    @Singleton
    fun provideServerFilesystemFactory(context: Context): ServerFactory {
        return ServerFactoryImpl(
            cacheDir = Directories.cacheDir(context),
            keysDir = Directories.keysDir(context),
        )
    }

    @Provides
    @Singleton
    fun provideServerRepository(
        serverFactory: ServerFactory,
        settingsManager: SettingsManager,
        dispatcherProvider: DispatcherProvider,
        serverDao: ServerDao,
        pathDao: PathDao,
        context: Context,
    ): ServerRepository {
        return ServerRepositoryImpl(
            serverFactory = serverFactory,
            settingsManager = settingsManager,
            dispatcherProvider = dispatcherProvider,
            serverDao = serverDao,
            pathDao = pathDao,
            context = context,
        )
    }

    @Provides
    @Singleton
    fun provideServerInteractor(serverRepository: ServerRepository): ServerInteractor {
        return ServerInteractorImpl(serverRepository)
    }

    @Provides
    fun provideServerDao(appDatabase: AppDatabase): ServerDao {
        return appDatabase.serverDao()
    }

    @Provides
    fun providePathDao(appDatabase: AppDatabase): PathDao {
        return appDatabase.pathDao()
    }
}