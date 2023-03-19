/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.explorer.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.core.adapter.OnItemClickListener
import com.blacksquircle.ui.filesystem.base.model.FileModel

class FileAdapter(
    private val selectionTracker: SelectionTracker<String>,
    private val onItemClickListener: OnItemClickListener<FileModel>,
    private val viewMode: Int,
) : ListAdapter<FileModel, FileAdapter.FileViewHolder>(diffCallback) {

    companion object {

        private const val VIEW_MODE_COMPACT = 0
        private const val VIEW_MODE_DETAILED = 1

        private val diffCallback = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem.fileUri == newItem.fileUri
            }
            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return when (viewMode) {
            VIEW_MODE_COMPACT -> CompactViewHolder.create(parent, onItemClickListener)
            VIEW_MODE_DETAILED -> DetailedViewHolder.create(parent, onItemClickListener)
            else -> CompactViewHolder.create(parent, onItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileModel = getItem(position)
        val isSelected = selectionTracker.isSelected(fileModel.fileUri)
        holder.bind(fileModel, isSelected)
    }

    fun selection(keys: Selection<String>): List<FileModel> {
        return currentList.filter { keys.contains(it.fileUri) }
    }

    abstract class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(fileModel: FileModel, isSelected: Boolean)
    }
}