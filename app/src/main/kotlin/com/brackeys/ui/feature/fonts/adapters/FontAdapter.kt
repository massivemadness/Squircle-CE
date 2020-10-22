/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.fonts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.brackeys.ui.databinding.ItemFontBinding
import com.brackeys.ui.domain.model.font.FontModel
import com.brackeys.ui.feature.base.adapters.BaseViewHolder
import com.brackeys.ui.utils.extensions.createTypefaceFromPath

class FontAdapter(
    private val fontInteractor: FontInteractor
) : ListAdapter<FontModel, FontAdapter.FontViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FontModel>() {
            override fun areItemsTheSame(oldItem: FontModel, newItem: FontModel): Boolean {
                return oldItem.fontPath == newItem.fontPath
            }
            override fun areContentsTheSame(oldItem: FontModel, newItem: FontModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder {
        return FontViewHolder.create(parent, fontInteractor)
    }

    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FontViewHolder(
        private val binding: ItemFontBinding,
        private val fontInteractor: FontInteractor
    ) : BaseViewHolder<FontModel>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, fontInteractor: FontInteractor): FontViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemFontBinding.inflate(inflater, parent, false)
                return FontViewHolder(binding, fontInteractor)
            }
        }

        private lateinit var fontModel: FontModel

        init {
            binding.actionSelect.setOnClickListener {
                fontInteractor.selectFont(fontModel)
            }
            binding.actionRemove.setOnClickListener {
                fontInteractor.removeFont(fontModel)
            }
            itemView.setOnClickListener {
                if (!binding.actionSelect.isEnabled) {
                    fontInteractor.selectFont(fontModel)
                }
            }
        }

        override fun bind(item: FontModel) {
            fontModel = item
            binding.itemTitle.text = item.fontName
            binding.itemContent.typeface = itemView.context.createTypefaceFromPath(item.fontPath)
            binding.itemSubtitle.isVisible = item.supportLigatures
            binding.actionRemove.isVisible = item.isExternal
        }
    }

    interface FontInteractor {
        fun selectFont(fontModel: FontModel)
        fun removeFont(fontModel: FontModel)
    }
}