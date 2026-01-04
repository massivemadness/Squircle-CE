/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.forcesyntax

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.ResultEventBus
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.indexOrNull
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.preference.ListSelection
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.api.navigation.ForceSyntaxRoute
import com.blacksquircle.ui.feature.editor.data.model.LanguageScope
import com.blacksquircle.ui.feature.editor.domain.model.GrammarModel
import com.blacksquircle.ui.feature.editor.internal.EditorComponent
import com.blacksquircle.ui.feature.editor.ui.editor.KEY_SELECT_LANGUAGE

@Composable
internal fun ForceSyntaxScreen(
    navArgs: ForceSyntaxRoute,
    navController: NavController,
    viewModel: ForceSyntaxViewModel = daggerViewModel { context ->
        val component = EditorComponent.buildOrGet(context)
        ForceSyntaxViewModel.ParameterizedFactory(navArgs.language).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ForceSyntaxScreen(
        viewState = viewState,
        onLanguageSelected = { scopeName ->
            ResultEventBus.sendResult(KEY_SELECT_LANGUAGE, scopeName)
            navController.popBackStack()
        },
        onCancelClicked = {
            navController.popBackStack()
        },
    )
}

@Composable
private fun ForceSyntaxScreen(
    viewState: ForceSyntaxViewState,
    onLanguageSelected: (String) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    AlertDialog(
        title = stringResource(R.string.editor_force_syntax_dialog_title),
        verticalScroll = false,
        horizontalPadding = false,
        content = {
            if (viewState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    CircularProgress()
                }
                return@AlertDialog
            }

            val selectedIndex = viewState.languages.indexOrNull {
                it.scopeName == viewState.selectedLanguage
            }
            val lazyListState = rememberLazyListState(
                initialFirstVisibleItemIndex = selectedIndex ?: 0
            )
            LazyColumn(state = lazyListState) {
                item(key = LanguageScope.TEXT) {
                    ListSelection(
                        title = stringResource(R.string.editor_force_syntax_dialog_plain_text),
                        selected = LanguageScope.TEXT == viewState.selectedLanguage,
                        onClick = { onLanguageSelected(LanguageScope.TEXT) },
                    )
                }
                items(
                    items = viewState.languages,
                    key = GrammarModel::scopeName,
                ) { value ->
                    ListSelection(
                        title = value.displayName,
                        selected = value.scopeName == viewState.selectedLanguage,
                        onClick = { onLanguageSelected(value.scopeName) },
                    )
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun ForceSyntaxScreenPreview() {
    PreviewBackground {
        ForceSyntaxScreen(
            viewState = ForceSyntaxViewState(
                languages = listOf(
                    GrammarModel("c", "C", "source.c", "/", "/", emptyMap()),
                    GrammarModel("cpp", "C++", "source.cpp", "/", "/", emptyMap()),
                    GrammarModel("csharp", "C#", "source.csharp", "/", "/", emptyMap()),
                ),
                selectedLanguage = "source.c",
                isLoading = false,
            )
        )
    }
}