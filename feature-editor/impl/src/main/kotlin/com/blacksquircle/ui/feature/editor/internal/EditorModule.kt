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

package com.blacksquircle.ui.feature.editor.internal

import android.content.Context
import com.blacksquircle.ui.core.database.AppDatabase
import com.blacksquircle.ui.core.database.dao.document.DocumentDao
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.data.interactor.LanguageInteractorImpl
import com.blacksquircle.ui.feature.editor.data.manager.CacheManager
import com.blacksquircle.ui.feature.editor.data.repository.DocumentRepositoryImpl
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import dagger.Module
import dagger.Provides

@Module
internal object EditorModule {

    @Provides
    @EditorScope
    fun provideLanguageInteractor(
        dispatcherProvider: DispatcherProvider,
        context: Context,
    ): LanguageInteractor {
        return LanguageInteractorImpl(
            dispatcherProvider = dispatcherProvider,
            context = context,
        )
    }

    @Provides
    @EditorScope
    fun provideDocumentRepository(
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        cacheManager: CacheManager,
        documentDao: DocumentDao,
        filesystemFactory: FilesystemFactory,
        context: Context,
    ): DocumentRepository {
        return DocumentRepositoryImpl(
            dispatcherProvider = dispatcherProvider,
            settingsManager = settingsManager,
            cacheManager = cacheManager,
            documentDao = documentDao,
            filesystemFactory = filesystemFactory,
            context = context,
        )
    }

    @Provides
    @EditorScope
    fun provideCacheManager(context: Context): CacheManager {
        return CacheManager(Directories.filesDir(context))
    }

    @Provides
    fun provideDocumentDao(appDatabase: AppDatabase): DocumentDao {
        return appDatabase.documentDao()
    }
}