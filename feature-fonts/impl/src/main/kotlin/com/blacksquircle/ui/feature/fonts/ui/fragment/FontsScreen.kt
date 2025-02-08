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

package com.blacksquircle.ui.feature.fonts.ui.fragment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.FloatingButton
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSize
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.fonts.R
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
    onBackClicked: () -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onSelectClicked: (FontModel) -> Unit = {},
    onRemoveClicked: (FontModel) -> Unit = {},
    onImportClicked: () -> Unit = {},
) {
    val scrollState = rememberLazyListState()
    val showButton by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex == 0 ||
                scrollState.lastScrolledBackward
        }
    }

    Scaffold(
        topBar = {
            var expanded by rememberSaveable { mutableStateOf(false) }
            Toolbar(
                title = stringResource(R.string.label_fonts),
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
                                    iconColor = SquircleTheme.colors.colorTextAndIconSecondary,
                                    iconSize = IconButtonSize.S,
                                    onClick = { onClearQueryClicked(); expanded = false },
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
            AnimatedVisibility(
                visible = showButton,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingButton(
                    iconResId = UiR.drawable.ic_plus,
                    onClick = onImportClicked,
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (viewState.isLoading) {
                CircularProgress()
                return@Scaffold
            }
            LazyColumn(
                state = scrollState,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = viewState.fonts,
                    key = FontModel::uuid,
                ) { font ->
                    FontOverview(
                        fontModel = font,
                        isSelected = font.path == viewState.currentFont,
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

@PreviewLightDark
@Composable
private fun FontsScreenPreview() {
    PreviewBackground {
        FontsScreen(
            viewState = FontsViewState(
                query = "Mono",
                fonts = listOf(
                    FontModel(
                        uuid = "1",
                        name = "Droid Sans Mono",
                        path = "1",
                        isExternal = true,
                    ),
                    FontModel(
                        uuid = "2",
                        name = "JetBrains Mono",
                        path = "2",
                        isExternal = true,
                    ),
                ),
                isLoading = false,
            ),
        )
    }
}