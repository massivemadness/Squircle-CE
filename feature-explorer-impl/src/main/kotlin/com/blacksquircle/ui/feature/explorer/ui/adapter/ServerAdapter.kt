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

package com.blacksquircle.ui.feature.explorer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.blacksquircle.ui.core.extensions.replaceList
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.google.android.material.textview.MaterialTextView

class ServerAdapter(
    private val context: Context,
    private val addServer: () -> Unit,
) : BaseAdapter() {

    private val filesystemList = mutableListOf<FilesystemModel>()

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_filesystem, parent, false)
        val text = view.findViewById<MaterialTextView>(android.R.id.text1)
        val item = getItem(position)
        text?.text = item.title
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_dropdown, parent, false)
        val text = view.findViewById<MaterialTextView>(android.R.id.text1)
        val item = getItem(position)
        text?.text = item.title
        if (!isEnabled(position)) {
            view.setOnClickListener {
                addServer()
            }
        }
        return view
    }

    override fun areAllItemsEnabled() = false
    override fun isEnabled(position: Int): Boolean {
        return position < filesystemList.size - 1
    }

    override fun getItem(position: Int) = filesystemList[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = filesystemList.size

    fun submitList(filesystems: List<FilesystemModel>) {
        filesystemList.replaceList(filesystems)
        filesystemList.add(
            FilesystemModel(
                uuid = "null",
                title = context.getString(R.string.storage_add)
            )
        )
        notifyDataSetChanged()
    }
}