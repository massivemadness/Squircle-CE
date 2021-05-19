/*
 * Copyright 2021 Squircle IDE contributors.
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

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.feature.explorer.adapters.FileAdapter

class FileKeyProvider(
    private val recyclerView: RecyclerView
) : ItemKeyProvider<String>(SCOPE_CACHED) {

    override fun getKey(position: Int): String {
        val adapter = recyclerView.adapter as FileAdapter
        return adapter.currentList[position].path
    }

    override fun getPosition(key: String): Int {
        val adapter = recyclerView.adapter as FileAdapter
        return adapter.indexOf(key)
    }
}