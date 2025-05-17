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

package com.blacksquircle.ui.feature.terminal.ui.compose

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.blacksquircle.ui.core.extensions.copyText
import com.blacksquircle.ui.core.extensions.primaryClipText
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.terminal.domain.model.SessionModel
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalViewClientImpl
import com.termux.view.TerminalView

@Composable
internal fun Terminal(
    session: SessionModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val textSize = with(LocalDensity.current) { 12.sp.toPx() }
    val backgroundColor = SquircleTheme.colors.colorBackgroundPrimary.toArgb()
    val foregroundColor = SquircleTheme.colors.colorTextAndIconPrimary.toArgb()

    val terminalView = remember {
        TerminalView(context, null).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()

            setTextSize(textSize.toInt())
            setTypeface(Typeface.MONOSPACE)

            val viewClient = TerminalViewClientImpl(
                terminalView = this,
                backgroundColor = backgroundColor,
                foregroundColor = foregroundColor
            )
            setTerminalViewClient(viewClient)
        }
    }

    AndroidView(
        factory = { terminalView },
        update = { terminalView.onScreenUpdated() },
        modifier = modifier.fillMaxSize(),
    )

    LaunchedEffect(session.sessionId) {
        terminalView.attachSession(session.session)
    }

    LaunchedEffect(Unit) {
        session.commands.collect { command ->
            when (command) {
                is TerminalCommand.Update -> {
                    terminalView.onScreenUpdated()
                }
                is TerminalCommand.Copy -> {
                    context.copyText(command.text)
                }
                is TerminalCommand.Paste -> {
                    val text = context.primaryClipText()
                    terminalView.mEmulator?.paste(text)
                }
            }
        }
    }
}