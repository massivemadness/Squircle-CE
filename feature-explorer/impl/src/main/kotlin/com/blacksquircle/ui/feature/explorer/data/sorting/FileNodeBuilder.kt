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

package com.blacksquircle.ui.feature.explorer.data.sorting

import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import java.io.File

internal object FileNodeBuilder {

    fun buildFileNodes(
        nodes: HashMap<NodeKey, List<FileNode>>,
        searchQuery: String,
        showHidden: Boolean,
        sortMode: SortMode,
        foldersOnTop: Boolean,
        compactPackages: Boolean,
    ): List<FileNode> {
        val fileNodes = mutableListOf<FileNode>()
        val matchResults = FileNodeSearcher.search(nodes, searchQuery)

        fun appendNode(parentKey: NodeKey) {
            val children = nodes[parentKey] ?: return
            val sortedChildren = children
                .filter { showHidden || !it.isHidden }
                .sortedWith(fileComparator(sortMode))
                .sortedBy { it.isDirectory != foldersOnTop }

            for (child in sortedChildren) {
                val key = child.key
                when {
                    searchQuery.isNotBlank() -> {
                        if (key in matchResults) {
                            fileNodes.add(child)
                            if (child.isExpanded) {
                                appendNode(key)
                            }
                        }
                    }

                    compactPackages && child.isDirectory -> {
                        val mergeNodes = FileNodeMerger.merge(nodes, child, showHidden)
                        if (mergeNodes.size > 1) {
                            val deepestNode = mergeNodes.last()
                            fileNodes.add(
                                FileNode(
                                    file = deepestNode.file,
                                    depth = child.depth,
                                    isExpanded = deepestNode.isExpanded,
                                    isLoading = deepestNode.isLoading,
                                    errorState = deepestNode.errorState,
                                    displayName = mergeNodes.joinToString(File.separator) {
                                        it.file.name
                                    },
                                )
                            )
                            if (deepestNode.isExpanded) {
                                appendNode(deepestNode.key)
                            }
                        } else {
                            fileNodes.add(child)
                            if (child.isExpanded) {
                                appendNode(key)
                            }
                        }
                    }

                    else -> {
                        fileNodes.add(child)
                        if (child.isExpanded) {
                            appendNode(key)
                        }
                    }
                }
            }
        }

        appendNode(NodeKey.Root)
        return fileNodes
    }
}