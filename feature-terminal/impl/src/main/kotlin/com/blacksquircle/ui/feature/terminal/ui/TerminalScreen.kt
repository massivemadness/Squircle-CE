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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.terminal.R
import com.blacksquircle.ui.feature.terminal.internal.TerminalComponent
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalSessionClient
import com.blacksquircle.ui.feature.terminal.ui.view.TerminalViewClient
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
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
    TerminalScreen(
        onSessionClicked = {},
        onCreateSessionClicked = viewModel::onCreateSessionClicked,
        onCloseSessionClicked = viewModel::onCloseSessionClicked,
        onBackClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun TerminalScreen(
    onSessionClicked: () -> Unit = {},
    onCreateSessionClicked: () -> Unit = {},
    onCloseSessionClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
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
        val textSize = with(LocalDensity.current) { 14.sp.toPx() }
        AndroidView(
            factory = { context ->
                TerminalView(context, null).apply {
                    setTextSize(textSize.toInt())
                    setTypeface(Typeface.MONOSPACE)

                    val sessionClient = TerminalSessionClient()
                    val viewClient = TerminalViewClient()
                    setTerminalViewClient(viewClient)

                    val terminalSession = TerminalSession(
                        "",
                        "",
                        emptyArray(),
                        emptyArray(),
                        TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
                        sessionClient,
                    )

                    attachSession(terminalSession)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PreviewLightDark
@Composable
private fun TerminalScreenPreview() {
    PreviewBackground {
        TerminalScreen()
    }
}