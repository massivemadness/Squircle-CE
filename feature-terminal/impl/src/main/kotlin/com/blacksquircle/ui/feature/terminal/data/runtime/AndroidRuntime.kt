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
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.feature.terminal.domain.runtime.TerminalRuntime
import com.termux.shared.shell.command.environment.ShellEnvironmentUtils.*
import com.termux.shared.shell.command.environment.UnixShellEnvironment.*
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

internal class AndroidRuntime(private val context: Context) : TerminalRuntime {

    override val name = "Android"

    override fun create(client: TerminalSessionClient): TerminalSession {
        val home = Directories.terminalDir(context)
        val environment = HashMap<String, String>()

        environment[ENV_HOME] = home.absolutePath
        environment[ENV_LANG] = "en_US.UTF-8"
        environment[ENV_PATH] = System.getenv(ENV_PATH).orEmpty()
        environment[ENV_TMPDIR] = "/data/local/tmp"

        environment[ENV_COLORTERM] = "truecolor"
        environment[ENV_TERM] = "xterm-256color"

        putToEnvIfInSystemEnv(environment, "ANDROID_ASSETS")
        putToEnvIfInSystemEnv(environment, "ANDROID_DATA")
        putToEnvIfInSystemEnv(environment, "ANDROID_ROOT")
        putToEnvIfInSystemEnv(environment, "ANDROID_STORAGE")

        putToEnvIfInSystemEnv(environment, "EXTERNAL_STORAGE")
        putToEnvIfInSystemEnv(environment, "ASEC_MOUNTPOINT")
        putToEnvIfInSystemEnv(environment, "LOOP_MOUNTPOINT")

        putToEnvIfInSystemEnv(environment, "ANDROID_RUNTIME_ROOT")
        putToEnvIfInSystemEnv(environment, "ANDROID_ART_ROOT")
        putToEnvIfInSystemEnv(environment, "ANDROID_I18N_ROOT")
        putToEnvIfInSystemEnv(environment, "ANDROID_TZDATA_ROOT")

        putToEnvIfInSystemEnv(environment, "BOOTCLASSPATH")
        putToEnvIfInSystemEnv(environment, "DEX2OATBOOTCLASSPATH")
        putToEnvIfInSystemEnv(environment, "SYSTEMSERVERCLASSPATH")

        return TerminalSession(
            /* shellPath = */ ANDROID_SHELL,
            /* cwd = */ home.absolutePath,
            /* args = */ emptyArray(),
            /* env = */ convertEnvironmentToEnviron(environment).toTypedArray(),
            /* transcriptRows = */ TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
            /* client = */ client,
        )
    }

    companion object {
        private const val ANDROID_SHELL = "/system/bin/sh"
    }
}