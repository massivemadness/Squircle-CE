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

package com.lightteam.modpeide.ui.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.appupdate.AppUpdateManager
import com.lightteam.modpeide.data.storage.cache.CacheHandler
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.domain.feature.parser.SourceParser
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import com.lightteam.modpeide.ui.main.viewmodel.MainViewModel
import com.lightteam.modpeide.ui.settings.viewmodel.SettingsViewModel

class ViewModelFactory(
    private val schedulersProvider: SchedulersProvider,
    private val appUpdateManager: AppUpdateManager,
    private val fileRepository: FileRepository,
    private val cacheHandler: CacheHandler,
    private val appDatabase: AppDatabase,
    private val preferenceHandler: PreferenceHandler,
    private val sourceParser: SourceParser
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass === MainViewModel::class.java -> {
                MainViewModel(
                    schedulersProvider,
                    appUpdateManager,
                    preferenceHandler
                ) as T
            }
            modelClass === EditorViewModel::class.java ->
                EditorViewModel(
                    schedulersProvider,
                    fileRepository,
                    cacheHandler,
                    appDatabase,
                    preferenceHandler,
                    sourceParser
                ) as T
            modelClass === ExplorerViewModel::class.java ->
                ExplorerViewModel(
                    schedulersProvider,
                    fileRepository,
                    preferenceHandler
                ) as T
            modelClass === SettingsViewModel::class.java ->
                SettingsViewModel(
                    schedulersProvider,
                    preferenceHandler
                ) as T
            else -> null as T
        }
    }
}