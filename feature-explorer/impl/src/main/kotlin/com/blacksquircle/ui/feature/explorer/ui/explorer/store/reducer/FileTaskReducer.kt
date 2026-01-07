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

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.filesystem.base.exception.EncryptedArchiveException
import com.blacksquircle.ui.filesystem.base.exception.FileAlreadyExistsException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.exception.InvalidArchiveException
import com.blacksquircle.ui.filesystem.base.exception.SplitArchiveException
import com.blacksquircle.ui.filesystem.base.exception.UnsupportedArchiveException
import com.blacksquircle.ui.redux.reducer.Reducer
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

internal class FileTaskReducer @Inject constructor(
    private val stringProvider: StringProvider,
) : Reducer<ExplorerState, ExplorerAction, ExplorerEvent>() {

    override fun reduce(action: ExplorerAction) {
        when (action) {
            is ExplorerAction.CommandAction.FillBuffer -> {
                state {
                    copy(
                        taskType = action.taskType,
                        taskBuffer = action.taskBuffer,
                        selection = emptyList()
                    )
                }
            }

            is ExplorerAction.CommandAction.ResetBuffer -> {
                state {
                    copy(
                        taskType = TaskType.CREATE,
                        taskBuffer = emptyList(),
                        selection = emptyList(),
                    )
                }
            }

            is ExplorerAction.UiAction.OnCutClicked,
            is ExplorerAction.UiAction.OnCopyClicked -> {
                val message = stringProvider.getString(R.string.explorer_toast_select_folder_to_paste)
                event(ExplorerEvent.Toast(message))
            }

            is ExplorerAction.UiAction.OnPasteClicked -> {
                when (state.taskType) {
                    TaskType.MOVE -> {
                        val parent = state.selection.firstOrNull() ?: return
                        action(ExplorerAction.UiAction.OnMoveFileClicked(parent))
                    }

                    TaskType.COPY -> {
                        val parent = state.selection.firstOrNull() ?: return
                        action(ExplorerAction.UiAction.OnCopyFileClicked(parent))
                    }

                    else -> Unit
                }
            }

            is ExplorerAction.CommandAction.TaskComplete -> {
                val message = stringProvider.getString(R.string.explorer_toast_done)
                event(ExplorerEvent.Toast(message))
            }

            is ExplorerAction.CommandAction.TaskFailed -> {
                when (action.error) {
                    is FileNotFoundException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_file_not_found)
                        event(ExplorerEvent.Toast(message))
                    }

                    is FileAlreadyExistsException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_file_already_exists)
                        event(ExplorerEvent.Toast(message))
                    }

                    is UnsupportedArchiveException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_unsupported_archive)
                        event(ExplorerEvent.Toast(message))
                    }

                    is EncryptedArchiveException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_encrypted_archive)
                        event(ExplorerEvent.Toast(message))
                    }

                    is SplitArchiveException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_split_archive)
                        event(ExplorerEvent.Toast(message))
                    }

                    is InvalidArchiveException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_invalid_archive)
                        event(ExplorerEvent.Toast(message))
                    }

                    is UnsupportedOperationException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_operation_not_supported)
                        event(ExplorerEvent.Toast(message))
                    }

                    is CancellationException -> {
                        val message = stringProvider.getString(R.string.explorer_toast_operation_cancelled)
                        event(ExplorerEvent.Toast(message))
                    }

                    else -> {
                        event(ExplorerEvent.Toast(action.error.message.orEmpty()))
                    }
                }
            }

            else -> Unit
        }
    }
}