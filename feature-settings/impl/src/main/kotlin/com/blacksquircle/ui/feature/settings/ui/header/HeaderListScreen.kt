/*
 * Copyright 2023 Squircle CE contributors.
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.preference.PreferenceHeader
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun HeaderListScreen(viewModel: HeaderViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    HeaderListScreen(
        viewState = viewState,
        onBackClicked = viewModel::popBackStack,
        onItemClicked = viewModel::selectHeader
    )
}

@Composable
private fun HeaderListScreen(
    viewState: HeaderListState,
    onBackClicked: () -> Unit,
    onItemClicked: (Screen<*>) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.label_settings),
                backIcon = UiR.drawable.ic_back,
                onBackClicked = onBackClicked,
            )
        }
    ) { innerPadding ->
        HeaderList(
            viewState = viewState,
            contentPadding = innerPadding,
            onItemClicked = { onItemClicked(it.screen) },
        )
    }
}

@Composable
private fun HeaderList(
    viewState: HeaderListState,
    contentPadding: PaddingValues,
    onItemClicked: (PreferenceHeader) -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
    ) {
        viewState.headers.forEach { header ->
            PreferenceHeader(
                title = stringResource(header.title),
                subtitle = stringResource(header.subtitle),
                selected = false,
                onSelected = { onItemClicked(header) },
            )
        }
    }
}

@Preview
@Composable
private fun HeaderListScreenPreview() {
    SquircleTheme {
        HeaderListScreen(
            viewState = HeaderListState(),
            onBackClicked = {},
            onItemClicked = {},
        )
    }
}