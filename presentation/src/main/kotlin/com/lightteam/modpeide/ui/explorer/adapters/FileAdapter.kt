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
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.R
import com.lightteam.modpeide.domain.model.explorer.FileModel
import com.lightteam.modpeide.domain.model.explorer.FileType
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.explorer.adapters.interfaces.ItemCallback
import com.lightteam.modpeide.ui.explorer.adapters.FileAdapter.FileViewHolder
import com.lightteam.modpeide.utils.extensions.setTint

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
        holder.bind(getItem(position))
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

        private lateinit var fileModel: FileModel

        private var itemIcon: ImageView = itemView.findViewById(R.id.item_icon)
        private var itemTitle: TextView = itemView.findViewById(R.id.item_title)

        init {
            itemView.setOnClickListener {
                itemCallback.onClick(fileModel)
            }
            itemView.setOnLongClickListener {
                itemCallback.onLongClick(fileModel)
            }
        }

        override fun bind(item: FileModel) {
            fileModel = item
            itemTitle.text = fileModel.name

            if (fileModel.isHidden) {
                itemIcon.alpha = 0.45f
            } else {
                itemIcon.alpha = 1f
            }

            if (fileModel.isFolder) {
                itemIcon.setImageResource(R.drawable.ic_folder)
                itemIcon.setTint(R.color.colorFolder)
            } else {
                itemIcon.setImageResource(R.drawable.ic_file)
                itemIcon.setTint(R.color.colorIcon)
            }

            when (fileModel.getType()) {
                FileType.ARCHIVE -> {
                    itemIcon.setImageResource(R.drawable.ic_file_archive)
                    itemIcon.setTint(R.color.colorFolder)
                }
                FileType.IMAGE -> {
                    itemIcon.setImageResource(R.drawable.ic_file_image)
                }
                FileType.AUDIO -> {
                    itemIcon.setImageResource(R.drawable.ic_file_audio)
                }
                FileType.VIDEO -> {
                    itemIcon.setImageResource(R.drawable.ic_file_video)
                }
                FileType.DEFAULT -> { /* nothing */ }
            }
        }
    }
}