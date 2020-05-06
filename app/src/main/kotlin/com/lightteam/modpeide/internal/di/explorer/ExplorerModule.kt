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

package com.lightteam.modpeide.internal.di.explorer

import androidx.lifecycle.ViewModelProvider
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.explorer.fragments.ExplorerFragment
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ExplorerModule {

    @Provides
    @ExplorerScope
    fun provideExplorerViewModelFactory(
        schedulersProvider: SchedulersProvider,
        preferenceHandler: PreferenceHandler,
        @Named("Local")
        filesystem: Filesystem
    ): ExplorerViewModel.Factory {
        return ExplorerViewModel.Factory(
            schedulersProvider,
            preferenceHandler,
            filesystem
        )
    }

    @Provides
    @ExplorerScope
    fun provideExplorerViewModel(
        fragment: ExplorerFragment,
        factory: ExplorerViewModel.Factory
    ): ExplorerViewModel {
        return ViewModelProvider(fragment, factory).get(ExplorerViewModel::class.java)
    }
}