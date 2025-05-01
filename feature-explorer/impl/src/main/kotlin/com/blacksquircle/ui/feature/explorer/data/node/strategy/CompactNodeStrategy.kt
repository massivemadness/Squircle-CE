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
import com.blacksquircle.ui.feature.explorer.data.utils.fileComparator
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import java.io.File

internal class CompactNodeStrategy(private val options: NodeBuilderOptions) : NodeBuilderStrategy {

    override fun build(
        nodeMap: NodeMap,
        parentKey: NodeKey,
        child: FileNode,
        append: (FileNode) -> Unit,
        recurse: (NodeKey) -> Unit
    ) {
        if (child.isDirectory) {
            val nestedNodes = findNestedNodes(nodeMap, child)
            if (nestedNodes.size > 1) {
                val lastNode = nestedNodes.last().copy(
                    displayName = nestedNodes.joinToString(File.separator) { it.file.name },
                    displayDepth = child.depth,
                )
                append(lastNode)
                if (lastNode.isExpanded) {
                    recurseCollapsed(nodeMap, lastNode.key, child.depth + 1, append)
                }
                return
            }
        }
        append(child)
        if (child.isExpanded) {
            recurse(child.key)
        }
    }

    private fun findNestedNodes(nodeMap: NodeMap, fileNode: FileNode): List<FileNode> {
        var current = fileNode
        var currentKey = current.key
        val nestedNodes = mutableListOf(current)

        while (true) {
            val children = nodeMap[currentKey].orEmpty()
                .filter { options.showHidden || !it.isHidden }
                .sortedWith(fileComparator(options.sortMode))
                .sortedBy { it.isDirectory != options.foldersOnTop }
            if (children.size != 1 || !children[0].isDirectory) {
                break
            }

            current = children[0]
            currentKey = current.key
            nestedNodes += current
        }
        return nestedNodes
    }

    private fun recurseCollapsed(
        nodeMap: NodeMap,
        parentKey: NodeKey,
        depth: Int,
        append: (FileNode) -> Unit
    ) {
        val children = nodeMap[parentKey].orEmpty()
            .filter { options.showHidden || !it.isHidden }
            .sortedWith(fileComparator(options.sortMode))
            .sortedBy { it.isDirectory != options.foldersOnTop }
        for (child in children) {
            val adjusted = child.copy(displayDepth = depth)
            append(adjusted)
            if (child.isExpanded) {
                recurseCollapsed(nodeMap, child.key, depth + 1, append)
            }
        }
    }
}