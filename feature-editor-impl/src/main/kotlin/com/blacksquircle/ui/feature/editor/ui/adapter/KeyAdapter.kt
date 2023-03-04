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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.feature.editor.databinding.ItemKeyboardKeyBinding

class KeyAdapter(
    private val onKey: (String) -> Unit,
) : ListAdapter<String, KeyAdapter.KeyViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
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
        private val onKey: (String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onKey: (String) -> Unit): KeyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemKeyboardKeyBinding.inflate(inflater, parent, false)
                return KeyViewHolder(binding, onKey)
            }
        }

        private lateinit var char: String

        init {
            itemView.setOnClickListener {
                onKey(char)
            }
        }

        fun bind(item: String) {
            char = item
            binding.itemTitle.text = char
        }
    }
}