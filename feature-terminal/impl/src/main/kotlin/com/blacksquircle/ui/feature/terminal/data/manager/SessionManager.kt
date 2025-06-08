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

package com.blacksquircle.ui.feature.terminal.data.manager

import com.blacksquircle.ui.feature.terminal.data.factory.ShellFactory
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.blacksquircle.ui.feature.terminal.ui.model.TerminalCommand
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalSessionClientImpl
import com.termux.shared.shell.command.environment.ShellEnvironmentUtils.convertEnvironmentToEnviron
import com.termux.shared.shell.command.environment.ShellEnvironmentUtils.putToEnvIfInSystemEnv
import com.termux.shared.shell.command.environment.UnixShellEnvironment.ENV_COLORTERM
import com.termux.shared.shell.command.environment.UnixShellEnvironment.ENV_HOME
import com.termux.shared.shell.command.environment.UnixShellEnvironment.ENV_LANG
import com.termux.shared.shell.command.environment.UnixShellEnvironment.ENV_PATH
import com.termux.shared.shell.command.environment.UnixShellEnvironment.ENV_TERM
import com.termux.shared.shell.command.environment.UnixShellEnvironment.ENV_TMPDIR
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

internal class SessionManager(
    private val shellFactory: ShellFactory,
) {

    private val sessions = ConcurrentHashMap<String, SessionModel>()
    private val counter = AtomicInteger(0)

    fun sessions(): List<SessionModel> {
        return sessions.values.sortedBy(SessionModel::ordinal)
    }

    fun createSession(): String {
        val sessionId = UUID.randomUUID().toString()
        val commands = MutableSharedFlow<TerminalCommand>(extraBufferCapacity = 64)
        val client = TerminalSessionClientImpl(
            onUpdate = { commands.tryEmit(TerminalCommand.Update) },
            onCopy = { text -> commands.tryEmit(TerminalCommand.Copy(text)) },
            onPaste = { commands.tryEmit(TerminalCommand.Paste) }
        )

        val shell = shellFactory.create()
        val environment = HashMap<String, String>()

        environment[ENV_HOME] = shell.homeDir
        environment[ENV_LANG] = DEFAULT_LANG
        environment[ENV_PATH] = System.getenv(ENV_PATH).orEmpty()
        environment[ENV_TMPDIR] = shell.tmpDir

        environment[ENV_COLORTERM] = DEFAULT_COLOR
        environment[ENV_TERM] = DEFAULT_TERM

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

        sessions[sessionId] = SessionModel(
            id = sessionId,
            name = DEFAULT_NAME,
            ordinal = counter.getAndIncrement(),
            session = TerminalSession(
                /* shellPath = */ shell.shellPath,
                /* cwd = */ shell.homeDir,
                /* args = */ emptyArray(),
                /* env = */ convertEnvironmentToEnviron(environment).toTypedArray(),
                /* transcriptRows = */ TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
                /* client = */ client,
            ),
            commands = commands.asSharedFlow(),
        )
        return sessionId
    }

    fun closeSession(sessionId: String) {
        val terminalSession = sessions[sessionId]?.session ?: return
        if (terminalSession.pid > 0) {
            terminalSession.finishIfRunning()
        }
        sessions.remove(sessionId)

        if (sessions.isEmpty()) {
            counter.set(0)
        }
    }

    companion object {
        private const val DEFAULT_NAME = "Local"
        private const val DEFAULT_LANG = "en_US.UTF-8"
        private const val DEFAULT_COLOR = "truecolor"
        private const val DEFAULT_TERM = "xterm-256color"
    }
}