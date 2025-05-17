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

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.termux.terminal.TerminalSession
import com.termux.terminal.TextStyle
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient
import timber.log.Timber
import java.lang.Exception

internal class TerminalViewClientImpl(
    private val terminalView: TerminalView,
    private val backgroundColor: Int,
    private val foregroundColor: Int,
) : TerminalViewClient {

    override fun onScale(scale: Float): Float {
        return scale
    }

    override fun onSingleTapUp(e: MotionEvent) {
        terminalView.requestFocus()
        val inputMethodManager = terminalView.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(terminalView, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun shouldBackButtonBeMappedToEscape(): Boolean {
        return false
    }

    override fun shouldEnforceCharBasedInput(): Boolean {
        return true
    }

    override fun shouldUseCtrlSpaceWorkaround(): Boolean {
        return true
    }

    override fun isTerminalViewSelected(): Boolean {
        return true
    }

    override fun copyModeChanged(copyMode: Boolean) {
    }

    override fun onKeyDown(
        keyCode: Int,
        e: KeyEvent?,
        session: TerminalSession?
    ): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, e: KeyEvent?): Boolean {
        return false
    }

    override fun onLongPress(event: MotionEvent?): Boolean {
        return false
    }

    override fun readControlKey(): Boolean {
        return false
    }

    override fun readAltKey(): Boolean {
        return false
    }

    override fun readShiftKey(): Boolean {
        return false
    }

    override fun readFnKey(): Boolean {
        return false
    }

    override fun onCodePoint(
        codePoint: Int,
        ctrlDown: Boolean,
        session: TerminalSession?
    ): Boolean {
        return false
    }

    override fun onEmulatorSet() {
        if (terminalView.mEmulator != null) {
            terminalView.mEmulator.mColors.apply {
                mCurrentColors[TextStyle.COLOR_INDEX_BACKGROUND] = backgroundColor
                mCurrentColors[TextStyle.COLOR_INDEX_FOREGROUND] = foregroundColor
                mCurrentColors[TextStyle.COLOR_INDEX_CURSOR] = foregroundColor
            }
            terminalView.setTerminalCursorBlinkerRate(600)
            terminalView.setTerminalCursorBlinkerState(true, true)
        }
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