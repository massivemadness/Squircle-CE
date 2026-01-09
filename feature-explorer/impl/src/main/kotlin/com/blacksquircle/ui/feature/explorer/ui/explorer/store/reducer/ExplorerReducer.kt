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

import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerState
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.redux.reducer.Reducer
import javax.inject.Inject

internal class ExplorerReducer @Inject constructor() : Reducer<ExplorerState, ExplorerAction, ExplorerEvent>() {

    override fun reduce(action: ExplorerAction) {
        when (action) {
            is ExplorerAction.UiAction.OnBackClicked -> {
                if (state.selection.isNotEmpty()) {
                    state {
                        copy(selection = emptyList())
                    }
                } else {
                    event(ExplorerEvent.CloseDrawer)
                }
            }

            is ExplorerAction.UiAction.OnErrorActionClicked -> {
                when (state.errorState?.action) {
                    ErrorAction.REQUEST_PERMISSIONS -> {
                        event(ExplorerEvent.RequestPermission)
                    }

                    ErrorAction.ENTER_PASSWORD -> {
                        action(ExplorerAction.UiAction.OnServerAuthClicked(AuthMethod.PASSWORD))
                    }

                    ErrorAction.ENTER_PASSPHRASE -> {
                        action(ExplorerAction.UiAction.OnServerAuthClicked(AuthMethod.KEY))
                    }

                    else -> Unit
                }
            }

            is ExplorerAction.UiAction.OnRefreshClicked -> {
                val fileNode = state.selection.firstOrNull() ?: return
                state {
                    copy(selection = emptyList())
                }
                action(ExplorerAction.CommandAction.LoadFiles(fileNode))
            }

            else -> Unit
        }
    }
}