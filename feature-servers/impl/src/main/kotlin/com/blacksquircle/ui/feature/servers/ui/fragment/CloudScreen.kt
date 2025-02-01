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

package com.blacksquircle.ui.feature.servers.ui.fragment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.ui.viewmodel.CloudViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun CloudScreen(viewModel: CloudViewModel) {
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
    viewState: CloudState,
    onBackClicked: () -> Unit,
    onServerClicked: (ServerConfig) -> Unit,
    onAddServerClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_cloud_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.navigationBarsPadding()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                PreferenceGroup(
                    title = stringResource(R.string.pref_category_servers)
                )
            }
            items(
                items = viewState.servers,
                key = ServerConfig::uuid,
            ) { serverConfig ->
                Preference(
                    title = serverConfig.name,
                    subtitle = serverConfig.address,
                    onClick = { onServerClicked(serverConfig) },
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

@Preview
@Composable
private fun CloudScreenPreview() {
    SquircleTheme {
        CloudScreen(
            viewState = CloudState(
                servers = listOf(
                    ServerConfig(
                        uuid = "1",
                        scheme = FTPFilesystem.FTP_SCHEME,
                        name = "Example",
                        address = "192.168.21.101",
                        port = 21,
                        initialDir = "/",
                        authMethod = AuthMethod.PASSWORD,
                        username = "example",
                        password = "example",
                        privateKey = null,
                        passphrase = null,
                    )
                )
            ),
            onBackClicked = {},
            onServerClicked = {},
            onAddServerClicked = {},
        )
    }
}