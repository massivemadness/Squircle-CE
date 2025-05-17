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

package com.blacksquircle.ui.feature.terminal.ui.view

import com.blacksquircle.ui.feature.terminal.ui.compose.TerminalCommand
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import java.lang.Exception

internal class TerminalSessionClientImpl(
    private val commands: MutableSharedFlow<TerminalCommand>,
) : TerminalSessionClient {

    override fun onTextChanged(changedSession: TerminalSession) {
        commands.tryEmit(TerminalCommand.Update)
    }
    override fun onTitleChanged(changedSession: TerminalSession) {
        commands.tryEmit(TerminalCommand.Update)
    }

    override fun onSessionFinished(finishedSession: TerminalSession) = Unit

    override fun onCopyTextToClipboard(session: TerminalSession, text: String) {
        commands.tryEmit(TerminalCommand.Copy(text))
    }

    override fun onPasteTextFromClipboard(session: TerminalSession?) {
        commands.tryEmit(TerminalCommand.Paste)
    }

    override fun onBell(session: TerminalSession) = Unit

    override fun onColorsChanged(session: TerminalSession) {
        commands.tryEmit(TerminalCommand.Update)
    }
    override fun onTerminalCursorStateChange(state: Boolean) {
        commands.tryEmit(TerminalCommand.Update)
    }

    override fun setTerminalShellPid(session: TerminalSession, pid: Int) = Unit

    override fun getTerminalCursorStyle(): Int {
        return TerminalEmulator.DEFAULT_TERMINAL_CURSOR_STYLE
    }

    override fun logError(tag: String?, message: String?) {
        Timber.tag(tag.toString()).e(message)
    }
    override fun logWarn(tag: String?, message: String?) {
        Timber.tag(tag.toString()).w(message)
    }
    override fun logInfo(tag: String?, message: String?) {
        Timber.tag(tag.toString()).i(message)
    }
    override fun logDebug(tag: String?, message: String?) {
        Timber.tag(tag.toString()).d(message)
    }
    override fun logVerbose(tag: String?, message: String?) {
        Timber.tag(tag.toString()).v(message)
    }
    override fun logStackTraceWithMessage(tag: String?, message: String?, e: Exception?) {
        logError(tag, message)
        e?.printStackTrace()
    }
    override fun logStackTrace(tag: String?, e: Exception?) {
        e?.printStackTrace()
    }
}