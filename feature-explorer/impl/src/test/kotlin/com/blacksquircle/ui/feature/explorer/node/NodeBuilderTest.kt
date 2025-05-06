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
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilder
import com.blacksquircle.ui.feature.explorer.data.node.NodeBuilderOptions
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeBuilderTest {

    @Test
    fun `When compact packages disabled Then use AppendNodeStrategy`() {
        // Given
        val folder1 = createFolderNode(name = "folder1", depth = 0, isExpanded = true)
        val file1 = createFileNode(name = "folder1/.file1.txt", depth = 1)
        val file2 = createFileNode(name = "folder1/file2.txt", depth = 1)

        val nodeMap = hashMapOf(
            NodeKey.Root to listOf(folder1),
            folder1.key to listOf(file1, file2),
        )

        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = false,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = true,
            compactPackages = false,
        )

        // When
        val actual = NodeBuilder.buildNodeList(nodeMap, options)

        // Then
        val expected = listOf(folder1, file2)
        assertEquals(expected, actual)
        assertEquals(0, expected[0].displayDepth)
        assertEquals(1, expected[1].displayDepth)
    }

    @Test
    fun `When search query exists Then use SearchNodeStrategy`() {
        // Given
        val folder1 = createFolderNode(name = "src", depth = 0, isExpanded = true)
        val file1 = createFileNode(name = "src/file.kt", depth = 1)
        val file2 = createFileNode(name = "src/other.kt", depth = 1)

        val nodeMap = hashMapOf(
            NodeKey.Root to listOf(folder1),
            folder1.key to listOf(file1, file2)
        )

        val options = NodeBuilderOptions(
            searchQuery = "file",
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = true,
            compactPackages = false
        )

        // When
        val actual = NodeBuilder.buildNodeList(nodeMap, options)

        // Then
        val expected = listOf(folder1, file1)
        assertEquals(expected, actual)
        assertEquals(0, actual[0].displayDepth)
        assertEquals(1, actual[1].displayDepth)
    }

    @Test
    fun `When compact packages enabled Then use CompactNodeStrategy`() {
        // Given
        val folder1 = createFolderNode(name = "a", depth = 0, isExpanded = true)
        val folder2 = createFolderNode(name = "b", depth = 1, isExpanded = true)
        val folder3 = createFolderNode(name = "c", depth = 2, isExpanded = true)
        val file = createFileNode(name = "a/b/c/file.txt", depth = 3)

        val nodeMap = hashMapOf(
            NodeKey.Root to listOf(folder1),
            folder1.key to listOf(folder2),
            folder2.key to listOf(folder3),
            folder3.key to listOf(file)
        )

        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = false,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = true,
            compactPackages = true
        )

        // When
        val actual = NodeBuilder.buildNodeList(nodeMap, options)

        // Then
        assertEquals(2, actual.size)
        assertEquals("a/b/c", actual[0].displayName)
        assertEquals(file.file.name, actual[1].file.name)
        assertEquals(0, actual[0].displayDepth)
        assertEquals(1, actual[1].displayDepth)
    }

    @Test
    fun `When root node has no children Then return empty list`() {
        // Given
        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = true,
            compactPackages = false
        )
        val nodeMap = hashMapOf<NodeKey, List<FileNode>>()

        // When
        val actual = NodeBuilder.buildNodeList(nodeMap, options)

        // Then
        assertTrue(actual.isEmpty())
    }

    @Test
    fun `When foldersOnTop is false Then show files on top`() {
        // Given
        val folder = createFolderNode(name = "z_folder")
        val file = createFileNode(name = "a_file.txt")

        val nodeMap = hashMapOf<NodeKey, List<FileNode>>(
            NodeKey.Root to listOf(folder, file)
        )

        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = false,
            compactPackages = false
        )

        // When
        val actual = NodeBuilder.buildNodeList(nodeMap, options)

        // Then
        val expected = listOf(file, folder)
        assertEquals(expected, actual)
        assertEquals(0, expected[0].displayDepth)
        assertEquals(0, expected[1].displayDepth)
    }

    @Test
    fun `When showHidden is true Then hidden files are included`() {
        // Given
        val folder = createFolderNode(name = ".hiddenFolder", depth = 0, isExpanded = true)
        val file = createFileNode(name = ".hiddenFile.txt", depth = 1)

        val nodeMap = hashMapOf(
            NodeKey.Root to listOf(folder),
            folder.key to listOf(file)
        )

        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = true,
            compactPackages = false
        )

        // When
        val result = NodeBuilder.buildNodeList(nodeMap, options)

        // Then
        assertEquals(2, result.size)
        assertEquals(folder.file.name, result[0].file.name)
        assertEquals(file.file.name, result[1].file.name)
        assertEquals(0, result[0].displayDepth)
        assertEquals(1, result[1].displayDepth)
    }
}