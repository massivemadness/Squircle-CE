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

package com.blacksquircle.ui.feature.fonts.ui.fonts

import android.graphics.Typeface
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.MimeType
import com.blacksquircle.ui.core.contract.rememberOpenFileContract
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.FloatingButton
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.internal.FontsComponent
import com.blacksquircle.ui.feature.fonts.ui.fonts.compose.FontOverview
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun FontsScreen(
    navController: NavController,
    viewModel: FontsViewModel = daggerViewModel { context ->
        val component = FontsComponent.buildOrGet(context)
        FontsViewModel.Factory().also(component::inject)
    }
) {
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

    val openFileContract = rememberOpenFileContract { result ->
        when (result) {
            is ContractResult.Success -> viewModel.onFontLoaded(result.uri)
            is ContractResult.Canceled -> Unit
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.PopBackStack -> navController.popBackStack()
                is FontsViewEvent.ChooseFont -> openFileContract.launch(
                    arrayOf(MimeType.OCTET_STREAM, MimeType.X_FONT, MimeType.FONT)
                )
            }
        }
    }

    CleanupEffect {
        FontsComponent.release()
    }
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
    val lazyListState = rememberLazyGridState()
    val showButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 ||
                lazyListState.lastScrolledBackward
        }
    }

    ScaffoldSuite(
        topBar = {
            var searchMode by rememberSaveable {
                mutableStateOf(false)
            }
            Toolbar(
                title = stringResource(R.string.label_fonts),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
                navigationActions = {
                    if (searchMode) {
                        val focusRequester = remember { FocusRequester() }
                        TextField(
                            inputText = viewState.searchQuery,
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
                                    iconButtonStyle = IconButtonStyleDefaults.Secondary,
                                    iconButtonSize = IconButtonSizeDefaults.S,
                                    onClick = { onClearQueryClicked(); searchMode = false },
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
                        BackHandler {
                            onClearQueryClicked()
                            searchMode = false
                        }
                    } else {
                        IconButton(
                            iconResId = UiR.drawable.ic_search,
                            iconButtonSize = IconButtonSizeDefaults.L,
                            onClick = { searchMode = true },
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
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (viewState.isLoading) {
                CircularProgress()
                return@ScaffoldSuite
            }

            val itemPadding = 8.dp
            val layoutDirection = LocalLayoutDirection.current

            LazyVerticalGrid(
                state = lazyListState,
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
                    items = viewState.fonts,
                    key = FontModel::uuid,
                ) { font ->
                    FontOverview(
                        fontModel = font,
                        isSelected = font.uuid == viewState.selectedFont,
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
                searchQuery = "Mono",
                fonts = listOf(
                    FontModel(
                        uuid = "1",
                        name = "Droid Sans Mono",
                        typeface = Typeface.MONOSPACE,
                        isExternal = true,
                    ),
                    FontModel(
                        uuid = "2",
                        name = "JetBrains Mono",
                        typeface = Typeface.MONOSPACE,
                        isExternal = true,
                    ),
                ),
                isLoading = false,
            ),
        )
    }
}