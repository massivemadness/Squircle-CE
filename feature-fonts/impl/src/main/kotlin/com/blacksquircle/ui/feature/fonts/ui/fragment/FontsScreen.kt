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

package com.blacksquircle.ui.feature.fonts.ui.fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.ui.composable.FontOverview
import com.blacksquircle.ui.feature.fonts.ui.viewmodel.FontsViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun FontsScreen(viewModel: FontsViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    FontsScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQueryClicked = viewModel::onClearQueryClicked,
        onSelectClicked = viewModel::onSelectClicked,
        onRemoveClicked = viewModel::onRemoveClicked,
        onImportClicked = viewModel::onImportClicked,
    )
}

@Composable
private fun FontsScreen(
    viewState: FontsViewState,
    onBackClicked: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onClearQueryClicked: () -> Unit,
    onSelectClicked: (FontModel) -> Unit,
    onRemoveClicked: (FontModel) -> Unit,
    onImportClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
                navigationActions = {
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
                            if (viewState.query.isNotEmpty()) {
                                IconButton(
                                    iconResId = UiR.drawable.ic_close,
                                    iconSize = IconButtonSize.S,
                                    onClick = onClearQueryClicked,
                                )
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingButton(
                iconResId = UiR.drawable.ic_plus,
                onClick = onImportClicked,
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
                items(
                    items = viewState.fonts,
                    key = FontModel::fontUuid,
                ) { font ->
                    FontOverview(
                        fontName = font.fontName,
                        fontPath = font.fontPath,
                        canRemove = font.isExternal,
                        onSelectClicked = { onSelectClicked(font) },
                        onRemoveClicked = { onRemoveClicked(font) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
            if (viewState.fonts.isEmpty()) {
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
private fun FontsScreenPreview() {
    SquircleTheme {
        FontsScreen(
            viewState = FontsViewState(
                query = "Mono",
                fonts = listOf(
                    FontModel(
                        fontUuid = "1",
                        fontName = "Droid Sans Mono",
                        fontPath = "1",
                        isExternal = true,
                    ),
                    FontModel(
                        fontUuid = "2",
                        fontName = "JetBrains Mono",
                        fontPath = "2",
                        isExternal = true,
                    ),
                ),
                isLoading = false,
            ),
            onBackClicked = {},
            onQueryChanged = {},
            onClearQueryClicked = {},
            onSelectClicked = {},
            onRemoveClicked = {},
            onImportClicked = {},
        )
    }
}