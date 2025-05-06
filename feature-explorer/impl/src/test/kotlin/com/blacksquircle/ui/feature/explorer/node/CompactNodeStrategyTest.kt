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
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderOptions
import com.blacksquircle.ui.feature.explorer.data.node.strategy.CompactNodeStrategy
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import org.junit.Assert.assertEquals
import org.junit.Test

class CompactNodeStrategyTest {

    private val options = NodeBuilderOptions(
        searchQuery = "",
        showHidden = true,
        sortMode = SortMode.SORT_BY_NAME,
        foldersOnTop = true,
        compactPackages = true,
    )
    private val strategy = CompactNodeStrategy(options)

    @Test
    fun `When folder has single child Then compact and append collapsed node`() {
        // Given
        val rootKey = NodeKey.Root
        val folderA = createFolderNode(name = "a", isExpanded = false)
        val folderB = createFolderNode(name = "a/b", isExpanded = false)
        val folderC = createFolderNode(name = "a/b/c", isExpanded = false)

        val nodeMap = hashMapOf(
            rootKey to listOf(folderA),
            folderA.key to listOf(folderB),
            folderB.key to listOf(folderC),
        )

        val appended = mutableListOf<FileNode>()
        val recursed = mutableListOf<NodeKey>()

        // When
        strategy.build(
            nodeMap = nodeMap,
            parent = rootKey,
            child = folderA,
            append = { appended += it },
            recurse = { recursed += it }
        )

        // Then
        val fileNode = appended.first()
        assertEquals("a/b/c", fileNode.displayName)
        assertEquals(true, fileNode.isDirectory)

        assertEquals(1, appended.size)
        assertEquals(folderA.depth, fileNode.displayDepth)

        assertEquals(0, recursed.size)
    }

    @Test
    fun `When folder has multiple children Then don't compact and recurse if expanded`() {
        // Given
        val rootKey = NodeKey.Root
        val folderA = createFolderNode(name = "a", isExpanded = true)
        val folderB = createFolderNode(name = "a/b", isExpanded = false)
        val folderC = createFolderNode(name = "a/c", isExpanded = false)

        val nodeMap = hashMapOf(
            rootKey to listOf(folderA),
            folderA.key to listOf(folderB, folderC),
        )

        val appended = mutableListOf<FileNode>()
        val recursed = mutableListOf<NodeKey>()

        // When
        strategy.build(
            nodeMap = nodeMap,
            parent = rootKey,
            child = folderA,
            append = { appended += it },
            recurse = { recursed += it }
        )

        // Then
        assertEquals(
            listOf(
                folderA,
                folderB.copy(displayDepth = 1),
                folderC.copy(displayDepth = 1)
            ),
            appended
        )
        assertEquals(emptyList<FileNode>(), recursed)
    }

    @Test
    fun `When compacted node is expanded Then append its children`() {
        // Given
        val rootKey = NodeKey.Root
        val folderA = createFolderNode(name = "a", isExpanded = true)
        val folderB = createFolderNode(name = "a/b", isExpanded = true)
        val fileC = createFileNode(name = "a/b/c.txt")

        val nodeMap = hashMapOf(
            rootKey to listOf(folderA),
            folderA.key to listOf(folderB),
            folderB.key to listOf(fileC),
        )

        val appended = mutableListOf<FileNode>()
        val recursed = mutableListOf<NodeKey>()

        // When
        strategy.build(
            nodeMap = nodeMap,
            parent = rootKey,
            child = folderA,
            append = { appended += it },
            recurse = { recursed += it }
        )

        // Then
        assertEquals(2, appended.size)
        val collapsed = appended[0]
        val collapsedChild = appended[1]
        assertEquals("a/b", collapsed.displayName)
        assertEquals(folderA.depth, collapsed.displayDepth)
        assertEquals(folderB.depth + 1, collapsedChild.displayDepth)
        assertEquals(fileC.key, collapsedChild.key)
    }

    @Test
    fun `When compacted node has nested folders Then nested folders are compacted too`() {
        // Given
        val rootKey = NodeKey.Root
        val folderA = createFolderNode(name = "a", isExpanded = true)
        val folderB = createFolderNode(name = "a/b", isExpanded = true)
        val folderC = createFolderNode(name = "a/b/c", isExpanded = true)
        val fileD = createFileNode(name = "a/b/c/d.txt")

        val folderDE = createFolderNode(name = "a/b/c/d", isExpanded = true)
        val folderEF = createFolderNode(name = "a/b/c/d/e", isExpanded = true)
        val folderF = createFolderNode(name = "a/b/c/d/e/f", isExpanded = true)
        val fileG = createFileNode(name = "a/b/c/d/e/f/g.txt")

        val nodeMap = hashMapOf(
            rootKey to listOf(folderA),
            folderA.key to listOf(folderB),
            folderB.key to listOf(folderC),
            folderC.key to listOf(fileD, folderDE),
            folderDE.key to listOf(folderEF),
            folderEF.key to listOf(folderF),
            folderF.key to listOf(fileG),
        )

        val appended = mutableListOf<FileNode>()
        val recursed = mutableListOf<NodeKey>()

        // When
        strategy.build(
            nodeMap = nodeMap,
            parent = rootKey,
            child = folderA,
            append = { appended += it },
            recurse = { recursed += it }
        )

        // Then
        assertEquals(4, appended.size)

        val collapsedABC = appended[0]
        val collapsedDEF = appended[1]
        val fileGNode = appended[2]
        val fileDNode = appended[3]

        assertEquals("a/b/c", collapsedABC.displayName)
        assertEquals(folderA.depth, collapsedABC.displayDepth)

        assertEquals("d/e/f", collapsedDEF.displayName)
        assertEquals(fileDNode.displayDepth, collapsedDEF.displayDepth)

        assertEquals(fileG.key, fileGNode.key)

        assertEquals(fileD.key, fileDNode.key)
        assertEquals(collapsedABC.displayDepth + 1, fileDNode.displayDepth)
    }
}