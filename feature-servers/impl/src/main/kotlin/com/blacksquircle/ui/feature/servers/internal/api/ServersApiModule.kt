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
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.core.storage.database.AppDatabase
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.servers.api.interactor.ServerFilesystemFactory
import com.blacksquircle.ui.feature.servers.api.interactor.ServersInteractor
import com.blacksquircle.ui.feature.servers.data.factory.ServerFilesystemFactoryImpl
import com.blacksquircle.ui.feature.servers.data.interactor.ServersInteractorImpl
import com.blacksquircle.ui.feature.servers.data.repository.ServersRepositoryImpl
import com.blacksquircle.ui.feature.servers.domain.repository.ServersRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ServersApiModule {

    @Provides
    @Singleton
    fun provideServerFilesystemFactory(context: Context): ServerFilesystemFactory {
        return ServerFilesystemFactoryImpl(
            cacheDirectory = Directories.ftpDir(context),
        )
    }

    @Provides
    @Singleton
    fun provideServersRepository(
        serverFilesystemFactory: ServerFilesystemFactory,
        settingsManager: SettingsManager,
        dispatcherProvider: DispatcherProvider,
        appDatabase: AppDatabase,
    ): ServersRepository {
        return ServersRepositoryImpl(
            serverFilesystemFactory = serverFilesystemFactory,
            settingsManager = settingsManager,
            dispatcherProvider = dispatcherProvider,
            appDatabase = appDatabase
        )
    }

    @Provides
    @Singleton
    fun provideServersInteractor(serversRepository: ServersRepository): ServersInteractor {
        return ServersInteractorImpl(serversRepository)
    }
}