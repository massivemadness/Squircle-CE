/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.servers.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.core.effect.ResultEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.domain.model.ServerStatus
import com.blacksquircle.ui.feature.servers.internal.ServersComponent
import com.blacksquircle.ui.feature.servers.ui.list.compose.ConnectionStatus
import com.blacksquircle.ui.feature.servers.ui.list.model.ServerModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType
import com.blacksquircle.ui.ds.R as UiR

internal const val KEY_SAVE = "KEY_SAVE"
internal const val KEY_DELETE = "KEY_DELETE"

@Composable
internal fun ServerListScreen(
    viewModel: ServerListViewModel = daggerViewModel { context ->
        val component = ServersComponent.buildOrGet(context)
        ServerListViewModel.Factory().also(component::inject)
    },
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ServerListScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onServerClicked = viewModel::onServerClicked,
        onCreateClicked = viewModel::onCreateClicked,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
            }
        }
    }

    ResultEffect<Unit>(KEY_SAVE) {
        viewModel.loadServers()
    }
    ResultEffect<Unit>(KEY_DELETE) {
        viewModel.loadServers()
    }
}

@Composable
private fun ServerListScreen(
    viewState: ServerListViewState,
    onBackClicked: () -> Unit = {},
    onServerClicked: (ServerConfig) -> Unit = {},
    onCreateClicked: () -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.servers_toolbar_title),
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
                    title = stringResource(R.string.servers_category_ftp_servers)
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
                    title = stringResource(R.string.servers_add_dialog_title),
                    onClick = onCreateClicked,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ServerListScreenPreview() {
    PreviewBackground {
        ServerListScreen(
            viewState = ServerListViewState(
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
                            keyId = null,
                            passphrase = null,
                        ),
                        status = ServerStatus.Available(1000L),
                    )
                )
            ),
        )
    }
}