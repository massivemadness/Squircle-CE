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

import com.blacksquircle.ui.feature.explorer.createFolderNode
import com.blacksquircle.ui.feature.explorer.data.node.findNodeByKey
import com.blacksquircle.ui.feature.explorer.data.node.updateNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateNodeTest {

    @Test
    fun `When updating node Then node is updated`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder", isExpanded = true)
        val nodeMap = hashMapOf<NodeKey, List<FileNode>>(
            rootKey to listOf(folderNode),
        )

        // When
        nodeMap.updateNode(folderNode) {
            it.copy(isExpanded = false)
        }

        // Then
        val expected = folderNode.copy(isExpanded = false)
        val actual = nodeMap.findNodeByKey(folderNode.key)
        assertEquals(expected, actual)
    }

    @Test
    fun `When node does not exist Then nothing is updated`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder", isExpanded = true)
        val folderNode2 = createFolderNode(name = "folder2", isExpanded = false)
        val nodeMap = hashMapOf<NodeKey, List<FileNode>>(
            rootKey to listOf(folderNode),
        )

        // When
        nodeMap.updateNode(folderNode2) {
            it.copy(isExpanded = true)
        }

        // Then
        val actual = nodeMap.findNodeByKey(folderNode.key)
        assertEquals(folderNode, actual)
    }
}