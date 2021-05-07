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

package com.blacksquircle.ui.feature.settings.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.feature.settings.adapters.item.PreferenceItem
import com.blacksquircle.ui.feature.settings.databinding.ItemPreferenceBinding
import com.blacksquircle.ui.utils.adapters.OnItemClickListener

class PreferenceAdapter(
    private val onItemClickListener: OnItemClickListener<PreferenceItem>
) : ListAdapter<PreferenceItem, PreferenceAdapter.PreferenceViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PreferenceItem>() {
            override fun areItemsTheSame(oldItem: PreferenceItem, newItem: PreferenceItem): Boolean {
                return oldItem.navigationId == newItem.navigationId
            }
            override fun areContentsTheSame(oldItem: PreferenceItem, newItem: PreferenceItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        return PreferenceViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PreferenceViewHolder(
        private val binding: ItemPreferenceBinding,
        private val onItemClickListener: OnItemClickListener<PreferenceItem>
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<PreferenceItem>): PreferenceViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPreferenceBinding.inflate(inflater, parent, false)
                return PreferenceViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var preferenceItem: PreferenceItem

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(preferenceItem)
            }
        }

        fun bind(item: PreferenceItem) {
            preferenceItem = item
            binding.itemTitle.setText(item.title)
            binding.itemSubtitle.setText(item.subtitle)
        }
    }
}