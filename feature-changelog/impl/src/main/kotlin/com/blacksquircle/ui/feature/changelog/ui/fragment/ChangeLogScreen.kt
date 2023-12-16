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

package com.blacksquircle.ui.feature.changelog.ui.fragment

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.changelog.R
import com.blacksquircle.ui.feature.changelog.domain.model.ReleaseModel
import com.blacksquircle.ui.feature.changelog.ui.composable.ReleaseInfo
import com.blacksquircle.ui.feature.changelog.ui.viewmodel.ChangeLogState
import com.blacksquircle.ui.feature.changelog.ui.viewmodel.ChangeLogViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun ChangeLogScreen(viewModel: ChangeLogViewModel) {
    val state by viewModel.changelogState.collectAsState()
    ChangeLogScreen(
        state = state,
        onBackClicked = viewModel::popBackStack
    )
}

@Composable
private fun ChangeLogScreen(
    state: ChangeLogState,
    onBackClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.label_changelog),
                backIcon = UiR.drawable.ic_back,
                onBackClicked = onBackClicked,
            )
        }
    ) { innerPadding ->
        ReleaseList(
            state = state,
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun ReleaseList(
    state: ChangeLogState,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        items(items = state.releases, key = ReleaseModel::versionName) { release ->
            ReleaseInfo(
                versionName = release.versionName,
                releaseDate = release.releaseDate,
                releaseNotes = release.releaseNotes,
            )
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
private fun ChangeLogScreenPreview() {
    SquircleTheme {
        ChangeLogScreen(
            state = ChangeLogState(
                releases = listOf(
                    ReleaseModel(
                        versionName = "v2024.1.0",
                        releaseDate = "24 Jan. 2024",
                        releaseNotes = "- New UI!\n- Improved support for tablets and foldables!",
                    )
                )
            ),
            onBackClicked = {},
        )
    }
}