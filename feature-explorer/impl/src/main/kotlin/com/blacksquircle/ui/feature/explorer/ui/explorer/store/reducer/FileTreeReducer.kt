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

package com.blacksquircle.ui.feature.explorer.ui.explorer.store.reducer

import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.redux.reducer.Reducer
import javax.inject.Inject

internal class FileTreeReducer @Inject constructor() : Reducer<ExplorerState, ExplorerAction, ExplorerEvent>() {

    override fun reduce(action: ExplorerAction) {
        when (action) {
            is ExplorerAction.UiAction.OnFileClicked -> {
                when {
                    state.selection.isNotEmpty() -> {
                        action(ExplorerAction.UiAction.OnFileSelected(action.fileNode))
                    }
                    action.fileNode.isDirectory -> {
                        if (action.fileNode.isExpanded) {
                            action(ExplorerAction.UiAction.OnCollapseClicked(action.fileNode))
                        } else {
                            action(ExplorerAction.UiAction.OnExpandClicked(action.fileNode))
                        }
                    }
                    else -> when (action.fileNode.file.type) {
                        FileType.ARCHIVE -> {
                            action(ExplorerAction.UiAction.OnExtractFileClicked(action.fileNode))
                        }

                        FileType.DEFAULT -> {
                            // editorInteractor open file
                            event(ExplorerEvent.CloseDrawer)
                        }

                        else -> {
                            // open with
                        }
                    }
                }
            }

            is ExplorerAction.CommandAction.RenderNodeList -> {
                state {
                    copy(fileNodes = action.fileNodes)
                }
            }

            is ExplorerAction.CommandAction.LoadFilesError -> {
                if (!action.fileNode.isRoot) {
                    event(ExplorerEvent.Toast(action.error.message.orEmpty()))
                }
            }

            else -> Unit
        }
    }
}