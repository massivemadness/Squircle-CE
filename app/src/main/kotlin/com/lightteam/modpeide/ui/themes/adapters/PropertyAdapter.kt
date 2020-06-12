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

package com.lightteam.modpeide.ui.themes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.databinding.ItemPropertyBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.themes.adapters.item.PropertyItem

class PropertyAdapter(
    private val onItemClickListener: OnItemClickListener<PropertyItem>
) : ListAdapter<PropertyItem, PropertyAdapter.PropertyViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PropertyItem>() {
            override fun areItemsTheSame(oldItem: PropertyItem, newItem: PropertyItem): Boolean {
                return oldItem.propertyKey == newItem.propertyKey
            }
            override fun areContentsTheSame(oldItem: PropertyItem, newItem: PropertyItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        return PropertyViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PropertyViewHolder(
        private val binding: ItemPropertyBinding,
        private val onItemClickListener: OnItemClickListener<PropertyItem>
    ) : BaseViewHolder<PropertyItem>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<PropertyItem>): PropertyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPropertyBinding.inflate(inflater, parent, false)
                return PropertyViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var propertyItem: PropertyItem

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(propertyItem)
            }
        }

        override fun bind(item: PropertyItem) {
            propertyItem = item
            binding.itemTitle.text = item.propertyKey.key
            binding.itemSubtitle.setText(item.description)
            binding.itemColor.drawable.setTint(item.propertyValue.toColorInt())
        }
    }
}