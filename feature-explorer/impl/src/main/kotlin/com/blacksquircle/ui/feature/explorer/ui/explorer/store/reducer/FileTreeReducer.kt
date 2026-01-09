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

import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.indexOf
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.filesystem.base.exception.AuthRequiredException
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.redux.reducer.Reducer
import javax.inject.Inject
import kotlin.collections.minus
import kotlin.collections.plus
import com.blacksquircle.ui.ds.R as UiR

internal class FileTreeReducer @Inject constructor(
    private val stringProvider: StringProvider,
) : Reducer<ExplorerState, ExplorerAction, ExplorerEvent>() {

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

            is ExplorerAction.UiAction.OnOpenWithClicked -> {
                val fileNode = state.selection.firstOrNull()
                if (fileNode != null) {
                    event(ExplorerEvent.OpenFileWith(fileNode.file))
                }

                state {
                    copy(
                        taskType = TaskType.CREATE,
                        taskBuffer = emptyList(),
                        selection = emptyList(),
                    )
                }
            }

            is ExplorerAction.CommandAction.RenderNodeList -> {
                state {
                    copy(fileNodes = action.fileNodes)
                }
            }

            is ExplorerAction.CommandAction.LoadFilesError -> {
                if (action.fileNode.isRoot) {
                    state {
                        copy(errorState = errorState(action.error))
                    }
                } else {
                    event(ExplorerEvent.Toast(action.error.message.orEmpty()))
                }
            }

            else -> Unit
        }
    }

    private fun errorState(e: Throwable): ErrorState {
        return when (e) {
            is PermissionException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_access_denied_dialog_title),
                subtitle = stringProvider.getString(UiR.string.common_access_denied_dialog_message),
                action = ErrorAction.REQUEST_PERMISSIONS,
            )

            is AuthRequiredException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(R.string.explorer_error_view_title_auth),
                subtitle = when (e.authMethod) {
                    AuthMethod.PASSWORD ->
                        stringProvider.getString(R.string.explorer_error_view_message_password)

                    AuthMethod.KEY ->
                        stringProvider.getString(R.string.explorer_error_view_message_passphrase)
                },
                action = when (e.authMethod) {
                    AuthMethod.PASSWORD -> ErrorAction.ENTER_PASSWORD
                    AuthMethod.KEY -> ErrorAction.ENTER_PASSPHRASE
                }
            )

            is AuthenticationException -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_error_occurred),
                subtitle = when (e.authMethod) {
                    AuthMethod.PASSWORD ->
                        stringProvider.getString(R.string.explorer_error_view_message_password)

                    AuthMethod.KEY ->
                        stringProvider.getString(R.string.explorer_error_view_message_passphrase)
                },
                action = when (e.authMethod) {
                    AuthMethod.PASSWORD -> ErrorAction.ENTER_PASSWORD
                    AuthMethod.KEY -> ErrorAction.ENTER_PASSPHRASE
                }
            )

            else -> ErrorState(
                icon = UiR.drawable.ic_file_error,
                title = stringProvider.getString(UiR.string.common_error_occurred),
                subtitle = e.message.orEmpty(),
                action = ErrorAction.UNDEFINED,
            )
        }
    }
}