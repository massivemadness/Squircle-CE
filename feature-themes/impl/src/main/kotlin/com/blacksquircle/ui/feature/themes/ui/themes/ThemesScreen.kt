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

package com.blacksquircle.ui.feature.themes.ui.themes

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
import com.blacksquircle.ui.core.contract.rememberCreateFileContract
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.core.effect.NavResultEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.FloatingButton
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.domain.model.EditorTheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.internal.ThemesComponent
import com.blacksquircle.ui.feature.themes.ui.themes.compose.ThemeOverview
import com.blacksquircle.ui.ds.R as UiR

internal const val KEY_SAVE = "KEY_SAVE"

@Composable
internal fun ThemesScreen(
    navController: NavController,
    viewModel: ThemesViewModel = daggerViewModel { context ->
        val component = ThemesComponent.buildOrGet(context)
        ThemesViewModel.Factory().also(component::inject)
    },
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ThemesScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQueryClicked = viewModel::onClearQueryClicked,
        onCreateClicked = viewModel::onCreateClicked,
        onSelectClicked = viewModel::onSelectClicked,
        onExportClicked = viewModel::onExportClicked,
        onEditClicked = viewModel::onEditClicked,
        onRemoveClicked = viewModel::onRemoveClicked,
    )

    val createFileContract = rememberCreateFileContract(MimeType.JSON) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.onExportFileSelected(result.uri)
            is ContractResult.Canceled -> Unit
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
                is ThemesViewEvent.ChooseExportFile -> {
                    createFileContract.launch(event.themeName)
                }
            }
        }
    }

    NavResultEffect(KEY_SAVE) {
        viewModel.loadThemes()
    }

    CleanupEffect {
        ThemesComponent.release()
    }
}

@Composable
private fun ThemesScreen(
    viewState: ThemesViewState,
    onBackClicked: () -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onSelectClicked: (ThemeModel) -> Unit = {},
    onExportClicked: (ThemeModel) -> Unit = {},
    onEditClicked: (ThemeModel) -> Unit = {},
    onRemoveClicked: (ThemeModel) -> Unit = {},
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
                title = stringResource(R.string.label_themes),
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
                                    iconColor = SquircleTheme.colors.colorTextAndIconSecondary,
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
            // Do we really need it?
            /*AnimatedVisibility(
                visible = showButton,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingButton(
                    iconResId = UiR.drawable.ic_plus,
                    onClick = onCreateClicked,
                    modifier = Modifier.padding(8.dp)
                )
            }*/
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
                    items = viewState.themes,
                    key = ThemeModel::uuid,
                ) { theme ->
                    ThemeOverview(
                        themeModel = theme,
                        isSelected = theme.uuid == viewState.selectedTheme,
                        typeface = viewState.typeface,
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
                searchQuery = "Mono",
                themes = listOf(
                    ThemeModel(
                        uuid = "1",
                        name = "Darcula",
                        author = "Squircle CE",
                        colorScheme = EditorTheme.DARCULA,
                        isExternal = false,
                    ),
                    ThemeModel(
                        uuid = "2",
                        name = "Eclipse",
                        author = "Squircle CE",
                        colorScheme = EditorTheme.ECLIPSE,
                        isExternal = false,
                    ),
                ),
                selectedTheme = "1",
                typeface = Typeface.MONOSPACE,
                isLoading = false,
            ),
        )
    }
}