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
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.R
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
        keyAdapter.keys = arrayOf('{', '}', '(', ')', ';', ',', '.', '=', '\\', '|',
            '&', '!', '[', ']', '<', '>', '+', '-', '/', '*', '?', ':', '_')
    }

    private class KeyAdapter(
        private val keyListener: OnKeyListener
    ) : RecyclerView.Adapter<KeyAdapter.KeyViewHolder>() {

        var keys: Array<Char> = arrayOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
            return KeyViewHolder.create(parent, keyListener)
        }

        override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
            holder.bind(keys[position])
        }

        override fun getItemCount(): Int {
            return keys.size
        }

        private class KeyViewHolder(
            itemView: View,
            private val keyListener: OnKeyListener
        ) : BaseViewHolder<Char>(itemView) {

            companion object {
                fun create(parent: ViewGroup, keyListener: OnKeyListener): KeyViewHolder {
                    val itemView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_key, parent, false)
                    return KeyViewHolder(itemView, keyListener)
                }
            }

            private val textView: TextView = itemView.findViewById(R.id.item_title)

            private lateinit var char: String

            init {
                itemView.setOnClickListener {
                    keyListener.onKey(char)
                }
            }

            override fun bind(item: Char) {
                char = item.toString()
                textView.text = char
            }
        }
    }

    interface OnKeyListener {
        fun onKey(char: String)
    }
}