/*
 * Copyright Squircle CE contributors.
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

import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey

internal class FileNodeCache {

    private val cache = HashMap<NodeKey, List<FileNode>>(128)

    fun setRoot(fileNode: FileNode) {
        cache.clear()
        cache[NodeKey.Root] = listOf(fileNode)
    }

    fun updateNode(fileNode: FileNode, transform: (FileNode) -> FileNode) {
        val parentKey = cache.findParentKey(fileNode.key) ?: return
        val parentList = cache[parentKey]?.toMutableList() ?: return

        val index = parentList.indexOfFirst { it.key == fileNode.key }
        if (index != -1) {
            parentList[index] = transform(parentList[index])
            cache[parentKey] = parentList
        }
    }

    fun removeNode(fileNode: FileNode) {
        val parentKey = cache.findParentKey(fileNode.key) ?: return
        val parentList = cache[parentKey]?.toMutableList() ?: return

        parentList.removeAll { it.key == fileNode.key }

        if (parentList.isNotEmpty()) {
            cache[parentKey] = parentList
        } else {
            cache.remove(parentKey)
        }

        if (fileNode.isDirectory) {
            val children = cache[fileNode.key] ?: return
            for (child in children) {
                removeNode(child)
            }
            cache.remove(fileNode.key)
        }
    }

    fun parentNode(fileNode: FileNode): FileNode? {
        val parentKey = cache.findParentKey(fileNode.key) ?: return null
        val parentNode = cache.findNodeByKey(parentKey) ?: return null
        return parentNode
    }

    fun ensureCommonParentKey(fileNodes: List<FileNode>): Boolean {
        if (fileNodes.isEmpty()) {
            return false
        }
        val parentKey = cache.findParentKey(fileNodes.first().key)
            ?: return false

        return fileNodes.all { node ->
            cache.findParentKey(node.key) == parentKey
        }
    }

    fun contains(key: NodeKey): Boolean {
        return cache[key] != null
    }

    fun get(key: NodeKey): List<FileNode> {
        return cache[key].orEmpty()
    }

    fun put(key: NodeKey, nodeList: List<FileNode>) {
        cache[key] = nodeList
    }

    fun getAll(): NodeMap {
        return cache
    }
}