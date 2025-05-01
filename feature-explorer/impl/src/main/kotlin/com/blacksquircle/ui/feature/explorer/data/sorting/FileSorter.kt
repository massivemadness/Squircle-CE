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

package com.blacksquircle.ui.feature.explorer.data.sorting

import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import kotlin.Comparator

internal object FileSorter {

    val COMPARATOR_NAME: Comparator<in FileNode>
        get() = Comparator { first, second ->
            first.file.name.compareTo(second.file.name, ignoreCase = true)
        }

    val COMPARATOR_SIZE: Comparator<in FileNode>
        get() = Comparator { first, second ->
            second.file.size.compareTo(first.file.size)
        }

    val COMPARATOR_DATE: Comparator<in FileNode>
        get() = Comparator { first, second ->
            second.file.lastModified.compareTo(first.file.lastModified)
        }
}

internal fun fileComparator(value: String): Comparator<in FileNode> {
    return when (SortMode.of(value)) {
        SortMode.SORT_BY_NAME -> FileSorter.COMPARATOR_NAME
        SortMode.SORT_BY_SIZE -> FileSorter.COMPARATOR_SIZE
        SortMode.SORT_BY_DATE -> FileSorter.COMPARATOR_DATE
    }
}