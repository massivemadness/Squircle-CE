package com.lightteam.modpeide.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.data.feature.language.LanguageProvider
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.modpeide.databinding.ItemThemeBinding
import com.lightteam.modpeide.domain.editor.DocumentModel
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.settings.customview.CodeView
import com.lightteam.modpeide.utils.extensions.isUltimate

class ThemeAdapter(
    private val onItemClickListener: OnItemClickListener<Theme>
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
        return ThemeViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ThemeViewHolder(
        private val binding: ItemThemeBinding,
        private val onItemClickListener: OnItemClickListener<Theme>
    ) : BaseViewHolder<Theme>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<Theme>): ThemeViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemThemeBinding.inflate(inflater, parent, false)
                return ThemeViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var theme: Theme

        init {
            binding.actionSelect.setOnClickListener {
                onItemClickListener.onClick(theme)
            }
            binding.actionInfo.setOnClickListener {
                Toast.makeText(itemView.context, theme.description, Toast.LENGTH_SHORT).show()
            }
            itemView.setOnClickListener {
                if (!binding.actionSelect.isEnabled) {
                    onItemClickListener.onClick(theme)
                }
            }
        }

        override fun bind(item: Theme) {
            theme = item
            binding.itemTitle.text = item.name
            binding.itemSubtitle.text = item.author

            binding.card.setCardBackgroundColor(item.colorScheme.backgroundColor)
            binding.editor.theme = item
            binding.editor.text = CodeView.CODE_PREVIEW

            val documentModel = DocumentModel("none", ".js", "none", 0, 0, 0, 0)
            binding.editor.language = LanguageProvider.provide(documentModel) // JavaScript

            val isUltimate = itemView.context.isUltimate()
            binding.actionInfo.isEnabled = !item.isPaid || isUltimate
            binding.actionSelect.isEnabled = !item.isPaid || isUltimate
        }
    }
}