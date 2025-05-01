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

package com.blacksquircle.ui.feature.explorer.utils

import com.blacksquircle.ui.feature.explorer.createFile
import com.blacksquircle.ui.feature.explorer.data.utils.FileSorter
import com.blacksquircle.ui.feature.explorer.data.utils.fileComparator
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FileSorterTest {

    private val fileA = createFile(name = "apple.txt", size = 100, lastModified = 1000)
    private val fileB = createFile(name = "Banana.txt", size = 200, lastModified = 2000)
    private val fileC = createFile(name = "cherry.txt", size = 50, lastModified = 500)

    @Test
    fun `When sorted by name Then should be in alphabetical order ignoring case`() {
        // Given
        val sortMode = SortMode.SORT_BY_NAME
        val files = listOf(fileB, fileC, fileA)

        // When
        val sorted = files.sortedWith(fileComparator(sortMode))

        // Then
        assertEquals(listOf(fileA, fileB, fileC), sorted)
    }

    @Test
    fun `When sorted by size Then should be in descending order`() {
        // Given
        val files = listOf(fileA, fileB, fileC)

        // When
        val sorted = files.sortedWith(FileSorter.COMPARATOR_SIZE)

        // Then
        assertEquals(listOf(fileB, fileA, fileC), sorted)
    }

    @Test
    fun `When sorted by date Then should be in descending lastModified order`() {
        // Given
        val files = listOf(fileA, fileB, fileC)

        // When
        val sorted = files.sortedWith(FileSorter.COMPARATOR_DATE)

        // Then
        assertEquals(listOf(fileB, fileA, fileC), sorted)
    }

    @Test
    fun `When fileComparator called with name Then returns COMPARATOR_NAME`() {
        // Given
        val input = SortMode.SORT_BY_NAME.value

        // When
        val comparator = fileComparator(input)

        // Then
        val sorted = listOf(fileB, fileC, fileA).sortedWith(comparator)
        assertEquals(listOf(fileA, fileB, fileC), sorted)
    }

    @Test
    fun `When fileComparator called with size Then returns COMPARATOR_SIZE`() {
        // Given
        val input = SortMode.SORT_BY_SIZE

        // When
        val comparator = fileComparator(input)

        // Then
        val sorted = listOf(fileA, fileB, fileC).sortedWith(comparator)
        assertEquals(listOf(fileB, fileA, fileC), sorted)
    }

    @Test
    fun `When fileComparator called with date Then returns COMPARATOR_DATE`() {
        // Given
        val input = SortMode.SORT_BY_DATE

        // When
        val comparator = fileComparator(input)

        // Then
        val sorted = listOf(fileA, fileB, fileC).sortedWith(comparator)
        assertEquals(listOf(fileB, fileA, fileC), sorted)
    }
}