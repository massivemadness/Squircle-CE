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

package com.blacksquircle.ui.feature.editor.ui.forcesyntax

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.modifier.debounceClickable
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.radio.Radio
import com.blacksquircle.ui.ds.radio.RadioStyleDefaults
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.api.navigation.ForceSyntaxDialog
import com.blacksquircle.ui.feature.editor.domain.model.GrammarModel
import com.blacksquircle.ui.feature.editor.internal.EditorComponent
import com.blacksquircle.ui.feature.editor.ui.editor.ARG_LANGUAGE
import com.blacksquircle.ui.feature.editor.ui.editor.KEY_SELECT_LANGUAGE

@Composable
internal fun ForceSyntaxScreen(
    navArgs: ForceSyntaxDialog,
    navController: NavController,
    viewModel: ForceSyntaxViewModel = daggerViewModel { context ->
        val component = EditorComponent.buildOrGet(context)
        ForceSyntaxViewModel.ParameterizedFactory(navArgs.language).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ForceSyntaxScreen(
        viewState = viewState,
        onLanguageSelected = { grammar ->
            sendNavigationResult(
                key = KEY_SELECT_LANGUAGE,
                result = bundleOf(ARG_LANGUAGE to grammar.scopeName)
            )
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
    onLanguageSelected: (GrammarModel) -> Unit = {},
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
            LazyColumn {
                items(viewState.languages) { value ->
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .debounceClickable(
                                interactionSource = interactionSource,
                                indication = ripple(),
                                onClick = { onLanguageSelected(value) }
                            )
                            .padding(horizontal = 24.dp)
                    ) {
                        Radio(
                            title = value.displayName,
                            checked = value.scopeName == viewState.selectedLanguage,
                            onClick = { onLanguageSelected(value) },
                            radioStyle = RadioStyleDefaults.Primary.copy(
                                textStyle = SquircleTheme.typography.text18Regular,
                            ),
                            interactionSource = interactionSource,
                            indication = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
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