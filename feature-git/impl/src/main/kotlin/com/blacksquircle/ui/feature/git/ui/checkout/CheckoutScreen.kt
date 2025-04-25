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

package com.blacksquircle.ui.feature.git.ui.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.git.R
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog
import com.blacksquircle.ui.feature.git.internal.GitComponent

@Composable
internal fun CheckoutScreen(
    navArgs: CheckoutDialog,
    navController: NavController,
    viewModel: CheckoutViewModel = daggerViewModel { context ->
        val component = GitComponent.buildOrGet(context)
        CheckoutViewModel.ParameterizedFactory(navArgs.repository).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CheckoutScreen(
        viewState = viewState,
        getBranches = viewModel::getBranches,
        onInputChanged = viewModel::onInputChanged,
        onCheckoutClicked = viewModel::onCheckoutClicked,
        onBackClicked = viewModel::onBackClicked
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }
}

@Composable
private fun CheckoutScreen(
    viewState: CheckoutViewState,
    getBranches: (String) -> Unit = {},
    onInputChanged: (String) -> Unit = {},
    onCheckoutClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    AlertDialog(
        title = stringResource(R.string.git_checkout_title),
        content = {
            Column {
                if (viewState.showListOfBranches) {
                    val branches = getBranches
                    itemsIndexed(branches) { index, value ->
                        val interactionSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .debounceClickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(),
                                    onClick = { viewState.checkoutBranch = value }
                                )
                                .padding(horizontal = 24.dp)
                        ) {
                            Radio(
                                title = branches[index],
                                checked = value == selectedValue,
                                onClick = { viewState.checkoutBranch = value },
                                textStyle = SquircleTheme.typography.text18Regular,
                                interactionSource = interactionSource,
                                indication = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .debounceClickable(
                                indication = ripple(),
                                onClick = {
                                    viewState.showListOfBranches = false,
                                    viewState.showBranchInput = true
                                }
                            )
                            .padding(horizontal = 24.dp)
                    ) {
                        Radio(
                            title = branches[index],
                            checked = value == selectedValue,
                            onClick = {
                                viewState.showListOfBranches = false,
                                viewState.showBranchInput = true
                            }
                            textStyle = SquircleTheme.typography.text18Regular,
                            indication = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                } else {
                    Text(
                        text = when {
                            viewState.showBranchInput -> {
                                stringResource(R.string.git_checkout_dialog_input_message)
                            }
                            viewState.isLoading -> {
                                stringResource(R.string.git_checkout_dialog_message)
                            }
                            viewState.isError -> {
                                stringResource(R.string.git_fatal, viewState.errorMessage)
                            }
                            else -> {
                                stringResource(R.string.git_checkout_dialog_complete)
                            }
                        },
                        color = SquircleTheme.colors.colorTextAndIconSecondary,
                        style = SquircleTheme.typography.text14Regular,
                    )
                }

                if (viewState.showBranchInput) {
                    Spacer(Modifier.height(16.dp))
                    TextField(
                        inputText = viewState.checkoutBranch,
                        modifier = Modifier.fillMaxWidth(),
                        onInputChanged = onInputChanged
                    )
                } else if (viewState.isLoading) {
                    Spacer(Modifier.height(16.dp))
                    LinearProgress(
                        indeterminate = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onBackClicked,
        onDismiss = onBackClicked,
        confirmButton = if (viewState.showBranchInput || viewState.showListOfBranches) stringResource(android.R.string.ok) else null,
        confirmButtonEnabled = viewState.checkoutBranch.isNotEmpty(),
        onConfirmClicked = onCheckoutClicked
    )
}

@PreviewLightDark
@Composable
private fun CheckoutScreenPreview() {
    PreviewBackground {
        CheckoutScreen(
            viewState = CheckoutViewState(),
        )
    }
}