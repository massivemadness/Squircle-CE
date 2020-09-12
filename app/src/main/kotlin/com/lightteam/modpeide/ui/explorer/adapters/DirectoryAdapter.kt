/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.ui.explorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.filesystem.base.model.FileModel
import com.lightteam.modpeide.databinding.ItemTabDirectoryBinding
import com.lightteam.modpeide.ui.base.adapters.TabAdapter

class DirectoryAdapter : TabAdapter<FileModel, DirectoryAdapter.DirectoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        return DirectoryViewHolder.create(parent) { select(it) }
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
        holder.bind(currentList[position], position == selectedPosition)
    }

    class DirectoryViewHolder(
        private val binding: ItemTabDirectoryBinding,
        private val tabCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, tabCallback: (Int) -> Unit): DirectoryViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTabDirectoryBinding.inflate(inflater, parent, false)
                return DirectoryViewHolder(binding, tabCallback)
            }
        }

        init {
            itemView.setOnClickListener {
                tabCallback.invoke(adapterPosition)
            }
        }

        fun bind(item: FileModel, isSelected: Boolean) {
            binding.selectionIndicator.isVisible = isSelected
            binding.itemTitle.text = item.name
        }
    }
}