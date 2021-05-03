/*
 * Copyright 2021 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.explorer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brackeys.ui.feature.explorer.R
import com.brackeys.ui.feature.explorer.databinding.ItemFileDetailedBinding
import com.brackeys.ui.feature.explorer.utils.setSelectableBackground
import com.brackeys.ui.feature.explorer.utils.setTint
import com.brackeys.ui.feature.explorer.utils.toReadableDate
import com.brackeys.ui.feature.explorer.utils.toReadableSize
import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.filesystem.base.model.FileType
import com.brackeys.ui.utils.adapters.OnItemClickListener

class DetailedViewHolder(
    private val binding: ItemFileDetailedBinding,
    private val onItemClickListener: OnItemClickListener<FileModel>
) : FileAdapter.FileViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<FileModel>): DetailedViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemFileDetailedBinding.inflate(inflater, parent, false)
            return DetailedViewHolder(binding, onItemClickListener)
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
        binding.itemSubtitle.text = fileModel.lastModified.toReadableDate(
            pattern = itemView.context.getString(R.string.explorer_date_format)
        )
        binding.itemFileLength.text = fileModel.size.toReadableSize()

        if (fileModel.isHidden) {
            binding.itemIcon.alpha = 0.45f
        } else {
            binding.itemIcon.alpha = 1f
        }

        if (fileModel.isFolder) {
            binding.itemIcon.setImageResource(R.drawable.ic_folder)
            binding.itemIcon.setTint(R.color.colorFolder)
            binding.itemFileLength.visibility = View.GONE
        } else {
            binding.itemIcon.setImageResource(R.drawable.ic_file)
            binding.itemIcon.setTint(R.color.colorFile)
            binding.itemFileLength.visibility = View.VISIBLE
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