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

package com.lightteam.modpeide.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.R
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.databinding.ItemFileBinding
import com.lightteam.modpeide.ui.main.adapters.interfaces.RecyclerSelection
import com.lightteam.modpeide.ui.main.adapters.FileAdapter.FileViewHolder
import com.lightteam.modpeide.ui.main.adapters.utils.FileDiffCallback

class FileAdapter(private val recyclerSelection: RecyclerSelection) : RecyclerView.Adapter<FileViewHolder>() {

    private var data: List<FileModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) = holder.bind(data[position])
    override fun getItemCount(): Int = data.size

    fun setData(newList: List<FileModel>) {
        val diffCallback = FileDiffCallback(data, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        data = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class FileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding: ItemFileBinding? = DataBindingUtil.bind(itemView)

        fun bind(fileModel: FileModel) {
            binding?.fileModel = fileModel
            binding?.recyclerSelection = recyclerSelection
        }
    }
}