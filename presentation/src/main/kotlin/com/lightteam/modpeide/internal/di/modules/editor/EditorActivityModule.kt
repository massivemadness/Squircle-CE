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

package com.lightteam.modpeide.internal.di.modules.editor

import androidx.lifecycle.ViewModelProvider
import com.lightteam.modpeide.internal.di.scopes.PerActivity
import com.lightteam.modpeide.ui.editor.activities.EditorActivity
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import com.lightteam.modpeide.ui.common.viewmodel.ViewModelFactory
import com.lightteam.modpeide.ui.editor.activities.utils.ToolbarManager
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import dagger.Module
import dagger.Provides

@Module
class EditorActivityModule {

    @Provides
    @PerActivity
    fun provideEditorViewModel(activity: EditorActivity, factory: ViewModelFactory): EditorViewModel {
        return ViewModelProvider(activity, factory).get(EditorViewModel::class.java)
    }

    @Provides
    @PerActivity
    fun provideExplorerViewModel(activity: EditorActivity, factory: ViewModelFactory): ExplorerViewModel {
        return ViewModelProvider(activity, factory).get(ExplorerViewModel::class.java)
    }

    @Provides
    @PerActivity
    fun provideToolbarManager(activity: EditorActivity): ToolbarManager {
        return ToolbarManager(activity)
    }
}