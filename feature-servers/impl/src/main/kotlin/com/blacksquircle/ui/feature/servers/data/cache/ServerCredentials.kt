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

package com.blacksquircle.ui.feature.servers.data.cache

internal object ServerCredentials {

    private val credentials = HashMap<String, String>()

    fun put(uuid: String, password: String) {
        credentials[uuid] = password
    }

    fun get(uuid: String): String? {
        return credentials[uuid]
    }

    fun remove(uuid: String) {
        credentials.remove(uuid)
    }
}