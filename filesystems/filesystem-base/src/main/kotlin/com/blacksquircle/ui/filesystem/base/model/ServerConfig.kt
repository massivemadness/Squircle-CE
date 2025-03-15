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

package com.blacksquircle.ui.filesystem.base.model

data class ServerConfig(
    val uuid: String,
    val scheme: ServerType,
    val name: String,
    val address: String,
    val port: Int,
    val initialDir: String,
    val authMethod: AuthMethod,
    val username: String,
    val password: String?,
    val keyId: String?,
    val passphrase: String?,
)