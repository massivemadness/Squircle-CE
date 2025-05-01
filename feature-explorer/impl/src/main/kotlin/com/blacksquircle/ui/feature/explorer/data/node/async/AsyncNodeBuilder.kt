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

package com.blacksquircle.ui.feature.explorer.data.node.async

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilder
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderOptions
import com.blacksquircle.ui.feature.explorer.data.node.NodeMap
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import kotlinx.coroutines.withContext

internal class AsyncNodeBuilder(private val dispatcherProvider: DispatcherProvider) {

    suspend fun buildNodeList(nodes: NodeMap, options: NodeBuilderOptions): List<FileNode> {
        return withContext(dispatcherProvider.io()) {
            NodeBuilder.buildNodeList(nodes, options)
        }
    }
}