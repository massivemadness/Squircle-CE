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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.core.adapter.TabAdapter
import com.blacksquircle.ui.core.view.MaterialPopupMenu
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.databinding.ItemTabDocumentBinding
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel

class DocumentAdapter(
    private val tabInteractor: TabInteractor,
) : TabAdapter<DocumentModel, DocumentAdapter.DocumentViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<DocumentModel>() {
            override fun areItemsTheSame(oldItem: DocumentModel, newItem: DocumentModel): Boolean {
                return oldItem.uuid == newItem.uuid
            }
            override fun areContentsTheSame(oldItem: DocumentModel, newItem: DocumentModel): Boolean {
                return oldItem.modified == newItem.modified &&
                    oldItem.position == newItem.position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTabDocumentBinding.inflate(inflater, parent, false)
        return DocumentViewHolder(binding, tabInteractor)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class DocumentViewHolder(
        private val binding: ItemTabDocumentBinding,
        private val tabInteractor: TabInteractor,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var document: DocumentModel

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    select(adapterPosition)
                }
            }
            binding.itemIcon.setOnLongClickListener {
                val popupMenu = MaterialPopupMenu(it.context)
                popupMenu.setOnMenuItemClickListener { item ->
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        when (item.itemId) {
                            R.id.action_close -> tabInteractor.close(adapterPosition)
                            R.id.action_close_others -> tabInteractor.closeOthers(adapterPosition)
                            R.id.action_close_all -> tabInteractor.closeAll(adapterPosition)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.inflate(R.menu.menu_document)
                popupMenu.show(it)
                return@setOnLongClickListener true
            }
            binding.itemIcon.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    tabInteractor.close(adapterPosition)
                }
            }
        }

        fun bind(item: DocumentModel) {
            document = item
            updateSelected()
            updateModified(item.modified)
        }

        private fun updateModified(modified: Boolean) {
            binding.itemTitle.text = if (modified) "• ${document.name}" else document.name
        }

        private fun updateSelected() {
            binding.selectionIndicator.isVisible = adapterPosition == selectedPosition
        }
    }

    interface TabInteractor {
        fun close(position: Int)
        fun closeOthers(position: Int)
        fun closeAll(position: Int)
    }
}