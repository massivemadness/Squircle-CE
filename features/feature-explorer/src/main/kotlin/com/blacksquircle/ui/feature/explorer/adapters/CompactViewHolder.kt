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

package com.blacksquircle.ui.feature.explorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.databinding.ItemFileCompactBinding
import com.blacksquircle.ui.feature.explorer.utils.setSelectableBackground
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.utils.adapters.OnItemClickListener
import com.blacksquircle.ui.utils.extensions.setTint

class CompactViewHolder(
    private val binding: ItemFileCompactBinding,
    private val onItemClickListener: OnItemClickListener<FileModel>
) : FileAdapter.FileViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<FileModel>): CompactViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemFileCompactBinding.inflate(inflater, parent, false)
            return CompactViewHolder(binding, onItemClickListener)
        }
    }

    private lateinit var fileModel: FileModel

    init {
        itemView.setOnClickListener {
            onItemClickListener.onClick(fileModel)
        }
        itemView.setOnLongClickListener {
            onItemClickListener.onLongClick(fileModel)
        }
    }

    override fun bind(fileModel: FileModel, isSelected: Boolean) {
        this.fileModel = fileModel

        if (isSelected) {
            itemView.setBackgroundResource(R.color.colorSelection)
        } else {
            itemView.setSelectableBackground()
        }

        binding.itemTitle.text = fileModel.name

        if (fileModel.isHidden) {
            binding.itemIcon.alpha = 0.45f
        } else {
            binding.itemIcon.alpha = 1f
        }

        if (fileModel.isFolder) {
            binding.itemIcon.setImageResource(R.drawable.ic_folder)
            binding.itemIcon.setTint(R.color.colorFolder)
        } else {
            binding.itemIcon.setImageResource(R.drawable.ic_file)
            binding.itemIcon.setTint(R.color.colorFile)
        }

        when (fileModel.getType()) {
            FileType.TEXT -> {
                binding.itemIcon.setImageResource(R.drawable.ic_file_document)
            }
            FileType.ARCHIVE -> {
                binding.itemIcon.setImageResource(R.drawable.ic_file_archive)
                binding.itemIcon.setTint(R.color.colorFolder)
            }
            FileType.IMAGE -> {
                binding.itemIcon.setImageResource(R.drawable.ic_file_image)
            }
            FileType.AUDIO -> {
                binding.itemIcon.setImageResource(R.drawable.ic_file_audio)
            }
            FileType.VIDEO -> {
                binding.itemIcon.setImageResource(R.drawable.ic_file_video)
            }
            else -> Unit
        }
    }
}