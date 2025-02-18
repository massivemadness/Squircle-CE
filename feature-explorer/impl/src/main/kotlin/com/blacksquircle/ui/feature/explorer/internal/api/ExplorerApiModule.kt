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

package com.blacksquircle.ui.feature.explorer.internal.api

import android.content.Context
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.data.factory.FilesystemFactoryImpl
import com.blacksquircle.ui.feature.servers.api.interactor.ServerFilesystemFactory
import com.blacksquircle.ui.feature.servers.api.interactor.ServersInteractor
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ExplorerApiModule {

    @Provides
    @Singleton
    fun provideFilesystemFactory(
        serverFilesystemFactory: ServerFilesystemFactory,
        serversInteractor: ServersInteractor,
    ): FilesystemFactory {
        return FilesystemFactoryImpl(
            serverFilesystemFactory = serverFilesystemFactory,
            serversInteractor = serversInteractor,
        )
    }

    @Provides
    @Singleton
    fun provideCacheFilesystem(context: Context): Filesystem {
        return LocalFilesystem(Directories.filesDir(context))
    }
}