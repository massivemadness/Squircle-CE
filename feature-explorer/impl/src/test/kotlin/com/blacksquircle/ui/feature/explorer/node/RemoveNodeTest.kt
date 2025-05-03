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

package com.blacksquircle.ui.feature.explorer.node

import com.blacksquircle.ui.feature.explorer.createFileNode
import com.blacksquircle.ui.feature.explorer.createFolderNode
import com.blacksquircle.ui.feature.explorer.data.node.findNodeByKey
import com.blacksquircle.ui.feature.explorer.data.node.removeNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveNodeTest {

    @Test
    fun `When removing node Then node is removed`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode),
            folderNode.key to listOf(fileNode)
        )

        // When
        nodeMap.removeNode(fileNode)

        // Then
        val removedNode = nodeMap.findNodeByKey(fileNode.key)
        assertNull(removedNode)
    }

    @Test
    fun `When removing node with no children Then remove only the node`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode),
            folderNode.key to listOf(fileNode)
        )

        // When
        nodeMap.removeNode(fileNode)

        // Then
        assertEquals(listOf(folderNode), nodeMap[rootKey])
    }

    @Test
    fun `When removing node with children Then remove node and its children`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val subFolderNode = createFolderNode(name = "subfolder")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode),
            folderNode.key to listOf(subFolderNode),
            subFolderNode.key to listOf(fileNode)
        )

        // When
        nodeMap.removeNode(folderNode)

        // Then
        assertNull(nodeMap.findNodeByKey(folderNode.key))
        assertNull(nodeMap.findNodeByKey(subFolderNode.key))
        assertNull(nodeMap.findNodeByKey(fileNode.key))
    }

    @Test
    fun `When removing empty folder Then remove node`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val nodeMap = hashMapOf<NodeKey, List<FileNode>>(
            rootKey to listOf(folderNode)
        )

        // When
        nodeMap.removeNode(folderNode)

        // Then
        assertNull(nodeMap.findNodeByKey(folderNode.key))
        assertTrue(nodeMap[rootKey].isNullOrEmpty())
    }

    @Test
    fun `When removing node Then remove node without affecting other nodes`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode),
            folderNode.key to listOf(fileNode)
        )

        // When
        nodeMap.removeNode(fileNode)

        // Then
        assertNull(nodeMap.findNodeByKey(fileNode.key))
        assertEquals(listOf(folderNode), nodeMap[rootKey])
    }
}