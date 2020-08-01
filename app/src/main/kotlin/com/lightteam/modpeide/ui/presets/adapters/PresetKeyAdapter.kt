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

package com.lightteam.modpeide.ui.presets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.databinding.ItemPresetKeyBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder

class PresetKeyAdapter : RecyclerView.Adapter<PresetKeyAdapter.PresetKeyViewHolder>() {

    var dataSet: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetKeyViewHolder {
        return PresetKeyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PresetKeyViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class PresetKeyViewHolder(
        private val binding: ItemPresetKeyBinding
    ) : BaseViewHolder<String>(binding.root) {

        companion object {
            fun create(parent: ViewGroup): PresetKeyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPresetKeyBinding.inflate(inflater, parent, false)
                return PresetKeyViewHolder(binding)
            }
        }

        override fun bind(item: String) {
            binding.itemTitle.text = item
        }
    }
}