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
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.R
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.databinding.ItemFileBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.explorer.adapters.interfaces.ItemCallback
import com.lightteam.modpeide.ui.explorer.adapters.FileAdapter.FileViewHolder

class FileAdapter(
    private val itemCallback: ItemCallback<FileModel>
) : ListAdapter<FileModel, FileViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem.path == newItem.path
            }
            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder.create(parent, itemCallback)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    class FileViewHolder(
        itemView: View,
        private val itemCallback: ItemCallback<FileModel>
    ): BaseViewHolder<FileModel>(itemView) {

        companion object {
            fun create(parent: ViewGroup, itemCallback: ItemCallback<FileModel>): FileViewHolder {
                val itemView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_file, parent, false)
                return FileViewHolder(itemView, itemCallback)
            }
        }

        private val binding: ItemFileBinding? = DataBindingUtil.bind(itemView)

        override fun bind(item: FileModel) {
            binding?.fileModel = item
            binding?.itemCallback = itemCallback
        }
    }
}