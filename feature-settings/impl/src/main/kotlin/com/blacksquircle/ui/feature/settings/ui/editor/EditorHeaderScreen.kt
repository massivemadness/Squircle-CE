/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.settings.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SliderPreference
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.preference.TextFieldPreference
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun EditorHeaderScreen(viewModel: EditorHeaderViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    EditorHeaderScreen(
        viewState = viewState,
        onBackClicked = viewModel::popBackStack,
        onFontSizeChanged = viewModel::onFontSizeChanged,
        onWordWrapChanged = viewModel::onWordWrapChanged,
        onCodeCompletionChanged = viewModel::onCodeCompletionChanged,
        onPinchZoomChanged = viewModel::onPinchZoomChanged,
        onLineNumbersChanged = viewModel::onLineNumbersChanged,
        onHighlightCurrentLineChanged = viewModel::onHighlightCurrentLineChanged,
        onHighlightMatchingDelimitersChanged = viewModel::onHighlightMatchingDelimitersChanged,
        onReadOnlyChanged = viewModel::onReadOnlyChanged,
        onAutoSaveFilesChanged = viewModel::onAutoSaveFilesChanged,
        onExtendedKeyboardChanged = viewModel::onExtendedKeyboardChanged,
        onKeyboardPresetChanged = viewModel::onKeyboardPresetChanged,
        onSoftKeyboardChanged = viewModel::onSoftKeyboardChanged,
    )
}

@Composable
private fun EditorHeaderScreen(
    viewState: EditorHeaderState,
    onBackClicked: () -> Unit,
    onFontSizeChanged: (Int) -> Unit,
    onWordWrapChanged: (Boolean) -> Unit,
    onCodeCompletionChanged: (Boolean) -> Unit,
    onPinchZoomChanged: (Boolean) -> Unit,
    onLineNumbersChanged: (Boolean) -> Unit,
    onHighlightCurrentLineChanged: (Boolean) -> Unit,
    onHighlightMatchingDelimitersChanged: (Boolean) -> Unit,
    onReadOnlyChanged: (Boolean) -> Unit,
    onAutoSaveFilesChanged: (Boolean) -> Unit,
    onExtendedKeyboardChanged: (Boolean) -> Unit,
    onKeyboardPresetChanged: (String) -> Unit,
    onSoftKeyboardChanged: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_editor_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.navigationBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.pref_category_code_style)
            )
            SliderPreference(
                title = stringResource(R.string.pref_font_size_title),
                subtitle = stringResource(R.string.pref_font_size_summary),
                minValue = 10f,
                maxValue = 20f,
                stepCount = 10,
                currentValue = viewState.fontSize.toFloat(),
                onValueChanged = { fontSize ->
                    onFontSizeChanged(fontSize.toInt())
                }
            )
            Preference(
                title = stringResource(R.string.pref_font_type_title),
                subtitle = stringResource(R.string.pref_font_type_summary),
                // TODO onClick = onFontTypeClicked
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_editor)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_word_wrap_title),
                subtitle = stringResource(R.string.pref_word_wrap_summary),
                checked = viewState.wordWrap,
                onCheckedChange = onWordWrapChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_code_completion_title),
                subtitle = stringResource(R.string.pref_code_completion_summary),
                checked = viewState.codeCompletion,
                onCheckedChange = onCodeCompletionChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_pinch_zoom_title),
                subtitle = stringResource(R.string.pref_pinch_zoom_summary),
                checked = viewState.pinchZoom,
                onCheckedChange = onPinchZoomChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_line_numbers_title),
                subtitle = stringResource(R.string.pref_line_numbers_summary),
                checked = viewState.lineNumbers,
                onCheckedChange = onLineNumbersChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_highlight_line_title),
                subtitle = stringResource(R.string.pref_highlight_line_summary),
                checked = viewState.highlightCurrentLine,
                onCheckedChange = onHighlightCurrentLineChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_highlight_delimiters_title),
                subtitle = stringResource(R.string.pref_highlight_delimiters_summary),
                checked = viewState.highlightMatchingDelimiters,
                onCheckedChange = onHighlightMatchingDelimitersChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_read_only_title),
                subtitle = stringResource(R.string.pref_read_only_summary),
                checked = viewState.readOnly,
                onCheckedChange = onReadOnlyChanged,
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_tabs)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_auto_save_files_title),
                subtitle = stringResource(R.string.pref_auto_save_files_summary),
                checked = viewState.autoSaveFiles,
                onCheckedChange = onAutoSaveFilesChanged,
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_keyboard)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_extended_keyboard_title),
                subtitle = stringResource(R.string.pref_extended_keyboard_summary),
                checked = viewState.extendedKeyboard,
                onCheckedChange = onExtendedKeyboardChanged,
            )
            TextFieldPreference(
                title = stringResource(R.string.pref_keyboard_preset_title),
                subtitle = stringResource(R.string.pref_keyboard_preset_summary),
                enabled = viewState.extendedKeyboard,
                confirmButton = stringResource(UiR.string.common_save),
                dismissButton = stringResource(android.R.string.cancel),
                topHelperText = stringResource(R.string.hint_enter_preset_chars),
                bottomHelperText = stringResource(R.string.message_preset_disclaimer),
                inputTextStyle = TextStyle(fontFamily = FontFamily.Monospace),
                inputValue = viewState.keyboardPreset,
                onInputConfirmed = onKeyboardPresetChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_soft_keyboard_title),
                subtitle = stringResource(R.string.pref_soft_keyboard_summary),
                checked = viewState.softKeyboard,
                onCheckedChange = onSoftKeyboardChanged,
            )
        }
    }
}

@Preview
@Composable
private fun EditorHeaderScreenPreview() {
    SquircleTheme {
        EditorHeaderScreen(
            viewState = EditorHeaderState(
                fontSize = 14,
                wordWrap = true,
                codeCompletion = true,
                pinchZoom = true,
                lineNumbers = true,
                highlightCurrentLine = true,
                highlightMatchingDelimiters = true,
                readOnly = false,
                autoSaveFiles = false,
                extendedKeyboard = true,
                keyboardPreset = "0123456789",
                softKeyboard = false,
            ),
            onBackClicked = {},
            onFontSizeChanged = {},
            onWordWrapChanged = {},
            onCodeCompletionChanged = {},
            onPinchZoomChanged = {},
            onLineNumbersChanged = {},
            onHighlightCurrentLineChanged = {},
            onHighlightMatchingDelimitersChanged = {},
            onReadOnlyChanged = {},
            onAutoSaveFilesChanged = {},
            onExtendedKeyboardChanged = {},
            onKeyboardPresetChanged = {},
            onSoftKeyboardChanged = {},
        )
    }
}