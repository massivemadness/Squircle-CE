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

package com.blacksquircle.ui.feature.editor.ui.editor.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.button.TextButton
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.editor.menu.FindMenu
import com.blacksquircle.ui.feature.editor.ui.editor.model.SearchState
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun SearchPanel(
    searchState: SearchState,
    modifier: Modifier = Modifier,
    onFindTextChanged: (String) -> Unit = {},
    onReplaceTextChanged: (String) -> Unit = {},
    onToggleReplaceClicked: () -> Unit = {},
    onRegexClicked: () -> Unit = {},
    onMatchCaseClicked: () -> Unit = {},
    onWordsOnlyClicked: () -> Unit = {},
    onCloseSearchClicked: () -> Unit = {},
    onPreviousMatchClicked: () -> Unit = {},
    onNextMatchClicked: () -> Unit = {},
    onReplaceMatchClicked: () -> Unit = {},
    onReplaceAllClicked: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier.padding(vertical = 8.dp)) {
        Row(Modifier.padding(horizontal = 8.dp)) {
            IconButton(
                iconResId = if (searchState.replaceShown) {
                    UiR.drawable.ic_menu_up
                } else {
                    UiR.drawable.ic_menu_down
                },
                iconButtonStyle = IconButtonStyleDefaults.Secondary,
                iconButtonSize = IconButtonSizeDefaults.XS,
                onClick = onToggleReplaceClicked,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Column(Modifier.weight(1f)) {
                var menuExpanded by rememberSaveable { mutableStateOf(false) }

                TextField(
                    inputText = searchState.findText,
                    onInputChanged = onFindTextChanged,
                    placeholderText = stringResource(R.string.action_find),
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (searchState.replaceShown) {
                            ImeAction.Next
                        } else {
                            ImeAction.Unspecified
                        },
                    ),
                    endContent = {
                        IconButton(
                            iconResId = UiR.drawable.ic_dots_vertical,
                            iconButtonStyle = IconButtonStyleDefaults.Secondary,
                            iconButtonSize = IconButtonSizeDefaults.XS,
                            onClick = { menuExpanded = !menuExpanded },
                            anchor = {
                                FindMenu(
                                    expanded = menuExpanded,
                                    onDismiss = { menuExpanded = false },
                                    regex = searchState.regex,
                                    matchCase = searchState.matchCase,
                                    wordsOnly = searchState.wordsOnly,
                                    onRegexClicked = { menuExpanded = false; onRegexClicked() },
                                    onMatchCaseClicked = { menuExpanded = false; onMatchCaseClicked() },
                                    onWordsOnlyClicked = { menuExpanded = false; onWordsOnlyClicked() },
                                )
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .focusRequester(focusRequester)
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                if (searchState.replaceShown) {
                    Spacer(Modifier.height(8.dp))

                    TextField(
                        inputText = searchState.replaceText,
                        onInputChanged = onReplaceTextChanged,
                        placeholderText = stringResource(R.string.action_replace),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            IconButton(
                iconResId = UiR.drawable.ic_close,
                iconButtonStyle = IconButtonStyleDefaults.Secondary,
                iconButtonSize = IconButtonSizeDefaults.XS,
                onClick = onCloseSearchClicked,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            TextButton(
                text = stringResource(R.string.action_prev),
                onClick = onPreviousMatchClicked,
                debounce = false,
            )
            TextButton(
                text = stringResource(R.string.action_next),
                onClick = onNextMatchClicked,
                debounce = false,
            )
            TextButton(
                text = stringResource(R.string.action_replace),
                onClick = onReplaceMatchClicked,
                enabled = searchState.replaceShown,
                debounce = false,
            )
            TextButton(
                text = stringResource(R.string.action_replace_all),
                onClick = onReplaceAllClicked,
                enabled = searchState.replaceShown,
                debounce = true,
            )
        }
    }
    BackHandler {
        onCloseSearchClicked()
    }
}

@PreviewLightDark
@Composable
private fun SearchPanelPreview() {
    PreviewBackground {
        SearchPanel(
            searchState = SearchState(
                findText = "",
                replaceShown = true,
                replaceText = "",
                regex = true,
                matchCase = false,
                wordsOnly = false,
            )
        )
    }
}