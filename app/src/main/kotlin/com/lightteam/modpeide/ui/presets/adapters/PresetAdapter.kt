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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.databinding.ItemPresetBinding
import com.lightteam.modpeide.domain.model.preset.PresetModel
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.utils.extensions.isUltimate

class PresetAdapter(
    private val presetInteractor: PresetInteractor
) : ListAdapter<PresetModel, PresetAdapter.PresetViewHolder>(diffCallback) {

    companion object {
        private val sharedViewPool = RecyclerView.RecycledViewPool()

        private val diffCallback = object : DiffUtil.ItemCallback<PresetModel>() {
            override fun areItemsTheSame(oldItem: PresetModel, newItem: PresetModel): Boolean {
                return oldItem.uuid == newItem.uuid
            }
            override fun areContentsTheSame(oldItem: PresetModel, newItem: PresetModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        return PresetViewHolder.create(parent, presetInteractor)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PresetViewHolder(
        private val binding: ItemPresetBinding,
        private val presetInteractor: PresetInteractor
    ) : BaseViewHolder<PresetModel>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, presetInteractor: PresetInteractor): PresetViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPresetBinding.inflate(inflater, parent, false)
                return PresetViewHolder(binding, presetInteractor)
            }
        }

        private val adapter = PresetKeyAdapter()

        private lateinit var presetModel: PresetModel

        init {
            binding.itemContent.setHasFixedSize(true)
            binding.itemContent.setRecycledViewPool(sharedViewPool)
            binding.itemContent.adapter = adapter

            binding.actionSelect.setOnClickListener {
                presetInteractor.selectPreset(presetModel)
            }
            binding.actionEdit.setOnClickListener {
                presetInteractor.editPreset(presetModel)
            }
            binding.actionRemove.setOnClickListener {
                presetInteractor.removePreset(presetModel)
            }
        }

        override fun bind(item: PresetModel) {
            presetModel = item
            binding.itemTitle.text = item.name
            binding.actionSelect.isEnabled = !item.isExternal || isUltimate()
            binding.actionEdit.isVisible = item.isExternal
            binding.actionRemove.isVisible = item.isExternal
            adapter.dataSet = item.keys
        }
    }

    interface PresetInteractor {
        fun selectPreset(presetModel: PresetModel)
        fun editPreset(presetModel: PresetModel)
        fun removePreset(presetModel: PresetModel)
    }
}