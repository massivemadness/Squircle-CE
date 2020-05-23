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

package com.lightteam.modpeide.ui.editor.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ItemTabDocumentBinding
import com.lightteam.modpeide.domain.model.editor.DocumentModel
import com.lightteam.modpeide.ui.base.adapters.TabAdapter
import com.lightteam.modpeide.utils.extensions.makeRightPaddingRecursively

class DocumentAdapter(
    onTabSelectedListener: OnTabSelectedListener,
    private val tabInteractor: TabInteractor
) : TabAdapter<DocumentModel, DocumentAdapter.DocumentViewHolder>(onTabSelectedListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        return DocumentViewHolder.create(parent, tabInteractor) {
            select(it)
        }
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    class DocumentViewHolder(
        private val binding: ItemTabDocumentBinding,
        private val tabInteractor: TabInteractor,
        private val tabCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(
                parent: ViewGroup,
                tabInteractor: TabInteractor,
                tabCallback: (Int) -> Unit
            ): DocumentViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTabDocumentBinding.inflate(inflater, parent, false)
                return DocumentViewHolder(binding, tabInteractor, tabCallback)
            }
        }

        init {
            itemView.setOnClickListener {
                tabCallback.invoke(adapterPosition)
            }
            itemView.setOnLongClickListener {
                val wrapper = ContextThemeWrapper(it.context, R.style.Widget_AppTheme_PopupMenu)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_close -> tabInteractor.close(adapterPosition)
                        R.id.action_close_others -> tabInteractor.closeOthers(adapterPosition)
                        R.id.action_close_all -> tabInteractor.closeAll(adapterPosition)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.inflate(R.menu.menu_document)
                popupMenu.makeRightPaddingRecursively()
                popupMenu.show()
                return@setOnLongClickListener true
            }
            binding.itemIcon.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    tabInteractor.close(adapterPosition)
                }
            }
        }

        fun bind(item: DocumentModel, isSelected: Boolean) {
            binding.selectionIndicator.isVisible = isSelected
            binding.itemTitle.text = item.name
        }
    }

    interface TabInteractor {
        fun close(position: Int)
        fun closeOthers(position: Int)
        fun closeAll(position: Int)
    }
}