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

package com.blacksquircle.ui.feature.terminal.ui.view

import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import timber.log.Timber

internal class TerminalSessionClientImpl(
    private val onUpdate: () -> Unit,
    private val onCopy: (String) -> Unit,
    private val onPaste: () -> Unit,
) : TerminalSessionClient {

    override fun onTextChanged(changedSession: TerminalSession) = onUpdate()
    override fun onTitleChanged(changedSession: TerminalSession) = onUpdate()
    override fun onSessionFinished(finishedSession: TerminalSession) = Unit
    override fun onCopyTextToClipboard(session: TerminalSession, text: String) = onCopy(text)
    override fun onPasteTextFromClipboard(session: TerminalSession?) = onPaste()
    override fun onBell(session: TerminalSession) = Unit
    override fun onColorsChanged(session: TerminalSession) = onUpdate()
    override fun onTerminalCursorStateChange(state: Boolean) = onUpdate()
    override fun setTerminalShellPid(session: TerminalSession, pid: Int) = Unit
    override fun getTerminalCursorStyle(): Int = TerminalEmulator.DEFAULT_TERMINAL_CURSOR_STYLE

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