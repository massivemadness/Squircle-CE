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

package com.blacksquircle.ui.feature.explorer.internal

import android.content.Context
import com.blacksquircle.ui.core.database.AppDatabase
import com.blacksquircle.ui.core.database.dao.workspace.WorkspaceDao
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.data.node.async.AsyncNodeBuilder
import com.blacksquircle.ui.feature.explorer.data.repository.ExplorerRepositoryImpl
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.scottyab.rootbeer.RootBeer
import dagger.Module
import dagger.Provides

@Module
internal object ExplorerModule {

    @Provides
    @ExplorerScope
    fun provideAsyncNodeBuilder(dispatcherProvider: DispatcherProvider): AsyncNodeBuilder {
        return AsyncNodeBuilder(dispatcherProvider)
    }

    @Provides
    @ExplorerScope
    fun provideTaskManager(dispatcherProvider: DispatcherProvider): TaskManager {
        return TaskManager(dispatcherProvider)
    }

    @Provides
    @ExplorerScope
    fun provideExplorerRepository(
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        taskManager: TaskManager,
        gitInteractor: GitInteractor,
        serverInteractor: ServerInteractor,
        filesystemFactory: FilesystemFactory,
        workspaceDao: WorkspaceDao,
        rootBeer: RootBeer,
        context: Context,
    ): ExplorerRepository {
        return ExplorerRepositoryImpl(
            dispatcherProvider = dispatcherProvider,
            settingsManager = settingsManager,
            taskManager = taskManager,
            gitInteractor = gitInteractor,
            serverInteractor = serverInteractor,
            filesystemFactory = filesystemFactory,
            workspaceDao = workspaceDao,
            rootBeer = rootBeer,
            context = context,
        )
    }

    @Provides
    fun provideWorkspaceDao(appDatabase: AppDatabase): WorkspaceDao {
        return appDatabase.workspaceDao()
    }

    @Provides
    fun provideRootBeer(context: Context): RootBeer {
        return RootBeer(context)
    }
}