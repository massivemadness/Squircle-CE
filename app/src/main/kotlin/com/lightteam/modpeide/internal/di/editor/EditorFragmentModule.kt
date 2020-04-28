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

package com.lightteam.modpeide.internal.di.editor

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.modpeide.data.repository.CacheRepository
import com.lightteam.modpeide.data.repository.FileRepository
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.editor.adapters.DocumentAdapter
import com.lightteam.modpeide.ui.editor.fragments.EditorFragment
import com.lightteam.modpeide.ui.editor.utils.ToolbarManager
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class EditorFragmentModule {

    @Provides
    @EditorScope
    fun provideEditorViewModelFactory(
        schedulersProvider: SchedulersProvider,
        preferenceHandler: PreferenceHandler,
        appDatabase: AppDatabase,
        fileRepository: FileRepository,
        cacheRepository: CacheRepository
    ): EditorViewModel.Factory {
        return EditorViewModel.Factory(
            schedulersProvider,
            preferenceHandler,
            appDatabase,
            fileRepository,
            cacheRepository
        )
    }

    @Provides
    @EditorScope
    fun provideEditorViewModel(
        fragment: EditorFragment,
        factory: EditorViewModel.Factory
    ): EditorViewModel {
        return ViewModelProvider(fragment, factory).get(EditorViewModel::class.java)
    }

    @Provides
    @EditorScope
    fun provideCacheRepository(
        context: Context,
        appDatabase: AppDatabase,
        @Named("Cache")
        filesystem: Filesystem
    ): CacheRepository {
        return CacheRepository(context.filesDir, appDatabase, filesystem)
    }

    @Provides
    @EditorScope
    fun provideFileRepository(
        appDatabase: AppDatabase,
        @Named("Local")
        filesystem: Filesystem
    ): FileRepository {
        return FileRepository(appDatabase, filesystem)
    }

    @Provides
    @EditorScope
    fun provideToolbarManager(fragment: EditorFragment): ToolbarManager {
        return ToolbarManager(fragment)
    }

    @Provides
    @EditorScope
    fun provideDocumentAdapter(fragment: EditorFragment): DocumentAdapter {
        return DocumentAdapter(fragment)
    }
}