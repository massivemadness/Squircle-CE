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

package com.blacksquircle.ui.feature.terminal.domain.manager

import com.blacksquircle.ui.feature.terminal.api.model.ShellArgs
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.blacksquircle.ui.feature.terminal.domain.runtime.TerminalRuntime

internal interface SessionManager {

    fun sessions(): List<SessionModel>

    fun createSession(runtime: TerminalRuntime, args: ShellArgs? = null): String
    fun closeSession(sessionId: String)
}