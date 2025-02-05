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

package com.blacksquircle.ui.feature.themes.ui.fragment

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSize
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.ui.viewmodel.EditThemeViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun EditThemeScreen(viewModel: EditThemeViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    EditThemeScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
    )
}

@Composable
private fun EditThemeScreen(
    viewState: EditThemeViewState,
    onBackClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.label_new_theme),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
                navigationActions = {
                    IconButton(
                        iconResId = UiR.drawable.ic_file_import,
                        iconSize = IconButtonSize.L,
                        onClick = {},
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        // TODO
    }
}

@Preview
@Composable
private fun EditThemeScreenPreview() {
    SquircleTheme {
        EditThemeScreen(
            viewState = EditThemeViewState(
                name = "Darcula",
                author = "Squircle CE",
            ),
            onBackClicked = {},
        )
    }
}