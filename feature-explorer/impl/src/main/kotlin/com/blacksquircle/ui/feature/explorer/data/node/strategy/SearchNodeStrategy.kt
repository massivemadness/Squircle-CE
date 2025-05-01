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

package com.blacksquircle.ui.feature.explorer.data.node.strategy

import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderOptions
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderStrategy
import com.blacksquircle.ui.feature.explorer.data.node.NodeMap
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey

internal class SearchNodeStrategy(private val options: NodeBuilderOptions) : NodeBuilderStrategy {

    override fun build(
        nodeMap: NodeMap,
        parentKey: NodeKey,
        child: FileNode,
        append: (FileNode) -> Unit,
        recurse: (NodeKey) -> Unit
    ) {
        val matchResults = search(nodeMap)
        if (child.key in matchResults) {
            append(child)
            if (child.isExpanded) {
                recurse(child.key)
            }
        }
    }

    private fun search(nodeMap: NodeMap): Set<NodeKey> {
        val foundMatches = mutableSetOf<NodeKey>()

        fun matches(fileNode: FileNode): Boolean {
            return fileNode.file.name.contains(options.searchQuery, ignoreCase = true)
        }

        fun findParentKey(key: NodeKey): NodeKey? {
            for ((parent, children) in nodeMap) {
                if (children.any { it.key == key }) {
                    return parent
                }
            }
            return null
        }

        fun collectMatches(key: NodeKey) {
            val children = nodeMap[key] ?: return
            for (child in children) {
                if (matches(child)) {
                    var current: NodeKey? = key
                    while (current != null && foundMatches.add(current)) {
                        current = findParentKey(current)
                    }
                    foundMatches.add(child.key)
                }
                collectMatches(child.key)
            }
        }

        collectMatches(NodeKey.Root)

        return foundMatches
    }
}