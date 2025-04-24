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
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiDepsProvider
import com.blacksquircle.ui.feature.editor.api.internal.EditorApiProvider
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiDepsProvider
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApiProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiDepsProvider
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApiProvider
import com.blacksquircle.ui.feature.git.api.internal.GitApiDepsProvider
import com.blacksquircle.ui.feature.git.api.internal.GitApiProvider
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApiDepsProvider
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApiProvider
import dagger.Component

@EditorScope
@Component(
    modules = [
        EditorModule::class,
    ],
    dependencies = [
        CoreApiDepsProvider::class,
        EditorApiDepsProvider::class,
        ExplorerApiDepsProvider::class,
        FontsApiDepsProvider::class,
        GitApiDepsProvider::class,
        ShortcutsApiDepsProvider::class,
    ]
)
internal interface EditorComponent {

    fun inject(factory: EditorViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApiDepsProvider: CoreApiDepsProvider,
            editorApiDepsProvider: EditorApiDepsProvider,
            explorerApiDepsProvider: ExplorerApiDepsProvider,
            fontsApiDepsProvider: FontsApiDepsProvider,
            gitApiDepsProvider: GitApiDepsProvider,
            shortcutsApiDepsProvider: ShortcutsApiDepsProvider,
        ): EditorComponent
    }

    companion object {

        private var component: EditorComponent? = null

        fun buildOrGet(context: Context): EditorComponent {
            return component ?: DaggerEditorComponent.factory().create(
                coreApiDepsProvider = (context.applicationContext as CoreApiProvider)
                    .provideCoreApiDepsProvider(),
                editorApiDepsProvider = (context.applicationContext as EditorApiProvider)
                    .provideEditorApiDepsProvider(),
                explorerApiDepsProvider = (context.applicationContext as ExplorerApiProvider)
                    .provideExplorerApiDepsProvider(),
                fontsApiDepsProvider = (context.applicationContext as FontsApiProvider)
                    .provideFontsApiDepsProvider(),
                gitApiDepsProvider = (context.applicationContext as GitApiProvider)
                    .provideGitApiDepsProvider(),
                shortcutsApiDepsProvider = (context.applicationContext as ShortcutsApiProvider)
                    .provideShortcutsApiDepsProvider(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}