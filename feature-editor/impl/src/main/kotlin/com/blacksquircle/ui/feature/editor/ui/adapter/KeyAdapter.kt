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

package com.blacksquircle.ui.feature.editor.ui.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.feature.editor.databinding.ItemKeyboardKeyBinding
import com.blacksquircle.ui.feature.settings.domain.model.KeyModel
import com.blacksquircle.ui.uikit.extensions.dpToPx

class KeyAdapter(
    private val onKey: (KeyModel) -> Unit,
) : ListAdapter<KeyModel, KeyAdapter.KeyViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<KeyModel>() {
            override fun areItemsTheSame(oldItem: KeyModel, newItem: KeyModel): Boolean {
                return oldItem.value == newItem.value
            }
            override fun areContentsTheSame(oldItem: KeyModel, newItem: KeyModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
        return KeyViewHolder.create(parent, onKey)
    }

    override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class KeyViewHolder(
        private val binding: ItemKeyboardKeyBinding,
        private val onKey: (KeyModel) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onKey: (KeyModel) -> Unit): KeyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemKeyboardKeyBinding.inflate(inflater, parent, false)
                return KeyViewHolder(binding, onKey)
            }
        }

        private lateinit var keyModel: KeyModel

        init {
            itemView.setOnClickListener {
                onKey(keyModel)
            }
        }

        fun bind(item: KeyModel) {
            keyModel = item
            binding.title.text = item.display
            if (item.display.length > 1) {
                binding.title.updatePadding(bottom = 0)
                binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            } else {
                binding.title.updatePadding(bottom = 2.dpToPx())
                binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }
        }
    }
}