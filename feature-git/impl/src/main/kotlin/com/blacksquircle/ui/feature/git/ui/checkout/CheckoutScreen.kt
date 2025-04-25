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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.modifier.debounceClickable
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.radio.Radio
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.git.R
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog
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
        horizontalPadding = false,
        content = {
            Column {
                when {
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
                    viewState.isNewBranch -> {
                        TextField(
                            inputText = viewState.newBranchName,
                            onInputChanged = onBranchNameChanged,
                            labelText = stringResource(R.string.git_checkout_branch_name),
                            enabled = !viewState.isChecking,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                        )
                    }
                    else -> {
                        BranchList(
                            currentBranch = viewState.currentBranch,
                            branchList = viewState.branchList,
                            onBranchSelected = onBranchSelected,
                            enabled = !viewState.isChecking
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                CheckBox(
                    title = stringResource(R.string.action_new_branch),
                    checked = viewState.isNewBranch,
                    enabled = !viewState.isChecking,
                    onClick = onNewBranchClicked,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                /* if (viewState.newBranchName) {

                 } else if (viewState.isLoading) {
                     Spacer(Modifier.height(16.dp))
                     LinearProgress(
                         indeterminate = true,
                         modifier = Modifier.fillMaxWidth()
                     )
                 }*/
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
        confirmButtonEnabled = viewState.currentBranch.isNotEmpty() && !viewState.isChecking,
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
                isNewBranch = true,
            ),
        )
    }
}