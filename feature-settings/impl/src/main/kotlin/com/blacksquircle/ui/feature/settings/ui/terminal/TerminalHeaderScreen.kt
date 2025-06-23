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

package com.blacksquircle.ui.feature.settings.ui.terminal

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.copyText
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.isPermissionGranted
import com.blacksquircle.ui.core.extensions.openAppSettings
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.preference.ListPreference
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.textfield.TextFieldStyleDefaults
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import com.blacksquircle.ui.feature.terminal.api.model.RuntimeType
import com.blacksquircle.ui.ds.R as UiR

private const val TERMUX_PERMISSION = "com.termux.permission.RUN_COMMAND"
private const val TERMUX_PROPERTIES = "allow-external-apps = true"

@Composable
internal fun TerminalHeaderScreen(
    navController: NavController,
    viewModel: TerminalHeaderViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        TerminalHeaderViewModel.Factory().also(component::inject)
    }
) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var canRunTermuxCommand by remember {
        val initialValue = context.isPermissionGranted(TERMUX_PERMISSION)
        mutableStateOf(initialValue)
    }
    TerminalHeaderScreen(
        viewState = viewState,
        canRunTermuxCommand = canRunTermuxCommand,
        onBackClicked = navController::popBackStack,
        onTerminalRuntimeChanged = viewModel::onTerminalRuntimeChanged,
        onTermuxPermissionClicked = context::openAppSettings,
        onTermuxCopyPropsClicked = {
            context.copyText(TERMUX_PROPERTIES)
            viewModel.onTermuxCopyPropsClicked()
        },
        onCursorBlinkingChanged = viewModel::onCursorBlinkingChanged,
        onKeepScreenOnChanged = viewModel::onKeepScreenOnChanged,
    )

    LifecycleResumeEffect(Unit) {
        canRunTermuxCommand = context.isPermissionGranted(TERMUX_PERMISSION)
        onPauseOrDispose { /* no-op */ }
    }
}

@Composable
private fun TerminalHeaderScreen(
    viewState: TerminalHeaderViewState,
    canRunTermuxCommand: Boolean,
    onBackClicked: () -> Unit = {},
    onTerminalRuntimeChanged: (String) -> Unit = {},
    onTermuxPermissionClicked: () -> Unit = {},
    onTermuxCopyPropsClicked: () -> Unit = {},
    onCursorBlinkingChanged: (Boolean) -> Unit = {},
    onKeepScreenOnChanged: (Boolean) -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.settings_header_terminal_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.settings_category_environment),
            )
            ListPreference(
                title = stringResource(R.string.settings_terminal_runtime_title),
                subtitle = stringResource(R.string.settings_terminal_runtime_subtitle),
                entries = RuntimeType.entries
                    .fastMap(RuntimeType::title).toTypedArray(),
                entryValues = RuntimeType.entries
                    .fastMap(RuntimeType::value).toTypedArray(),
                selectedValue = viewState.currentRuntime.value,
                onValueSelected = onTerminalRuntimeChanged,
            )

            HorizontalDivider()

            PreferenceGroup(
                title = stringResource(R.string.settings_category_termux),
                enabled = viewState.isTermux,
            )

            Preference(
                title = stringResource(R.string.settings_terminal_exec_permission_title),
                subtitle = stringResource(R.string.settings_terminal_exec_permission_subtitle),
                enabled = viewState.isTermux,
                onClick = onTermuxPermissionClicked,
                trailingContent = {
                    CheckBox(
                        checked = canRunTermuxCommand,
                        enabled = viewState.isTermux,
                        onClick = onTermuxPermissionClicked,
                    )
                },
            )

            Preference(
                title = stringResource(R.string.settings_terminal_allow_external_apps_title),
                subtitle = stringResource(R.string.settings_terminal_allow_external_apps_subtitle),
                enabled = viewState.isTermux,
                bottomContent = {
                    Spacer(Modifier.height(8.dp))

                    TextField(
                        inputText = TERMUX_PROPERTIES,
                        enabled = viewState.isTermux,
                        readOnly = true,
                        textFieldStyle = TextFieldStyleDefaults.Default.copy(
                            textStyle = TextStyle(
                                fontFamily = FontFamily(Typeface.MONOSPACE),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            ),
                        ),
                        endContent = {
                            IconButton(
                                iconResId = if (viewState.termuxPropsCopied) {
                                    UiR.drawable.ic_check
                                } else {
                                    UiR.drawable.ic_copy
                                },
                                enabled = viewState.isTermux,
                                onClick = {
                                    if (!viewState.termuxPropsCopied) {
                                        onTermuxCopyPropsClicked()
                                    }
                                },
                                iconButtonStyle = IconButtonStyleDefaults.Secondary,
                                iconButtonSize = IconButtonSizeDefaults.S,
                            )
                        }
                    )
                }
            )

            HorizontalDivider()

            PreferenceGroup(
                title = stringResource(R.string.settings_category_behavior),
                enabled = !viewState.isTermux,
            )
            SwitchPreference(
                title = stringResource(R.string.settings_terminal_cursor_blinking_title),
                subtitle = stringResource(R.string.settings_terminal_cursor_blinking_subtitle),
                enabled = !viewState.isTermux,
                checked = viewState.cursorBlinking,
                onCheckedChange = onCursorBlinkingChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.settings_terminal_keep_screen_on_title),
                subtitle = stringResource(R.string.settings_terminal_keep_screen_on_subtitle),
                enabled = !viewState.isTermux,
                checked = viewState.keepScreenOn,
                onCheckedChange = onKeepScreenOnChanged,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TerminalHeaderScreenPreview() {
    PreviewBackground {
        TerminalHeaderScreen(
            viewState = TerminalHeaderViewState(
                currentRuntime = RuntimeType.ANDROID,
                termuxPropsCopied = false,
                cursorBlinking = true,
                keepScreenOn = true,
            ),
            canRunTermuxCommand = true,
        )
    }
}