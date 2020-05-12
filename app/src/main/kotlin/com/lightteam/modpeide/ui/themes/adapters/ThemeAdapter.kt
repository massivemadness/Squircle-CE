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

package com.lightteam.modpeide.ui.themes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.feature.language.LanguageProvider
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.modpeide.databinding.ItemThemeBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.themes.customview.CodeView
import com.lightteam.modpeide.utils.extensions.isUltimate
import com.lightteam.modpeide.utils.extensions.makeRightPaddingRecursively

class ThemeAdapter(
    private val themeInteractor: ThemeInteractor
) : ListAdapter<Theme, ThemeAdapter.ThemeViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Theme>() {
            override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean {
                return oldItem.uuid == newItem.uuid
            }
            override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder.create(parent, themeInteractor)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ThemeViewHolder(
        private val binding: ItemThemeBinding,
        private val themeInteractor: ThemeInteractor
    ) : BaseViewHolder<Theme>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, themeInteractor: ThemeInteractor): ThemeViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemThemeBinding.inflate(inflater, parent, false)
                return ThemeViewHolder(binding, themeInteractor)
            }
        }

        private lateinit var theme: Theme

        init {
            itemView.setOnClickListener {
                if (!binding.actionSelect.isEnabled) {
                    themeInteractor.selectTheme(theme)
                }
            }
            binding.actionSelect.setOnClickListener {
                themeInteractor.selectTheme(theme)
            }
            binding.actionInfo.setOnClickListener {
                themeInteractor.showInfo(theme)
            }
            binding.actionOverflow.setOnClickListener {
                val wrapper = ContextThemeWrapper(it.context, R.style.Widget_AppTheme_PopupMenu)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_edit -> themeInteractor.editTheme(theme)
                        R.id.action_remove -> themeInteractor.removeTheme(theme)
                    }
                    true
                }
                popupMenu.inflate(R.menu.menu_theme)
                popupMenu.makeRightPaddingRecursively()
                popupMenu.show()
            }
        }

        override fun bind(item: Theme) {
            theme = item
            binding.itemTitle.text = item.name
            binding.itemSubtitle.text = item.author
            binding.actionOverflow.isVisible = item.isExternal

            binding.card.setCardBackgroundColor(item.colorScheme.backgroundColor)
            binding.editor.doOnPreDraw {
                binding.editor.theme = theme
                binding.editor.language = LanguageProvider.provideLanguage(".js")
            }
            binding.editor.text = CodeView.CODE_PREVIEW

            val isUltimate = itemView.context.isUltimate()
            binding.actionInfo.isEnabled = !item.isPaid || isUltimate
            binding.actionSelect.isEnabled = !item.isPaid || isUltimate
        }
    }

    interface ThemeInteractor {
        fun selectTheme(theme: Theme)
        fun removeTheme(theme: Theme)
        fun editTheme(theme: Theme)
        fun showInfo(theme: Theme)
    }
}