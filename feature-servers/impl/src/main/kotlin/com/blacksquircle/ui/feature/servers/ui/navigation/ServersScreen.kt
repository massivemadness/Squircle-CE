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

package com.blacksquircle.ui.feature.servers.ui.navigation

import com.blacksquircle.ui.core.extensions.toJsonEncoded
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.google.gson.Gson

sealed class ServersScreen(route: String) : Screen<String>(route) {

    class EditServer(serverConfig: ServerConfig) : ServersScreen(
        route = "blacksquircle://settings/cloud/edit?data=${Gson().toJsonEncoded(serverConfig)}",
    )
}