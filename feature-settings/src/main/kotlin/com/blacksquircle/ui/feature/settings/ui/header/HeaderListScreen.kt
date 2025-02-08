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

package com.blacksquircle.ui.feature.settings.ui.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.preference.PreferenceHeader
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun HeaderListScreen(viewModel: HeaderViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    HeaderListScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onHeaderClicked = viewModel::onHeaderClicked
    )
}

@Composable
private fun HeaderListScreen(
    viewState: HeaderListViewState,
    onBackClicked: () -> Unit = {},
    onHeaderClicked: (Screen<*>) -> Unit = {},
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.label_settings),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(contentPadding),
        ) {
            viewState.headers.forEach { header ->
                PreferenceHeader(
                    title = stringResource(header.title),
                    subtitle = stringResource(header.subtitle),
                    onClick = { onHeaderClicked(header.screen) },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HeaderListScreenPreview() {
    PreviewBackground {
        HeaderListScreen(
            viewState = HeaderListViewState(),
        )
    }
}