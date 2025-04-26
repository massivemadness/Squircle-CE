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

package com.blacksquircle.ui.feature.settings.ui.git

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import com.blacksquircle.ui.feature.settings.ui.git.compose.GitCredentialsScreen
import com.blacksquircle.ui.feature.settings.ui.git.compose.GitUserScreen
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun GitHeaderScreen(
    navController: NavController,
    viewModel: GitHeaderViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        GitHeaderViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    GitHeaderScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onCredentialsChanged = viewModel::onCredentialsChanged,
        onUserChanged = viewModel::onUserChanged,
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
private fun GitHeaderScreen(
    viewState: GitHeaderViewState,
    onBackClicked: () -> Unit = {},
    onCredentialsChanged: (String, String) -> Unit = { _, _ -> },
    onUserChanged: (String, String) -> Unit = { _, _ -> }
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_git_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.pref_category_account)
            )

            var credentialsDialogShown by rememberSaveable { mutableStateOf(false) }
            if (credentialsDialogShown) {
                GitCredentialsScreen(
                    initialUsername = viewState.credentialsUsername,
                    initialPassword = viewState.credentialsPassword,
                    onConfirmClicked = { username, password ->
                        onCredentialsChanged(username, password)
                        credentialsDialogShown = false
                    },
                    onDismissClicked = { credentialsDialogShown = false },
                )
            }
            Preference(
                title = stringResource(R.string.pref_git_credentials_title),
                subtitle = stringResource(R.string.pref_git_credentials_summary),
                onClick = { credentialsDialogShown = true },
            )

            var userDialogShown by rememberSaveable { mutableStateOf(false) }
            if (userDialogShown) {
                GitUserScreen(
                    initialName = viewState.userName,
                    initialEmail = viewState.userEmail,
                    onConfirmClicked = { name, email ->
                        onUserChanged(name, email)
                        userDialogShown = false
                    },
                    onDismissClicked = { userDialogShown = false },
                )
            }
            Preference(
                title = stringResource(R.string.pref_git_user_title),
                subtitle = stringResource(R.string.pref_git_user_summary),
                onClick = { userDialogShown = true },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun GitHeaderScreenPreview() {
    PreviewBackground {
        GitHeaderScreen(
            viewState = GitHeaderViewState(
                credentialsUsername = "",
                credentialsPassword = "",
                userEmail = "",
                userName = ""
            )
        )
    }
}