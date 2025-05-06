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
import com.blacksquircle.ui.feature.explorer.data.node.applyFilter
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import java.io.File

internal class CompactNodeStrategy(private val options: NodeBuilderOptions) : NodeBuilderStrategy {

    override fun build(
        nodeMap: NodeMap,
        parent: NodeKey,
        child: FileNode,
        append: (FileNode) -> Unit,
        recurse: (NodeKey) -> Unit
    ) {
        if (child.isDirectory) {
            appendDirectory(nodeMap, child, child.depth, append)
        } else {
            append(child)
        }
    }

    private fun appendDirectory(
        nodeMap: NodeMap,
        node: FileNode,
        depth: Int,
        append: (FileNode) -> Unit
    ) {
        val nestedNodes = findNestedNodes(nodeMap, node)
        if (nestedNodes.size > 1) {
            val compactName = nestedNodes.joinToString(File.separator) { it.file.name }
            val compactNode = nestedNodes.last().copy(
                displayName = compactName,
                displayDepth = depth,
            )
            append(compactNode)
            if (compactNode.isExpanded) {
                recurseCollapsed(nodeMap, compactNode.key, depth + 1, append)
            }
        } else {
            val adjusted = node.copy(displayDepth = depth)
            append(adjusted)
            if (node.isExpanded) {
                recurseCollapsed(nodeMap, node.key, depth + 1, append)
            }
        }
    }

    private fun recurseCollapsed(
        nodeMap: NodeMap,
        parent: NodeKey,
        depth: Int,
        append: (FileNode) -> Unit
    ) {
        val children = nodeMap[parent].orEmpty()
            .applyFilter(options)
        for (child in children) {
            if (child.isDirectory) {
                appendDirectory(nodeMap, child, depth, append)
            } else {
                val adjusted = child.copy(displayDepth = depth)
                append(adjusted)
            }
        }
    }

    private fun findNestedNodes(nodeMap: NodeMap, fileNode: FileNode): List<FileNode> {
        var current = fileNode
        var currentKey = current.key
        val nestedNodes = mutableListOf(current)

        while (true) {
            val children = nodeMap[currentKey].orEmpty()
                .applyFilter(options)
            if (children.size != 1 || !children[0].isDirectory) {
                break
            }
            current = children[0]
            currentKey = current.key
            nestedNodes += current
        }
        return nestedNodes
    }
}