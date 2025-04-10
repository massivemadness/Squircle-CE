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

package com.blacksquircle.ui.feature.editor.ui.editor.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.feature.editor.R

@Composable
internal fun FindMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    regex: Boolean = false,
    matchCase: Boolean = false,
    wordsOnly: Boolean = false,
    onRegexClicked: () -> Unit = {},
    onMatchCaseClicked: () -> Unit = {},
    onWordsOnlyClicked: () -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-56).dp,
        properties = PopupProperties(
            focusable = false // keep keyboard in focus
        ),
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(R.string.action_regex),
            onClick = onRegexClicked,
            trailing = {
                CheckBox(
                    checked = regex,
                    onClick = onRegexClicked,
                )
            }
        )
        PopupMenuItem(
            title = stringResource(R.string.action_match_case),
            onClick = onMatchCaseClicked,
            trailing = {
                CheckBox(
                    checked = matchCase,
                    onClick = onMatchCaseClicked,
                )
            }
        )
        PopupMenuItem(
            title = stringResource(R.string.action_words_only),
            onClick = onWordsOnlyClicked,
            trailing = {
                CheckBox(
                    checked = wordsOnly,
                    onClick = onWordsOnlyClicked,
                )
            }
        )
    }
}