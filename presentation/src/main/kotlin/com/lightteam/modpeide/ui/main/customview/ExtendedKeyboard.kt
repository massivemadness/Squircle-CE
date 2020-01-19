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

package com.lightteam.modpeide.ui.main.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.R

class ExtendedKeyboard(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    interface OnKeyListener {
        fun onKey(char: String)
    }

    private lateinit var keyAdapter: KeyAdapter

    fun setKeyListener(keyListener: OnKeyListener) {
        keyAdapter = KeyAdapter(keyListener)
        keyAdapter.keys = arrayOf("{", "}", "(", ")", ";", ",", ".", "=", "\\", "|",
            "&", "!", "[", "]", "<", ">", "+", "-", "/", "*", "?", ":", "_")
        adapter = keyAdapter
        adapter?.notifyDataSetChanged()
    }

    private class KeyAdapter(private val keyListener: OnKeyListener) : RecyclerView.Adapter<KeyAdapter.ViewHolder>() {
        
        var keys: Array<String> = arrayOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_key, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(keys[position])
        override fun getItemCount(): Int = keys.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val textView: TextView = itemView.findViewById(R.id.item_title)

            fun bind(char: String) {
                textView.text = char
                itemView.setOnClickListener {
                    keyListener.onKey(char)
                }
            }
        }
    }
}