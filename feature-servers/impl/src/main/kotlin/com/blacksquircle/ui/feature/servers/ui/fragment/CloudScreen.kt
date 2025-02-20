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

package com.blacksquircle.ui.feature.servers.ui.fragment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.domain.model.ServerStatus
import com.blacksquircle.ui.feature.servers.ui.fragment.internal.ConnectionStatus
import com.blacksquircle.ui.feature.servers.ui.fragment.internal.ServerModel
import com.blacksquircle.ui.feature.servers.ui.viewmodel.CloudViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun CloudScreen(viewModel: CloudViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CloudScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onServerClicked = viewModel::onServerClicked,
        onAddServerClicked = viewModel::onAddServerClicked,
    )
}

@Composable
private fun CloudScreen(
    viewState: CloudViewState,
    onBackClicked: () -> Unit = {},
    onServerClicked: (ServerConfig) -> Unit = {},
    onAddServerClicked: () -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_cloud_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                PreferenceGroup(
                    title = stringResource(R.string.pref_category_servers)
                )
            }
            items(
                items = viewState.servers,
                key = { it.config.uuid },
            ) { serverModel ->
                Preference(
                    title = serverModel.config.name,
                    subtitle = serverModel.config.address,
                    bottomContent = {
                        ConnectionStatus(
                            status = serverModel.status,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    },
                    onClick = { onServerClicked(serverModel.config) },
                )
            }
            item {
                Preference(
                    title = stringResource(R.string.pref_add_server_title),
                    onClick = onAddServerClicked,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CloudScreenPreview() {
    PreviewBackground {
        CloudScreen(
            viewState = CloudViewState(
                servers = listOf(
                    ServerModel(
                        config = ServerConfig(
                            uuid = "1",
                            scheme = ServerType.FTP,
                            name = "Example",
                            address = "192.168.21.101",
                            port = 21,
                            initialDir = "/",
                            authMethod = AuthMethod.PASSWORD,
                            username = "example",
                            password = "example",
                            privateKey = null,
                            passphrase = null,
                        ),
                        status = ServerStatus.Available(1000L),
                    )
                )
            ),
        )
    }
}