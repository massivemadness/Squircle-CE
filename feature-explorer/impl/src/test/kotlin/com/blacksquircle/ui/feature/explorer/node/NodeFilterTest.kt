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
import com.blacksquircle.ui.feature.explorer.data.node.applyFilter
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import org.junit.Assert.assertEquals
import org.junit.Test

class NodeFilterTest {

    @Test
    fun `When showHidden is false Then hidden files are excluded`() {
        // Given
        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = false,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = false,
            compactPackages = false
        )
        val fileNodes = listOf(
            createFileNode(name = ".hidden.txt"),
            createFileNode(name = "visible.txt"),
        )

        // When
        val actual = fileNodes.applyFilter(options)

        // Then
        val expected = listOf(
            createFileNode(name = "visible.txt"),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `When showHidden is true Then hidden files are included`() {
        // Given
        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = false,
            compactPackages = false
        )
        val fileNodes = listOf(
            createFileNode(name = ".hidden.txt"),
            createFileNode(name = "visible.txt"),
        )

        // When
        val actual = fileNodes.applyFilter(options)

        // Then
        assertEquals(fileNodes, actual)
    }

    @Test
    fun `When foldersOnTop is true Then show folders on top`() {
        // Given
        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = true,
            compactPackages = false
        )
        val fileNodes = listOf(
            createFileNode("b.txt"),
            createFolderNode("a_folder"),
            createFileNode("a.txt"),
        )

        // When
        val actual = fileNodes.applyFilter(options)

        // Then
        val expected = listOf(
            createFolderNode("a_folder"),
            createFileNode("a.txt"),
            createFileNode("b.txt"),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `When foldersOnTop is false Then show files on top`() {
        // Given
        val options = NodeBuilderOptions(
            searchQuery = "",
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            foldersOnTop = false,
            compactPackages = false
        )
        val fileNodes = listOf(
            createFileNode("b.txt"),
            createFolderNode("a_folder"),
            createFileNode("a.txt")
        )

        // When
        val actual = fileNodes.applyFilter(options)

        // Then
        val expected = listOf(
            createFileNode("a.txt"),
            createFileNode("b.txt"),
            createFolderNode("a_folder"),
        )
        assertEquals(expected, actual)
    }
}