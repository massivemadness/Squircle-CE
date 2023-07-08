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

package com.blacksquircle.ui.feature.settings.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.core.adapter.OnItemClickListener
import com.blacksquircle.ui.core.extensions.getColorAttr
import com.blacksquircle.ui.core.extensions.setActivatedBackground
import com.blacksquircle.ui.core.extensions.setSelectableBackground
import com.blacksquircle.ui.feature.settings.databinding.ItemPreferenceBinding
import com.google.android.material.R as MtrlR

class PreferenceAdapter(
    private val onItemClickListener: OnItemClickListener<PreferenceHeader>,
) : ListAdapter<PreferenceHeader, PreferenceAdapter.PreferenceViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PreferenceHeader>() {
            override fun areItemsTheSame(oldItem: PreferenceHeader, newItem: PreferenceHeader): Boolean {
                return oldItem.screen == newItem.screen
            }
            override fun areContentsTheSame(oldItem: PreferenceHeader, newItem: PreferenceHeader): Boolean {
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
        private val onItemClickListener: OnItemClickListener<PreferenceHeader>,
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<PreferenceHeader>): PreferenceViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPreferenceBinding.inflate(inflater, parent, false)
                return PreferenceViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var preferenceHeader: PreferenceHeader

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(preferenceHeader)
            }
        }

        fun bind(item: PreferenceHeader) {
            preferenceHeader = item
            binding.itemTitle.text = item.title
            binding.itemSubtitle.text = item.subtitle
            binding.root.isActivated = item.selected
            if (item.selected) {
                binding.itemTitle.setTextColor(
                    itemView.context.getColorAttr(MtrlR.attr.colorOnPrimary)
                )
                binding.itemSubtitle.setTextColor(
                    itemView.context.getColorAttr(MtrlR.attr.colorOnPrimary)
                )
                binding.root.setActivatedBackground()
            } else {
                binding.itemTitle.setTextColor(
                    itemView.context.getColorAttr(android.R.attr.textColorPrimary)
                )
                binding.itemSubtitle.setTextColor(
                    itemView.context.getColorAttr(android.R.attr.textColorSecondary)
                )
                binding.root.setSelectableBackground()
            }
        }
    }
}