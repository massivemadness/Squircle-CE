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

package com.blacksquircle.ui.feature.terminal.data.runtime

import android.content.Context
import android.os.Environment
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.feature.terminal.domain.runtime.TerminalRuntime
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

internal class AndroidRuntime(private val context: Context) : TerminalRuntime {

    override fun create(client: TerminalSessionClient): TerminalSession {
        return TerminalSession(
            /* shellPath = */ ANDROID_SHELL,
            /* cwd = */ Directories.terminalDir(context).absolutePath,
            /* args = */ emptyArray(),
            /* env = */ arrayOf(
                "HOME=${Directories.terminalDir(context).absolutePath}",
                "EXTERNAL_STORAGE=${Environment.getExternalStorageDirectory().absolutePath}",
            ),
            /* transcriptRows = */ TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
            /* client = */ client,
        )
    }

    companion object {
        private const val ANDROID_SHELL = "/system/bin/sh"
    }
}