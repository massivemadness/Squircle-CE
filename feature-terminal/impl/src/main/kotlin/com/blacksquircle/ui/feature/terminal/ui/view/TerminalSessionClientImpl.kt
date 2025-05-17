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

import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import timber.log.Timber
import java.lang.Exception

internal class TerminalSessionClientImpl(
    private val redraw: () -> Unit = {},
) : TerminalSessionClient {

    override fun onTextChanged(changedSession: TerminalSession) = redraw()
    override fun onTitleChanged(changedSession: TerminalSession) = redraw()

    override fun onSessionFinished(finishedSession: TerminalSession) {
        Timber.e("TerminalSessionClient: onSessionFinished")
    }

    override fun onCopyTextToClipboard(
        session: TerminalSession,
        text: String?
    ) {
    }

    override fun onPasteTextFromClipboard(session: TerminalSession?) {
    }

    override fun onBell(session: TerminalSession) {
    }

    override fun onColorsChanged(session: TerminalSession) = redraw()
    override fun onTerminalCursorStateChange(state: Boolean) = redraw()

    override fun setTerminalShellPid(
        session: TerminalSession,
        pid: Int
    ) {
    }

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