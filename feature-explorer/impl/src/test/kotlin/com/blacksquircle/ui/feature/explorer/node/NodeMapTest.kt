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
import com.blacksquircle.ui.feature.explorer.data.node.ensureCommonParentKey
import com.blacksquircle.ui.feature.explorer.data.node.findNodeByKey
import com.blacksquircle.ui.feature.explorer.data.node.findParentKey
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeMapTest {

    @Test
    fun `When finding parent key Then return correct parent key`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode),
            folderNode.key to listOf(fileNode)
        )

        // When
        val parentKey = nodeMap.findParentKey(fileNode.key)

        // Then
        assertEquals(folderNode.key, parentKey)
    }

    @Test
    fun `When parent not found Then return null`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val nodeMap = hashMapOf<NodeKey, List<FileNode>>(
            rootKey to listOf(folderNode)
        )

        // When
        val parentKey = nodeMap.findParentKey(NodeKey.File("nonexistent"))

        // Then
        assertNull(parentKey)
    }

    @Test
    fun `When node has multiple parents Then return the first parent found`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode1 = createFolderNode(name = "folder1")
        val folderNode2 = createFolderNode(name = "folder2")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode1, folderNode2),
            folderNode1.key to listOf(fileNode),
            folderNode2.key to listOf(fileNode)
        )

        // When
        val parentKey = nodeMap.findParentKey(fileNode.key)

        // Then
        assertTrue(parentKey == folderNode1.key || parentKey == folderNode2.key)
    }

    @Test
    fun `When finding node by key Then return correct node`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode),
            folderNode.key to listOf(fileNode)
        )

        // When
        val foundNode = nodeMap.findNodeByKey(fileNode.key)

        // Then
        assertEquals(fileNode, foundNode)
    }

    @Test
    fun `When node not found Then return null`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder")
        val nodeMap = hashMapOf<NodeKey, List<FileNode>>(
            rootKey to listOf(folderNode)
        )

        // When
        val foundNode = nodeMap.findNodeByKey(NodeKey.File("nonexistent"))

        // Then
        assertNull(foundNode)
    }

    @Test
    fun `When key exists multiple times Then return the first node found`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode1 = createFolderNode(name = "folder1")
        val folderNode2 = createFolderNode(name = "folder2")
        val fileNode = createFileNode(name = "file.txt")
        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode1, folderNode2),
            folderNode1.key to listOf(fileNode),
            folderNode2.key to listOf(fileNode)
        )

        // When
        val foundNode = nodeMap.findNodeByKey(fileNode.key)

        // Then
        assertEquals(fileNode, foundNode)
    }

    @Test
    fun `When fileNodes list is empty Then return false`() {
        // Given
        val nodeMap = hashMapOf<NodeKey, List<FileNode>>()
        val fileNodes = emptyList<FileNode>()

        // When
        val result = nodeMap.ensureCommonParentKey(fileNodes)

        // Then
        assertFalse(result)
    }

    @Test
    fun `When fileNodes are in the same parent Then return true`() {
        // Given
        val parentKey = NodeKey.File("parent")
        val fileNode1 = createFileNode(name = "file1.txt")
        val fileNode2 = createFileNode(name = "file2.txt")

        val nodeMap = hashMapOf<NodeKey, List<FileNode>>()
        nodeMap[parentKey] = listOf(fileNode1, fileNode2)

        // When
        val result = nodeMap.ensureCommonParentKey(listOf(fileNode1, fileNode2))

        // Then
        assertTrue(result)
    }

    @Test
    fun `When fileNodes are in different parents Then return false`() {
        // Given
        val parentKey1 = NodeKey.File("parent1")
        val parentKey2 = NodeKey.File("parent2")

        val fileNode1 = createFileNode(name = "file1.txt")
        val fileNode2 = createFileNode(name = "file2.txt")

        val nodeMap = hashMapOf<NodeKey, List<FileNode>>()
        nodeMap[parentKey1] = listOf(fileNode1)
        nodeMap[parentKey2] = listOf(fileNode2)

        // When
        val result = nodeMap.ensureCommonParentKey(listOf(fileNode1, fileNode2))

        // Then
        assertFalse(result)
    }
}