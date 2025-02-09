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

package com.blacksquircle.ui.feature.themes.ui.fragment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.FloatingButton
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSize
import com.blacksquircle.ui.ds.dropdown.Dropdown
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.fonts.api.model.InternalFont
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.api.model.InternalTheme
import com.blacksquircle.ui.feature.themes.api.model.ThemeModel
import com.blacksquircle.ui.feature.themes.data.model.CodePreview
import com.blacksquircle.ui.feature.themes.ui.composable.ThemeOverview
import com.blacksquircle.ui.feature.themes.ui.viewmodel.ThemesViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ThemesScreen(viewModel: ThemesViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ThemesScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onCodePreviewChanged = viewModel::onCodePreviewChanged,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQueryClicked = viewModel::onClearQueryClicked,
        onCreateClicked = viewModel::onCreateClicked,
        onSelectClicked = viewModel::onSelectClicked,
        onExportClicked = viewModel::onExportClicked,
        onEditClicked = viewModel::onEditClicked,
        onRemoveClicked = viewModel::onRemoveClicked,
    )
}

@Composable
private fun ThemesScreen(
    viewState: ThemesViewState,
    onBackClicked: () -> Unit = {},
    onCodePreviewChanged: (String) -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onSelectClicked: (ThemeModel) -> Unit = {},
    onExportClicked: (ThemeModel) -> Unit = {},
    onEditClicked: (ThemeModel) -> Unit = {},
    onRemoveClicked: (ThemeModel) -> Unit = {},
) {
    val scrollState = rememberLazyGridState()
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
                        Dropdown(
                            entries = stringArrayResource(R.array.preview_names),
                            entryValues = stringArrayResource(R.array.preview_extensions),
                            currentValue = viewState.preview.extension,
                            onValueSelected = onCodePreviewChanged,
                        )
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
                    onClick = onCreateClicked,
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

            val itemPadding = 8.dp
            val layoutDirection = LocalLayoutDirection.current

            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Adaptive(300.dp),
                verticalArrangement = Arrangement.spacedBy(itemPadding),
                horizontalArrangement = Arrangement.spacedBy(itemPadding),
                contentPadding = PaddingValues(
                    top = contentPadding.calculateTopPadding() + itemPadding,
                    start = contentPadding.calculateStartPadding(layoutDirection) + itemPadding,
                    end = contentPadding.calculateEndPadding(layoutDirection) + itemPadding,
                    bottom = contentPadding.calculateBottomPadding() + itemPadding,
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = viewState.themes,
                    key = ThemeModel::uuid,
                ) { theme ->
                    ThemeOverview(
                        themeModel = theme,
                        isSelected = theme.uuid == viewState.currentTheme.uuid,
                        fontPath = viewState.currentFont.path,
                        codePreview = viewState.preview,
                        onSelectClicked = { onSelectClicked(theme) },
                        onExportClicked = { onExportClicked(theme) },
                        onEditClicked = { onEditClicked(theme) },
                        onRemoveClicked = { onRemoveClicked(theme) },
                        modifier = Modifier.animateItem(),
                    )
                }
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

@PreviewLightDark
@Composable
private fun ThemesScreenPreview() {
    PreviewBackground {
        ThemesScreen(
            viewState = ThemesViewState(
                query = "Mono",
                preview = CodePreview.HTML,
                themes = InternalTheme.entries.map(InternalTheme::theme),
                currentTheme = InternalTheme.THEME_DARCULA.theme,
                currentFont = InternalFont.JETBRAINS_MONO.font,
                isLoading = false,
            ),
        )
    }
}