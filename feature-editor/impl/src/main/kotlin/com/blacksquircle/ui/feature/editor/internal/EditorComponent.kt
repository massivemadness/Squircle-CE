/*
 * Copyright Squircle CE contributors.
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
import com.blacksquircle.ui.core.internal.CoreApi
import com.blacksquircle.ui.core.internal.provideCoreApi
import com.blacksquircle.ui.feature.editor.api.internal.EditorApi
import com.blacksquircle.ui.feature.editor.api.internal.provideEditorApi
import com.blacksquircle.ui.feature.editor.ui.closefile.CloseFileViewModel
import com.blacksquircle.ui.feature.editor.ui.confirmexit.ConfirmExitViewModel
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.forcesyntax.ForceSyntaxViewModel
import com.blacksquircle.ui.feature.editor.ui.gotoline.GoToLineViewModel
import com.blacksquircle.ui.feature.editor.ui.insertcolor.InsertColorViewModel
import com.blacksquircle.ui.feature.explorer.api.internal.ExplorerApi
import com.blacksquircle.ui.feature.explorer.api.internal.provideExplorerApi
import com.blacksquircle.ui.feature.fonts.api.internal.FontsApi
import com.blacksquircle.ui.feature.fonts.api.internal.provideFontsApi
import com.blacksquircle.ui.feature.git.api.internal.GitApi
import com.blacksquircle.ui.feature.git.api.internal.provideGitApi
import com.blacksquircle.ui.feature.shortcuts.api.internal.ShortcutsApi
import com.blacksquircle.ui.feature.shortcuts.api.internal.provideShortcutsApi
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApi
import com.blacksquircle.ui.feature.terminal.api.internal.provideTerminalApi
import com.blacksquircle.ui.navigation.api.internal.NavigationApi
import com.blacksquircle.ui.navigation.api.internal.provideNavigationApi
import dagger.Component

@EditorScope
@Component(
    modules = [
        EditorModule::class,
    ],
    dependencies = [
        CoreApi::class,
        NavigationApi::class,
        EditorApi::class,
        ExplorerApi::class,
        FontsApi::class,
        GitApi::class,
        ShortcutsApi::class,
        TerminalApi::class,
    ]
)
internal interface EditorComponent {

    fun inject(factory: EditorViewModel.Factory)
    fun inject(factory: CloseFileViewModel.Factory)
    fun inject(factory: ForceSyntaxViewModel.ParameterizedFactory)
    fun inject(factory: GoToLineViewModel.Factory)
    fun inject(factory: InsertColorViewModel.Factory)
    fun inject(factory: ConfirmExitViewModel.Factory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApi: CoreApi,
            navigationApi: NavigationApi,
            editorApi: EditorApi,
            explorerApi: ExplorerApi,
            fontsApi: FontsApi,
            gitApi: GitApi,
            shortcutsApi: ShortcutsApi,
            terminalApi: TerminalApi,
        ): EditorComponent
    }

    companion object {

        private var component: EditorComponent? = null

        fun buildOrGet(context: Context): EditorComponent {
            return component ?: DaggerEditorComponent.factory().create(
                coreApi = context.provideCoreApi(),
                navigationApi = context.provideNavigationApi(),
                editorApi = context.provideEditorApi(),
                explorerApi = context.provideExplorerApi(),
                fontsApi = context.provideFontsApi(),
                gitApi = context.provideGitApi(),
                shortcutsApi = context.provideShortcutsApi(),
                terminalApi = context.provideTerminalApi(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}