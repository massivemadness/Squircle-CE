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
import com.blacksquircle.ui.feature.explorer.data.node.strategy.AppendNodeStrategy
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import org.junit.Assert.assertEquals
import org.junit.Test

class AppendNodeStrategyTest {

    private val strategy = AppendNodeStrategy

    @Test
    fun `When building node list Then append child and recurse when node is expanded`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder", isExpanded = true)
        val fileNode = createFileNode(name = "folder/file.txt")

        val nodeMap = hashMapOf(
            rootKey to listOf(folderNode),
            folderNode.key to listOf(fileNode),
        )

        val appended = mutableListOf<FileNode>()
        val recursed = mutableListOf<NodeKey>()

        // When
        strategy.build(
            nodeMap = nodeMap,
            parent = rootKey,
            child = folderNode,
            append = { appended += it },
            recurse = { recursed += it }
        )

        // Then
        assertEquals(listOf(folderNode), appended)
        assertEquals(listOf(folderNode.key), recursed)
    }

    @Test
    fun `When building node list Then append child and don't recurse when node is collapsed`() {
        // Given
        val rootKey = NodeKey.Root
        val folderNode = createFolderNode(name = "folder", isExpanded = false)

        val nodeMap = hashMapOf<NodeKey, List<FileNode>>(
            rootKey to listOf(folderNode),
        )

        val appended = mutableListOf<FileNode>()
        val recursed = mutableListOf<NodeKey>()

        // When
        strategy.build(
            nodeMap = nodeMap,
            parent = rootKey,
            child = folderNode,
            append = { appended += it },
            recurse = { recursed += it }
        )

        // Then
        assertEquals(listOf(folderNode), appended)
        assertEquals(emptyList<NodeKey>(), recursed)
    }
}