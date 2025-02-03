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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.FloatingButton
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSize
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.loader.Loader
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.domain.model.InternalTheme
import com.blacksquircle.ui.feature.themes.ui.viewmodel.ThemesViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ThemesScreen(viewModel: ThemesViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ThemesScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQueryClicked = viewModel::onClearQueryClicked,
        onCreateClicked = {},
    )
}

@Composable
private fun ThemesScreen(
    viewState: ThemesViewState,
    onBackClicked: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onClearQueryClicked: () -> Unit,
    onCreateClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            var expanded by rememberSaveable { mutableStateOf(false) }
            Toolbar(
                title = stringResource(R.string.label_themes),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
                navigationActions = {
                    if (expanded) {
                        val focusRequester = remember { FocusRequester() }
                        TextField(
                            inputText = viewState.query,
                            onInputChanged = onQueryChanged,
                            placeholderText = stringResource(android.R.string.search_go),
                            startContent = {
                                Icon(
                                    painter = painterResource(UiR.drawable.ic_search),
                                    contentDescription = null,
                                    tint = SquircleTheme.colors.colorTextAndIconSecondary,
                                    modifier = Modifier.padding(8.dp),
                                )
                            },
                            endContent = {
                                IconButton(
                                    iconResId = UiR.drawable.ic_close,
                                    iconSize = IconButtonSize.S,
                                    onClick = {
                                        onClearQueryClicked()
                                        expanded = false
                                    },
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .padding(horizontal = 8.dp)
                                .focusRequester(focusRequester)
                        )
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        IconButton(
                            iconResId = UiR.drawable.ic_search,
                            iconSize = IconButtonSize.L,
                            onClick = { expanded = true },
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingButton(
                iconResId = UiR.drawable.ic_plus,
                onClick = onCreateClicked,
                modifier = Modifier.padding(8.dp)
            )
        },
        modifier = Modifier.navigationBarsPadding()
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (viewState.isLoading) {
                Loader()
                return@Scaffold
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                /*items(
                    items = viewState.themes,
                    key = FontModel::themeUuid,
                ) { theme ->

                }*/
            }
            if (viewState.themes.isEmpty()) {
                EmptyView(
                    iconResId = UiR.drawable.ic_file_find,
                    title = stringResource(UiR.string.common_no_result),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ThemesScreenPreview() {
    SquircleTheme {
        ThemesScreen(
            viewState = ThemesViewState(
                query = "Mono",
                themes = InternalTheme.entries.map(InternalTheme::theme),
                isLoading = false,
            ),
            onBackClicked = {},
            onQueryChanged = {},
            onClearQueryClicked = {},
            onCreateClicked = {},
        )
    }
}