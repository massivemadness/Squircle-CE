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

package com.blacksquircle.ui.feature.terminal.ui.terminal

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel

@Immutable
internal data class TerminalViewState(
    val isInstalling: Boolean = false,
    val installProgress: Float = 0f,
    val installError: String? = null,
    val sessions: List<SessionModel> = emptyList(),
    val selectedSession: String? = null,
    val cursorBlinking: Boolean = false,
    val keepScreenOn: Boolean = false,
) : ViewState {

    val currentSession: SessionModel?
        get() = sessions.find { it.id == selectedSession }
}