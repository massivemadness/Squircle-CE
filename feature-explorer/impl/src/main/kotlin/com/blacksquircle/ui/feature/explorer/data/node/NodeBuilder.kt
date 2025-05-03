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

package com.blacksquircle.ui.feature.explorer.data.node

import com.blacksquircle.ui.feature.explorer.data.node.strategy.AppendNodeStrategy
import com.blacksquircle.ui.feature.explorer.data.node.strategy.CompactNodeStrategy
import com.blacksquircle.ui.feature.explorer.data.node.strategy.SearchNodeStrategy
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey

internal object NodeBuilder {

    fun buildNodeList(nodeMap: NodeMap, options: NodeBuilderOptions): List<FileNode> {
        val fileNodes = mutableListOf<FileNode>()
        val strategy = when {
            options.isSearching -> SearchNodeStrategy(options)
            options.compactPackages -> CompactNodeStrategy(options)
            else -> AppendNodeStrategy
        }

        fun appendNode(parent: NodeKey) {
            val children = nodeMap[parent].orEmpty()
                .applyFilter(options)
            for (child in children) {
                strategy.build(
                    parent = parent,
                    child = child,
                    nodeMap = nodeMap,
                    append = fileNodes::add,
                    recurse = ::appendNode
                )
            }
        }

        appendNode(NodeKey.Root)
        return fileNodes
    }
}