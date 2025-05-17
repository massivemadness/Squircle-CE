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

package com.blacksquircle.ui.feature.terminal.ui

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.copyText
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.primaryClipText
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.terminal.R
import com.blacksquircle.ui.feature.terminal.internal.TerminalComponent
import com.blacksquircle.ui.feature.terminal.ui.model.TerminalCommand
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalViewClientImpl
import com.termux.view.TerminalView
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun TerminalScreen(
    navController: NavController,
    viewModel: TerminalViewModel = daggerViewModel { context ->
        val component = TerminalComponent.buildOrGet(context)
        TerminalViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    TerminalScreen(
        viewState = viewState,
        onSessionClicked = {},
        onCreateSessionClicked = viewModel::onCreateSessionClicked,
        onCloseSessionClicked = viewModel::onCloseSessionClicked,
        onBackClicked = { navController.popBackStack() }
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }
}

@Composable
private fun TerminalScreen(
    viewState: TerminalViewState,
    onSessionClicked: () -> Unit = {},
    onCreateSessionClicked: () -> Unit = {},
    onCloseSessionClicked: (String) -> Unit = {},
    onBackClicked: () -> Unit = {},
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
                foregroundColor = foregroundColor,
            )
            setTerminalViewClient(viewClient)
        }
    }

    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.terminal_toolbar_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        val session = viewState.currentSession
            ?: return@ScaffoldSuite

        AndroidView(
            factory = { terminalView },
            update = { terminalView.onScreenUpdated() },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        )

        LaunchedEffect(session.sessionId) {
            terminalView.attachSession(session.session)

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
}

@PreviewLightDark
@Composable
private fun TerminalScreenPreview() {
    PreviewBackground {
        TerminalScreen(
            viewState = TerminalViewState(
                sessions = emptyList(),
                selectedSession = null,
            ),
        )
    }
}