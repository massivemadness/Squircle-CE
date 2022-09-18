/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.explorer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.replaceList
import com.blacksquircle.ui.filesystem.base.model.ServerModel

class ServerAdapter(
    private val context: Context,
    private val addServer: () -> Unit
) : BaseAdapter() {

    private val dataset = mutableListOf<CharSequence>()
    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_filesystem, parent, false)
        val text = view.findViewById<TextView>(android.R.id.text1)
        val item = getItem(position)
        text?.text = item
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_dropdown, parent, false)
        val text = view.findViewById<TextView>(android.R.id.text1)
        val item = getItem(position)
        text?.text = item
        if (!isEnabled(position)) {
            view.setOnClickListener {
                addServer()
            }
        }
        return view
    }

    override fun areAllItemsEnabled() = false
    override fun isEnabled(position: Int): Boolean {
        return position < dataset.size - 1
    }

    override fun getItem(position: Int) = dataset[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = dataset.size

    fun submitList(servers: List<ServerModel>) {
        dataset.replaceList(mutableListOf(
            context.getString(R.string.storage_local),
            context.getString(R.string.storage_root),
        ))
        dataset.addAll(servers.map(ServerModel::name))
        dataset.add(context.getString(R.string.storage_add))
        notifyDataSetChanged()
    }
}