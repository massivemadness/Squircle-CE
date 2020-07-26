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

package com.lightteam.modpeide.ui.editor.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.databinding.ItemKeyboardKeyBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder

class ExtendedKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private lateinit var keyAdapter: KeyAdapter

    fun setKeyListener(keyListener: OnKeyListener) {
        keyAdapter = KeyAdapter(keyListener)
        adapter = keyAdapter
    }

    fun submitList(keys: List<String>) {
        keyAdapter.submitList(keys)
    }

    private class KeyAdapter(
        private val keyListener: OnKeyListener
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
            return KeyViewHolder.create(parent, keyListener)
        }

        override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        private class KeyViewHolder(
            private val binding: ItemKeyboardKeyBinding,
            private val keyListener: OnKeyListener
        ) : BaseViewHolder<String>(binding.root) {

            companion object {
                fun create(parent: ViewGroup, keyListener: OnKeyListener): KeyViewHolder {
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = ItemKeyboardKeyBinding.inflate(inflater, parent, false)
                    return KeyViewHolder(binding, keyListener)
                }
            }

            private lateinit var char: String

            init {
                itemView.setOnClickListener {
                    keyListener.onKey(char)
                }
            }

            override fun bind(item: String) {
                char = item
                binding.itemTitle.text = char
            }
        }
    }

    interface OnKeyListener {
        fun onKey(char: String)
    }
}