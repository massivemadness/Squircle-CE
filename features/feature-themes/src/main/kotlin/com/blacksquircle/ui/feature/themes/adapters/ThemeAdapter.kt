/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.themes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.data.factory.LanguageFactory
import com.blacksquircle.ui.domain.model.themes.ThemeModel
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.databinding.ItemThemeBinding
import com.blacksquircle.ui.utils.extensions.makeRightPaddingRecursively

class ThemeAdapter(
    private val actions: Actions
) : ListAdapter<ThemeModel, ThemeAdapter.ThemeViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ThemeModel>() {
            override fun areItemsTheSame(oldItem: ThemeModel, newItem: ThemeModel): Boolean {
                return oldItem.uuid == newItem.uuid
            }
            override fun areContentsTheSame(oldItem: ThemeModel, newItem: ThemeModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    var codeSnippet: Pair<String, String> = "" to ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder.create(parent, actions)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(getItem(position), codeSnippet)
    }

    class ThemeViewHolder(
        private val binding: ItemThemeBinding,
        private val actions: Actions
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, actions: Actions): ThemeViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemThemeBinding.inflate(inflater, parent, false)
                return ThemeViewHolder(binding, actions)
            }
        }

        private lateinit var themeModel: ThemeModel

        init {
            itemView.setOnClickListener {
                if (!binding.actionSelect.isEnabled) {
                    actions.selectTheme(themeModel)
                }
            }
            binding.actionSelect.setOnClickListener {
                actions.selectTheme(themeModel)
            }
            binding.actionInfo.setOnClickListener {
                actions.showInfo(themeModel)
            }
            binding.actionOverflow.setOnClickListener {
                val wrapper = ContextThemeWrapper(it.context, R.style.Widget_AppTheme_PopupMenu)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_export -> actions.exportTheme(themeModel)
                        R.id.action_edit -> actions.editTheme(themeModel)
                        R.id.action_remove -> actions.removeTheme(themeModel)
                    }
                    true
                }
                popupMenu.inflate(R.menu.menu_theme_actions)
                popupMenu.makeRightPaddingRecursively()
                popupMenu.show()
            }
        }

        fun bind(item: ThemeModel, codeSnippet: Pair<String, String>) {
            themeModel = item

            binding.itemTitle.text = item.name
            binding.itemSubtitle.text = item.author
            binding.actionOverflow.isVisible = item.isExternal

            binding.card.setCardBackgroundColor(item.colorScheme.backgroundColor)
            binding.editor.themeModel = themeModel
            binding.editor.text = codeSnippet.first
            binding.editor.doOnPreDraw {
                binding.editor.language = LanguageFactory.create(codeSnippet.second)
            }
        }
    }

    interface Actions {
        fun selectTheme(themeModel: ThemeModel)
        fun exportTheme(themeModel: ThemeModel)
        fun editTheme(themeModel: ThemeModel)
        fun removeTheme(themeModel: ThemeModel)
        fun showInfo(themeModel: ThemeModel)
    }
}