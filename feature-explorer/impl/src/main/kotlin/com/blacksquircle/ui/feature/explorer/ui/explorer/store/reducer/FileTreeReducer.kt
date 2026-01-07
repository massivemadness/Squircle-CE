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

import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.redux.reducer.Reducer
import javax.inject.Inject
import kotlin.collections.minus
import kotlin.collections.plus

internal class FileTreeReducer @Inject constructor() : Reducer<ExplorerState, ExplorerAction, ExplorerEvent>() {

    override fun reduce(action: ExplorerAction) {
        when (action) {
            is ExplorerAction.UiAction.OnFileClicked -> {
                when {
                    state.selection.isNotEmpty() -> {
                        reduce(ExplorerAction.UiAction.OnFileSelected(action.fileNode))
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
                            action(ExplorerAction.UiAction.OnOpenFileClicked(action.fileNode))
                            event(ExplorerEvent.CloseDrawer)
                        }

                        else -> {
                            event(ExplorerEvent.OpenFileWith(action.fileNode.file))
                        }
                    }
                }
            }

            is ExplorerAction.UiAction.OnFileSelected -> {
                val fileNode = action.fileNode
                val anySelected = state.selection.isNotEmpty()
                val rootSelected = state.selection.any(FileNode::isRoot)

                if (fileNode.isRoot && anySelected && !rootSelected) return
                if (!fileNode.isRoot && rootSelected) return

                val index = state.selection.indexOf {
                    it.key == fileNode.key
                }

                state {
                    copy(
                        selection = if (index == -1) {
                            selection + fileNode
                        } else {
                            selection - fileNode
                        }
                    )
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