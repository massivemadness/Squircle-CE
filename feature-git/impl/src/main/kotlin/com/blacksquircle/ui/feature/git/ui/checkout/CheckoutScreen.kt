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

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.git.R
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog.Companion.KEY_CHECKOUT
import com.blacksquircle.ui.feature.git.internal.GitComponent
import com.blacksquircle.ui.feature.git.ui.checkout.compose.BranchList

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
        onCheckoutClicked = viewModel::onCheckoutClicked,
        onBranchSelected = viewModel::onBranchSelected,
        onBranchNameChanged = viewModel::onBranchNameChanged,
        onNewBranchClicked = viewModel::onNewBranchClicked,
        onBackClicked = viewModel::onBackClicked
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
                is CheckoutViewEvent.CheckoutComplete -> {
                    context.showToast(
                        text = context.getString(
                            R.string.git_checkout_checked_out,
                            event.branchName
                        )
                    )

                    sendNavigationResult(KEY_CHECKOUT, Bundle.EMPTY)
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
private fun CheckoutScreen(
    viewState: CheckoutViewState,
    onCheckoutClicked: () -> Unit = {},
    onBranchSelected: (String) -> Unit = {},
    onBranchNameChanged: (String) -> Unit = {},
    onNewBranchClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    AlertDialog(
        title = stringResource(R.string.git_checkout_title),
        content = {
            Column {
                when {
                    viewState.isChecking -> {
                        Text(
                            text = stringResource(R.string.git_checkout_checking_out),
                            style = SquircleTheme.typography.text16Regular,
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                        )

                        Spacer(Modifier.height(16.dp))

                        LinearProgress(
                            indeterminate = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    viewState.isLoading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            CircularProgress()
                        }
                    }

                    viewState.isError -> {
                        Text(
                            text = stringResource(R.string.git_fatal, viewState.errorMessage),
                            style = SquircleTheme.typography.text16Regular,
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                        )
                    }

                    viewState.isNewBranch -> {
                        TextField(
                            inputText = viewState.newBranchName,
                            onInputChanged = onBranchNameChanged,
                            labelText = stringResource(R.string.git_checkout_branch_name),
                            helpText = stringResource(
                                R.string.git_checkout_branch_base,
                                viewState.currentBranch
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    !viewState.isNewBranch -> {
                        BranchList(
                            currentBranch = viewState.currentBranch,
                            branchList = viewState.branchList,
                            onBranchSelected = onBranchSelected,
                        )
                    }
                }

                if (!viewState.isLoading && !viewState.isChecking && !viewState.isError) {
                    Spacer(Modifier.height(8.dp))

                    CheckBox(
                        title = stringResource(R.string.action_new_branch),
                        checked = viewState.isNewBranch,
                        onClick = onNewBranchClicked,
                    )
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onBackClicked,
        onDismiss = onBackClicked,
        confirmButton = if (viewState.isNewBranch) {
            stringResource(R.string.action_create)
        } else {
            stringResource(R.string.action_checkout)
        },
        confirmButtonEnabled = viewState.isNewBranchButtonEnabled ||
            viewState.isCheckoutButtonEnabled,
        onConfirmClicked = onCheckoutClicked,
    )
}

@PreviewLightDark
@Composable
private fun CheckoutScreenPreview() {
    PreviewBackground {
        CheckoutScreen(
            viewState = CheckoutViewState(
                currentBranch = "master",
                branchList = listOf("master", "develop"),
                isNewBranch = false,
                newBranchName = "feature",
                isLoading = false,
                isChecking = false,
            ),
        )
    }
}