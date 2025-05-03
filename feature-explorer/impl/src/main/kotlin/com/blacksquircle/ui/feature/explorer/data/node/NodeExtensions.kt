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

import com.blacksquircle.ui.feature.explorer.data.utils.fileComparator
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey

internal fun List<FileNode>.applyFilter(options: NodeBuilderOptions): List<FileNode> {
    return filter { options.showHidden || !it.isHidden }
        .sortedWith(fileComparator(options.sortMode))
        .sortedBy { it.isDirectory != options.foldersOnTop }
}

internal fun NodeMap.findParentKey(key: NodeKey): NodeKey? {
    for ((parent, children) in this) {
        if (children.any { it.key == key }) {
            return parent
        }
    }
    return null
}

internal fun NodeMap.findNodeByKey(key: NodeKey): FileNode? {
    return values.flatten().find { it.key == key }
}

internal fun NodeMap.updateNode(fileNode: FileNode, transform: (FileNode) -> FileNode) {
    val parentKey = findParentKey(fileNode.key) ?: return
    val parentList = this[parentKey]?.toMutableList() ?: return

    val index = parentList.indexOfFirst { it.key == fileNode.key }
    if (index != -1) {
        parentList[index] = transform(parentList[index])
        this[parentKey] = parentList
    }
}

internal fun NodeMap.removeNode(fileNode: FileNode) {
    val parentKey = findParentKey(fileNode.key) ?: return
    val parentList = this[parentKey]?.toMutableList() ?: return

    parentList.removeAll { it.key == fileNode.key }

    if (parentList.isNotEmpty()) {
        this[parentKey] = parentList
    } else {
        remove(parentKey)
    }

    if (fileNode.isDirectory) {
        val children = this[fileNode.key] ?: return
        for (child in children) {
            removeNode(child)
        }
        remove(fileNode.key)
    }
}

internal fun NodeMap.ensureCommonParentKey(fileNodes: List<FileNode>): Boolean {
    if (fileNodes.isEmpty()) {
        return false
    }
    val parentKey = findParentKey(fileNodes.first().key)
        ?: return false
    return fileNodes.all { node ->
        findParentKey(node.key) == parentKey
    }
}