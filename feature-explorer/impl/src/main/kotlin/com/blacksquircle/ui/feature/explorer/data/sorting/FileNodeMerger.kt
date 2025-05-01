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

import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.NodeKey

internal object FileNodeMerger {

    fun merge(
        nodes: HashMap<NodeKey, List<FileNode>>,
        fileNode: FileNode,
        showHidden: Boolean,
    ): List<FileNode> {
        var current = fileNode
        var currentKey = current.key
        val mergedList = mutableListOf(current)

        while (true) {
            val nextChildren = nodes[currentKey]
            if (
                nextChildren?.size != 1 ||
                !nextChildren[0].isDirectory ||
                (!showHidden && nextChildren[0].isHidden)
            ) break

            current = nextChildren[0]
            currentKey = current.key
            mergedList += current
        }
        return mergedList
    }
}