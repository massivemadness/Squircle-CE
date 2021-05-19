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

package com.blacksquircle.ui.feature.fonts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.domain.model.fonts.FontModel
import com.blacksquircle.ui.feature.fonts.databinding.ItemFontBinding
import com.blacksquircle.ui.utils.extensions.createTypefaceFromPath

class FontAdapter(
    private val actions: Actions
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
        return FontViewHolder.create(parent, actions)
    }

    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FontViewHolder(
        private val binding: ItemFontBinding,
        private val actions: Actions
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, actions: Actions): FontViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemFontBinding.inflate(inflater, parent, false)
                return FontViewHolder(binding, actions)
            }
        }

        private lateinit var fontModel: FontModel

        init {
            binding.actionSelect.setOnClickListener {
                actions.selectFont(fontModel)
            }
            binding.actionRemove.setOnClickListener {
                actions.removeFont(fontModel)
            }
            itemView.setOnClickListener {
                if (!binding.actionSelect.isEnabled) {
                    actions.selectFont(fontModel)
                }
            }
        }

        fun bind(item: FontModel) {
            fontModel = item
            binding.itemTitle.text = item.fontName
            binding.itemContent.typeface = itemView.context.createTypefaceFromPath(item.fontPath)
            binding.itemSubtitle.isVisible = item.supportLigatures
            binding.actionRemove.isVisible = item.isExternal
        }
    }

    interface Actions {
        fun selectFont(fontModel: FontModel)
        fun removeFont(fontModel: FontModel)
    }
}